package com.yit.promotion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yit.common.utils.SqlHelper;
import com.yit.finance.api.boss.ContractService;
import com.yit.price.api.inner.InnerPriceService;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/18.
 *
 * 为了兼容成本结算 活动提报需要作出修改  并迁移已经提报过的数据
 */
public class DealPromotionPriceRunner extends BaseTest {

    private static final String START_TIME = "2017-08-01 00:00:00";

    private static final String END_TIME = "2017-10-01 23:59:59";

    @Autowired
    private SqlHelper sqlHelper;


    @Autowired
    InnerPriceService innerPriceService;

    @Autowired
    ContractService contractService;

    List<PromotonInfo> promotonInfos = new ArrayList<>();

    public static void main(String[] args) {
        runTest(DealPromotionPriceRunner.class);
    }

    @Override
    public void run() throws Exception {
        //prepare data
        prepareData();
        generatorPriceConfig();

    }

    //为每一个提报数据生成价格配置
    private void generatorPriceConfig() {
        for (PromotonInfo promotonInfo : promotonInfos) {
            //获取价格方案id
            //根据活动类型 设置价格配置
        }

    }

    //获取规定时间内有效的提报数据
    private void prepareData() {
        String sql = "select "
            + "        p.id as promotionId, "
            + "        p.name as promotionName, "
            + "        psku.sku_id as skuId, "
            + "        psku.promotion_price as price, "
            + "        case when p.type = 5 then psku.start_time else p.start_time end as startTime,"
            + "        case when p.type = 5 then psku.end_time else p.end_time end as endTime "
            + " from "
            + "    yitiao_product_promotion_sku psku "
            + "left join yitiao_product_promotion p on p.id = psku.promotion_id "
            + "where       "
            + "        psku.is_deleted = 0 "
            + "        and case when p.type = 5 then !(psku.end_time < ' " + START_TIME + " ' or psku.start_time "
            + ">' " + END_TIME + " ') "
            + "        else !(p.end_time < '" + START_TIME + "' or p.start_time > '" + END_TIME + "') end";

        sqlHelper.exec(sql, (row) -> {
            int promotionId = row.getInt("promotionId");
            int skuId = row.getInt("skuId");
            String promotionName = row.getString("promotionName");
            String price = row.getString("price");
            Date startTime = row.getDate("startTime");
            Date endTime = row.getDate("endTime");

            PromotonInfo info = new PromotonInfo();
            info.price = price;
            info.promotionId = promotionId;
            info.promotionName = promotionName;
            info.skuId = skuId;
            info.startTime = startTime;
            info.endTime = endTime;
            promotonInfos.add(info);
        });
    }

    public class PromotonInfo {

        public int promotionId;

        public String promotionName;

        public int skuId;

        public String price;

        public Date startTime;

        public Date endTime;
    }
}

