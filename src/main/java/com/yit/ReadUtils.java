package com.yit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created by sober on 2017/8/4.
 *
 * @author sober
 * @date 2017/08/04
 */
public class ReadUtils {

    public static String read(File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        InputStream is = new FileInputStream(file);

        byte[] read = new byte[1024];
        int len = 0;
        while((len = is.read(read)) != -1) {
            bos.write(read,0,len);
         }

        is.close();
        bos.close();
       return bos.toString();
    }

}
