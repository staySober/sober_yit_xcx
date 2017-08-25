package com.yit.promotion;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.yit.common.utils.SqlHelper;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/23.
 *
 * @author sober
 * @date 2017/08/23
 * 活动提报中“一条周年庆”中的两个品牌【简帛】、【薏凡特】下的所有商品的活动后价格刷新为现在进行中的“七夕情人节”的活动后价格
 */
public class GeneratorSqlHepler extends BaseTest {

    @Autowired
    SqlHelper sqlHelper;

    List<String> list = new ArrayList<>();
    @Override
    public void run() throws Exception {
        generator();
    }

    private void generator() throws IOException {
        String sql = "select \n"
            + "\t\t\tsku_id as skuId,\n"
            + "\t\t\toriginal_price as price\n"
            + "from\n"
            + "\t\tyitiao_product_promotion_sku psku\n"
            + "\t\tleft join yitiao_product_sku sku on sku.id = psku.sku_id\n"
            + "\t\tleft join yitiao_product_spu spu on spu.id = sku.spu_id\n"
            + "\t\tleft join yitiao_brand brand on brand.entity_id = spu.brand_id\n"
            + "\t\tWHERE \n"
            + "\t\t\t\t psku.is_deleted = 0\n"
            + "\t\t and promotion_id = 12\n"
            + "\t\t and brand.entity_id in (271,387)";
        sqlHelper.exec(sql,(row)->{
            String skuId = row.getString("skuId");
            String price = row.getString("price");
            String sqlGen = "update yitiao_product_promotion_sku set original_price = " + price + " where promotion_id = 13 and is_deleted = 0 and sku_id = " +skuId +";\n";
            list.add(sqlGen);
        });

        OutputStream os = new FileOutputStream("/Users/sober/Desktop/update_promotion_original_price.sql");
        for (String s : list) {
            os.write(s.getBytes());
        }
        os.close();
    }

    public static void main(String[] args) {
        runTest(GeneratorSqlHepler.class);
    }


}

