package com.yit.product.service;

import com.yit.fcategory.api.FCategoryService;
import com.yit.fcategory.entity.FCategoryInfo;
import com.yit.keyvalue.api.KeyValueService;
import com.yit.product.api.CategoryService;
import com.yit.product.api.ProductService;
import com.yit.vendor.api.VendorService;
import com.yit.vendor.entity.Vendor;

import javax.annotation.Resource;


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

    public void start() throws Exception {
        System.out.println("=========start========");

        test2();

        //String value = keyValueService.get("");
        //System.out.println(value);

        System.out.println("=========end========");
        System.exit(0);
    }

    public void test1() {
        while (true) {
            int id = vendorService.createVendor(new Vendor());
            Vendor v = vendorService.getVendorById(id);
            vendorService.updateVendor(v);
            System.out.println(v);
        }
    }

    public void test2() {
        FCategoryInfo item = fcategoryService.getFCategoryInfo(1, 1);

        System.out.println(item);
    }

}
