package com.yit.promotion;

import java.util.ArrayList;
import java.util.List;

import com.yit.common.utils.SqlHelper;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/18.
 *
 * 为了兼容成本结算 活动提报需要作出修改  并迁移已经提报过的数据
 */
public class DealPromotionPriceRunner extends BaseTest {

    private static final String START_TIME = "2017-08-01 00:00:00";

    private static final String END_TIME = "2017-08-31 23:59:59";

    @Autowired
    private SqlHelper sqlHelper;

    List<PromotonInfo> promotonInfos = new ArrayList<>();

    public static void main(String[] args) {
        runTest(DealPromotionPriceRunner.class);
    }

    @Override
    public void run() throws Exception {
        //prepare data
        prepareData();

    }

    //获取规定时间的提报数据
    private void prepareData() {
        String sql = "select "
            + "        p.id as promotionId, "
            + "        p.name as promotionName, "
            + "        psku.sku_id as skuId, "
            + "        psku.promotion_price as price "
            + " from "
            + "    yitiao_product_promotion_sku psku "
            + "left join yitiao_product_promotion p on p.id = psku.promotion_id "
            + "where       "
            + "        psku.is_deleted = 0 "
            + "        and case when p.type = 5 then !(psku.start_time < ' " + START_TIME + " ' or psku.end_time "
            + ">' " + END_TIME + " ') "
            + "        else !(p.start_time < '" + START_TIME + "' or p.end_time > '" + END_TIME + "') end";

        sqlHelper.exec(sql, (row) -> {
            int promotionId = row.getInt("promotionId");
            int skuId = row.getInt("skuId");
            String promotionName = row.getString("promotionName");
            String price = row.getString("price");

            PromotonInfo info = new PromotonInfo();
            info.price = price;
            info.promotionId = promotionId;
            info.promotionName = promotionName;
            info.skuId = skuId;

            promotonInfos.add(info);
        });
    }

    public class PromotonInfo {

        public int promotionId;

        public String promotionName;

        public int skuId;

        public String price;
    }
}

