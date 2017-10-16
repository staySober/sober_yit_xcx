package com.yit.promotion;

import java.util.ArrayList;
import java.util.List;

import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.export.ExportTable;
import com.yit.common.utils.export.ExportType;
import com.yit.common.utils.export.ExportUtil;
import com.yit.promotion.api.PromotionService;
import com.yit.promotion.api.PromotionService.SaleStatus;
import com.yit.promotion.entity.SkuResultInfo;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/9/25.
 * 家装节活动价，大于 该sku最近一次限时特惠价
 */
public class ExportPromotion extends BaseTest{
    @Autowired
    PromotionService promotionService;

    @Autowired
    SqlHelper sqlHelper;

    public static void main(String[] args) {
        runTest(ExportPromotion.class);
    }
    @Override
    public void run() throws Exception {
        SkuResultInfo resultInfo = promotionService.getPromotionSkuList(15, null, null, null, null, null, null, null,
            SaleStatus.ALL, false, false, true, "Sober", 104);

        String sql = "select promotion_price2.price as price "
            + "                                 from  "
            + "                                 yitiao_product_promotion_sku psku2 "
            + "                                 left join yitiao_product_promotion p2 on psku2.promotion_id = p2.id "
            + "                                 left join yitiao_product_sku sku2 on sku2.id = psku2.sku_id         "
            + "                                 left join yitiao_price_contract_effective promotion_price2 on " 
            + "promotion_price2.sku_id = sku2.id "
            + "                                 and promotion_price2.is_deleted = 0 "
            + "                                 and promotion_price2.reference_type = 'promotion' "
            + "                                 and promotion_price2.reference_no = concat(p2.id, '_', psku2.id) "
            + "                                 where "
            + "                                      psku2.promotion_id = 3 "
            + "                                  and psku2.is_deleted = 0 "
            + "                                  and sku2.is_deleted = 0 "
            + "                                  and sku2.id = ? "
            + "                                  order by psku2.create_time desc "
            + "                                  limit 1 "
            + "      ";

        List<SkuPromotionInfo> infoList = new ArrayList<>();

        resultInfo.brandList.forEach(brand -> {
            brand.spuList.forEach(spu -> {
                spu.skuList.forEach(sku->{
                    if (sku.promotionSku != null) {
                        sqlHelper.exec(sql,new Object[]{sku.id},(row)->{
                            int oldPrice = row.getInt("price");
                            if (oldPrice > 0 && sku.promotionSku.promotionPrice.intValue() > oldPrice) {
                                SkuPromotionInfo info = new SkuPromotionInfo();
                                info.skuId = sku.id;
                                info.jiaZhuangPrice = sku.promotionSku.promotionPrice.intValue();
                                info.xianShiPrice = oldPrice;
                                info.brandName =  brand.brandName;
                                info.spuId = spu.id;
                                infoList.add(info);
                            }
                        });
                    }
                });
            });
        });

        ExportTable exportTable = new ExportTable();
        for (SkuPromotionInfo info : infoList) {
            exportTable.addRow(x->{
                x.put("SKUID",info.skuId);
                x.put("SPUID",info.spuId);
                x.put("品牌名",info.brandName);
                x.put("家装节价格",info.jiaZhuangPrice);
                x.put("限时特惠价格",info.xianShiPrice);
            });
        }

        ExportUtil.export(exportTable, ExportType.CSV,"/Users/sober/Desktop/家装节价格高于限时特惠价格导出");
        System.out.println("success");
    }

    class SkuPromotionInfo {
        public int skuId;
        public int jiaZhuangPrice;
        public int xianShiPrice;
        public int spuId;
        public String brandName;
    }
}
