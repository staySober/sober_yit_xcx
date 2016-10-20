package com.yit.product.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.yit.announcement.api.AnnouncementService;
import com.yit.announcement.entity.Announcement;
import com.yit.common.utils.*;
import com.yit.common.utils.cache.CacheService;
import com.yit.common.utils.disconf.DisconfUtil;
import com.yit.fcategory.api.FCategoryService;
import com.yit.fcategory.entity.FCategoryInfo;
import com.yit.file.api.FileService;
import com.yit.keyvalue.api.KeyValueService;
import com.yit.mail.api.EmailService;
import com.yit.mail.entity.MailInfo;
import com.yit.order.api.OrderService;
import com.yit.order.entity.OrderBriefInfo;
import com.yit.product.api.BrandService;
import com.yit.product.api.CategoryService;
import com.yit.product.api.ProductService;
import com.yit.product.entity.Attribute;
import com.yit.product.entity.CategoryInfo;
import com.yit.product.entity.Product;
import com.yit.util.StringUtil;
import com.yit.vendor.api.VendorService;
import com.yit.vendor.entity.Vendor;
import com.yit.vendor.entity.VendorRunLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.xml.ws.Provider;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;


/**
 * Created by reid.zeng on 8/1/16.
 */
public class TestAction {

    public static void main(String[] args) throws Exception{
        com.alibaba.dubbo.container.Main.main(args);
    }

    // region Services

    @Resource
    private VendorService vendorService;

    @Resource
    private ProductService productService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private FCategoryService fcategoryService;

    @Resource
    private KeyValueService keyValueService;

    @Resource
    private FileService fileService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AnnouncementService announcementService;

    // endregion Services

    public void start() throws Exception {

        exec(() -> run());
        //System.exit(0);

    }

    public void run() throws Exception {


        // File service
//        String url = fileService.upload("test", new byte[0], "/测试2-配送单-20123.xls");
//        System.out.println(url);


        //Object result = categoryService.getCategoryInfo(774, 20);
        //print(result);


//        List<VendorRunLog> list = vendorService.getVendorRunLogList(0, null, null, "2016-08-25 00:00:00", "2016-08-25 00:00:00");
//        System.out.println(list);
//
//        String[] s = getQueryDate();
//        System.out.println(s[0]);
//        System.out.println(s[1]);

//        Product p = productService.getProductById(0);
//        for (Product.SKU sku : p.skuInfo.skus) {
//            System.out.println(sku.code + ": " + p.getOptionsText(sku));
//        }

        //System.out.println(p);

//        try {
//
//            RetryUtil.retry(() -> {
//                System.out.println("Test");
//            });
//
//
//        } catch (Exception ex) {
//            StringWriter sw = new StringWriter();
//            ex.printStackTrace(new PrintWriter(sw));
//            System.out.println(sw);
//        }


//        String name = brandService.getName(-1);
//        System.out.println(name);

//        List<KeyValue> list = brandService.getBrandList();
//        System.out.println(list);

//        Date date = new Date(1472573400000L);
//        System.out.println(date);

//        FileService.PageResult result = fileService.test();
//        System.out.println(result);

//        keyValueService.put("test2", "23", "hello3_hello");
//        String result = keyValueService.get("test2", "23");
//        print(result);

//        String value = keyValueService.get("product", "1");
//        System.out.println(value);


//        Product p = productService.getProductById(5315);
//
//        p.basicInfo = null;
//        p.textInfo = null;
//        p.imageInfo = null;
//        p.saleInfo = null;
//        p.skuInfo = null;
//        p.extraAttributes = null;
//
//        for (int i = 0; i < 10; ++i) {
//            exec(() -> {
//                productService.updateProduct(p, "", 0);
//            });
//        }





/*
        List<Integer> allIds = productService.getProductIdsForTransfer();
        for (int id : allIds) {

            exec(() -> {
                try {
                    Product p = productService.getProductFromMagentoById(id);
                    productService.putProduct(p);
                } catch (Exception ex) {
                    print(ex);
                }
            });

        }
*/

//        Product p = productService.getProductFromMagentoById(2);
//        productService.putProduct(p);

        //print(p);


//        Product p = new Product();
//        p.basicInfo = new Product.BasicInfo();
//        p.basicInfo.brandId = 97;
//        p.basicInfo.channelId = 104;
//        p.basicInfo.categoryId = 353;
//        productService.createProduct(p, "Reid", 0);



        //productService.putProduct(p);
        //p = productService.getProductById(p.id);
        //print(p)

//        List<com.yit.vendor.entity.ChannelInfo> list = vendorService.getChannelList();
//        print(list);

        /*
        Product p = new Product();
        p.basicInfo = new Product.BasicInfo();
        p.basicInfo.brandId = 97;
        p.basicInfo.channelId = 104;
        p.basicInfo.categoryId = 353;
        productService.putProduct(p);
        */

        //Product p = fromJson("/Users/reid.zeng/Desktop/yit/gitlab/api_check/product_4247.json", Product.class);

/*
        while (true) {
            exec(() -> {
                Product p = productService.getProductById(5513);

                p.textInfo = null;
                p.imageInfo = null;
                p.saleInfo = null;
                p.skuInfo = null;
                p.extraAttributes = null;
                p.basicInfo = null;
                //p.basicInfo.categoryId = 1071;
                //p.basicInfo.brandId = 97;

                productService.updateProduct(p, "", 0);
            });
            Thread.sleep(1000);
        }

*/

        //productService.setProductOnSale(6111, true, "", 0);

//        int id = 10100;
//        id++;
//        p.id = id;
//        for (Product.SKU sku : p.skuInfo.skus) {
//            id++;
//            sku.id = id;
//        }

        //print(p);
        //productService.writeProductToMagento(p);

        //productService.setProductWeight(10001, 20);

        //BigDecimal a = new BigDecimal("123.12");
        //print(a);

        //print(new Date(1473847889567L));

        //print(new Date().getTime() + 1000 * 300);

//
//        List<OrderBriefInfo> list = orderService.getOrdersByCustomerId(0);
//        printJson(list);

//        try
//        {
//            for (int i = 0; i < 1; ++i) {
//                MailInfo mail = new MailInfo();
//                mail.subject = "Test Email";
//                mail.content = "Test <b>body</b> " + UUID.randomUUID().toString();
//                mail.receivers.add("361773643@qq.com");
//                //mail.receivers.add("66324619@qq.com");
//                emailService.send(mail);
//            }
//        } catch (Exception ex) {
//            StringWriter sw = new StringWriter();
//            ex.printStackTrace(new PrintWriter(sw));
//            String msg = String.format("系统错误: %s", sw);
//            System.out.println(msg);
//        }

//        MailInfo mail = new MailInfo();
//        mail.subject = "Test Email";
//        mail.content = "Test <b>body</b> " + UUID.randomUUID().toString();
//        mail.type = MailInfo.ContentType.HTML;
//        //mail.receivers.add("361773643@qq.com");
//        mail.receivers.add("dsfsadfhiwsd@qq.com");
//        emailService.send(mail);

//        String s = new HttpRequester()
//                .setUrl("http://zwc.name/222")
//                .downloadString();
//        System.out.println(s);

//        Announcement newEntity = new Announcement();
//        //newEntity.id = 11;
//        newEntity.platform = "hh8";
//        newEntity.type = "aa8";
//        newEntity.subType = "bb8";
//        newEntity.title = "This is a title.";
//        newEntity.content = "Test content8";
//        newEntity.startTime = DateTrans.toDate("2016-09-26 14:01:45");
//        newEntity.endTime = DateTrans.toDate("2016-10-20 16:03:15");
//        newEntity.isActive = true;
//        int result = announcementService.createUpdateGlobalAnnouncement(newEntity);
//        print(result);

//        List<Announcement> list = announcementService.getCurrentGlobalAnnouncements();
//        print(list);

        //http://oss-cn-hangzhou.aliyuncs.com/yit-stage/vendor/4b2403dd-e9f9-4576-a3d7-0064021a36c3./U+-配送单-2016-10-07.xls
        //http://oss-cn-hangzhou.aliyuncs.com/yit-stage/vendor/4b2403dd-e9f9-4576-a3d7-0064021a36c3./U%2B-%E9%85%8D%E9%80%81%E5%8D%95-2016-10-07.xls
//        String result = URLEncoder.encode("test.xls", "UTF8");
//        print(result);

        //String result = fileService.upload("test", new byte[0], "/U+-配送单-2016-10-07.xls");
//        String result = fileService.uploadTo7Niu("test", readByteFromFile("/Users/reid.zeng/Downloads/106785285.jpg"), ".jpg");
//        print(result);

        //print(DateTrans.toDate("2016-10-14 15:40:00").getTime());

//        List<Integer> allIds = productService.getProductIdsForTransfer();
//        for (int id : allIds) {
//            String result = keyValueService.get("product", String.valueOf(id));
//            if (result != null && result.length() > 8 * 1024) {
//                print("product id %d:", id);
//                print(result);
//            }
//        }


        //        while (true) {
//            int id = vendorService.createVendor(new Vendor());
//            Vendor v = vendorService.getVendorById(id);
//            vendorService.updateVendor(v);
//            System.out.println(v);
//        }

//        // SDK初始化
//        QueueService queueService = new QueueService();
//        queueService.endpoint = "http://1892677884288229.mns.cn-hangzhou.aliyuncs.com/";
//        queueService.accessId = "xZNGEdvqaFkouZRX";
//        queueService.accessKey = "2l8qBZAFvzWA70xSF2rduloG5hSD0B";
//        queueService.bucketName = "test";
//
//        // 发送消息
//        {
//            TestEntity msg = new TestEntity();
//            msg.text = "hello";
//            queueService.enqueue("TestQueue2", msg);
//        }
//
//        // 订阅消息
//        {
//            queueService.listen("TestQueue2", TestEntity.class, 5, msg -> {
//                print(msg);
//            });
//        }

//        TestEntity msg = new TestEntity();
//        msg.text = "hello3";
//
//        TopicService topicService = new TopicService();
//        topicService.producerId = UUID.randomUUID().toString();
//        topicService.consumerId = "CID_ReidTest2";
//        topicService.topic = "ReidTestTopic";
//        topicService.accessKey = "xZNGEdvqaFkouZRX";
//        topicService.secretKey = "2l8qBZAFvzWA70xSF2rduloG5hSD0B";
//        topicService.onsAddress = "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet";
//
//        for (int i = 0; i < 100; ++i) {
//            msg.text = "Test" + i;
//            topicService.publish("Test1", "TestKey5", msg);
//        }


//        topicService.subscribe("Test1", TestEntity.class, 2, msg2 -> {
//            print(Thread.currentThread().getId());
//            print(msg2);
//        });

//        TestConfig  config = new TestConfig();
//        DisconfUtil.register(config);
//
//        TestConfig  config2 = new TestConfig();
//        DisconfUtil.register(config2);
//
//        TestConfig2  config3 = new TestConfig2();
//        DisconfUtil.register(config3);
//
//        print(config);
//        print(config2);
//        print(config3);

//        CacheService cacheService = new CacheService();
//
//        while (true) {
//
//            Date date = cacheService.cache("Test", "test", 10, () -> {
//                return new Date();
//            });
//            print(date);
//            Thread.sleep(1000);
//        }

        productService.setSKUOnSale(5424, false, "", 0);
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
}
