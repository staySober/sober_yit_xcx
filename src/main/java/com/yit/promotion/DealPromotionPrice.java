package com.yit.promotion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.common.utils.StringUtils;

import com.yit.common.utils.import2.ImportUtil;
import com.yit.test.BaseTest;

/**
 * Created by sober on 2017/8/29.
 *
 * 修改或取消活动提报
 */
public class DealPromotionPrice extends BaseTest {

    @Override
    public void run() throws Exception {
        doExportSqlScript();
    }

    public static void main(String[] args) {
        runTest(DealPromotionPrice.class);
    }

    private void doExportSqlScript() throws IOException {
        List<String> list = new ArrayList<>();

        ImportUtil.doImport("/Users/sober/Desktop/活动价高于30天内订单最低价.xlsx", (row) -> {
            String promotionName = row.get("活动名称");
            String promotionPrice = row.get("更新活动价");
            String sku_id = row.get("SKU_ID");
            int promotionId = 0;
            if ("一条周年庆".equals(promotionName)) {
                promotionId = 13;
            }
            if ("一条酒水节".equals(promotionName)) {
                promotionId = 14;
            }

            if (StringUtils.isBlank(promotionPrice)) {
                //取消提报
                String sql = "update yitiao_product_promotion_sku set is_deleted = 1 where is_deleted = 0 and  sku_id = " + sku_id +" and promotion_id = " + promotionId +";\n";
                list.add(sql);
            } else {
                //更新价格
                String sql = "update yitiao_product_promotion_sku set promotion_price = " + promotionPrice + " where is_deleted = 0 and sku_id =" +sku_id + " and promotion_id = " + promotionId +";\n";
                list.add(sql);
            }
        });

        OutputStream os = new FileOutputStream(new File("/Users/sober/Desktop/8月29导出/20170829_update_promotion_price.sql"));
        for (String s : list) {
            os.write(s.getBytes());
        }
        os.close();

    }
}
