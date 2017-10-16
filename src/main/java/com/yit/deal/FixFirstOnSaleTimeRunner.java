package com.yit.deal;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;

import com.yit.common.utils.SqlHelper;
import com.yit.product.entity.Product;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *  修复firstOnSaleTime为null的数据
 */
public class FixFirstOnSaleTimeRunner extends BaseTest {

    List<Integer> spuIds = new ArrayList<>();

    Map<Integer,Date> spuWithFirstTime = new HashMap<>();

    List<String> fixSQL = new ArrayList<>();
    @Autowired
    SqlHelper sqlHelper;

    @Override
    public void run() throws Exception {
            invoke();

    }

    public static void main(String[] args) {
        runTest(FixFirstOnSaleTimeRunner.class);
    }

    private void invoke() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String querySpu = "select id from yitiao_product_spu where first_on_sale_time is null and on_sale = 1";
        sqlHelper.exec(querySpu,(row)->{
            spuIds.add(row.getInt("id"));
        });

        String queryAudit = "select body,created_time from yitiao_audit where main_type = 'product' and sub_type = ? order by created_time asc;";
        for (Integer spuId : spuIds) {
             sqlHelper.exec(queryAudit, new Object[]{spuId}, (row)->{
                 String productJSON = row.getString("body");
                 Timestamp created_time = row.getTimestamp("created_time");
                 Date date = new Date(created_time.getTime());
                 Product product = JSON.parseObject(productJSON, Product.class);
                 if (product.saleInfo.onSale) {
                     spuWithFirstTime.put( spuId,date);
                     return;
                 }
             });
        }

        for (Entry<Integer, Date> entry : spuWithFirstTime.entrySet()) {
            Integer spuId = entry.getKey();
            Date time = entry.getValue();
            String date = dateFormat.format(time);
            String updateProductSql= "update yitiao_product_spu set first_on_sale_time = '"+date+"'  where  id = "+spuId+";\n";
            fixSQL.add(updateProductSql);
        }

        OutputStream os = new FileOutputStream("/Users/sober/Desktop/fix_first_on_sale_time.sql");
        for (String s : fixSQL) {
            os.write(s.getBytes());
        }
        os.close();
        System.out.println("generation finish");
    }
}
