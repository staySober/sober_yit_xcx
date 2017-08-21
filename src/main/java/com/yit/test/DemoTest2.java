package com.yit.test;

import com.yit.common.utils.topic.TopicService2;
import com.yit.entity.ServiceException;
import com.yit.product.api.MediaService;
import com.yit.product.api.ProductService;
import com.yit.product.entity.Product;
import com.yit.product.entity.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 秒 分  时 日 周 月（年，最后一个年份可以隐藏不写）
 */
public class DemoTest2 extends BaseTest {

    /* @Autowired
     TopicService2 topicService;

     @Autowired
     ProductService productService;*/
    @Autowired
    MediaService mediaService;


    public static void main(String[] args) throws Exception {
        runTest(DemoTest2.class);
    }

    @Override
    public void run() throws Exception {
      /*  fun3();
        fun4();
        fun3();*/
      fun1();
    }

    // region method
/*
    public void fun2() {
        topicService.subscribe("SoberTest2Topic",null,"test02", 1, (s) -> {
            System.out.println("接收到topic : " + s);
        });
    }

    public void fun3() {
        List<Integer> spuIds = new ArrayList<>();
        spuIds.add(100);
        List<ProductInfo> productInfo = productService.getProductInfo(spuIds);
        System.out.println(productInfo);

    }

    public void fun4() throws ServiceException {
        Product product = productService.getProductById(100);
        product.textInfo.tagList.removeAll(product.textInfo.tagList);
        productService.updateProduct(product,"sober",114);

    }*/

    // endregion method

    public void fun1() {
        String s = mediaService.processSegment("https://asset.yit.com/media/59081b44-e84f-4683-a1ea-7e64977c2727.seg");
        System.out.println(s);
    }
}