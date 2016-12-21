package com.yit.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yit.common.entity.ActionWithException;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * 帮助测试类
 */
public abstract class BaseTest {

    // region 公共方法

    static Class testClass;
    public static void runTest(Class clazz) {
        testClass = clazz;
        com.alibaba.dubbo.container.Main.main(new String[0]);
    }

    public void start() throws Exception {
        if (this.getClass() != testClass) {
            return;
        }

        exec(() -> run());
        while (true) {
            Thread.sleep(100000);
        }
    }

    public abstract void run() throws Exception;

    // endregion 公共方法

    // region 帮助方法

    public void loop(int count, ActionWithException action) throws Exception {
        for (int i = 0; i < count; ++i) {
            action.invoke();
        }
    }

    public void exec(ActionWithException action) {

        System.out.println("=========start========");
        long ms = new Date().getTime();

        try {
            action.invoke();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


        ms = new Date().getTime() - ms;
        System.out.println("------");
        System.out.printf("Time: %d ms.\n", (Long)ms);
        System.out.println("=========end========");
    }

    public void print(String s, Object ... params) {
        System.out.printf(s + "\n", params);
    }

    public void print(Object obj) {

        if (obj != null && obj.getClass() == String.class) {
            System.out.println(obj);
            return;
        } else if (Exception.class.isInstance(obj)) {
            StringWriter sw = new StringWriter();
            ((Exception)obj).printStackTrace(new PrintWriter(sw));
            System.out.println(sw);
            return;
        }

        System.out.println(toJson(obj));
    }

    public String toJson(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }

    public <T> T fromJson(String filePath, Class<T> clazz) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, clazz);
        }
    }

    public byte[] readByteFromFile(String path) throws Exception {
        return Files.readAllBytes(Paths.get(path));
    }

    public void exit() {
        System.exit(0);
    }

    // endregion
}
