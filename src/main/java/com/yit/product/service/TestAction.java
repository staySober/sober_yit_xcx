package com.yit.product.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.yit.common.entity.KeyValue;
import com.yit.common.utils.HttpRequester;
import com.yit.common.utils.RetryUtil;
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
import com.yit.vendor.api.VendorService;
import com.yit.vendor.entity.Vendor;
import com.yit.vendor.entity.VendorRunLog;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.xml.ws.Provider;
import java.io.PrintWriter;
import java.io.StringWriter;
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

    public void start() throws Exception {
        System.out.println("=========start========");
        long ms = new Date().getTime();

        Product p = productService.getProductFromMagentoById(4247);
        print(p);

        ms = new Date().getTime() - ms;
        System.out.println("====================");
        System.out.printf("Time: %d ms.\n", (Long)ms);
        System.out.println("=========end========");
        System.exit(0);
    }

    public void print(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJsonString = gson.toJson(obj);
        System.out.println(prettyJsonString);
    }

}
