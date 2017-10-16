package com.yit.export;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.yit.ReadUtils;
import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.export.ExportTable;
import com.yit.common.utils.export.ExportType;
import com.yit.common.utils.export.ExportUtil;
import com.yit.price.api.inner.InnerPriceService;
import com.yit.price.entity.ActivityPriceConfigResponse;
import com.yit.price.entity.SkuIdParam;
import com.yit.promotion.entity.Promotion;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/26.
 * @author sober
 *
 * 8月份以后的活动提报, 没有活动价格配置方案的SKU列表
 */
public class ExportRunner5 extends BaseTest {
    @Override
    public void run() throws Exception {
        export();
    }

    public static void main(String[] args) {
        runTest(ExportRunner5.class);
    }

    @Autowired
    SqlHelper sqlHelper;
    @Autowired
    InnerPriceService innerPriceService;

    //do export
    private void export() throws IOException {
        List<PromotionInfo> promotionInfos = new ArrayList<>();
        String sql = ReadUtils.read(new File("sql/query8promotion.sql"));
        sqlHelper.exec(sql, (row) -> {
            String referenceType = row.getString("referenceType");
            String referenceNo = row.getString("referenceNo");
            String skuId = row.getString("skuId");
            String promotionId = row.getString("promotionId");
            String price = row.getString("price");
            PromotionInfo info = new PromotionInfo();
            info.price = price;
            info.referenceNo = referenceNo;
            info.referenceType = referenceType;
            info.skuId = skuId;
            info.promotionId = promotionId;
            promotionInfos.add(info);
        });

        ExportTable exportTable = new ExportTable();
        //result
        List<VerifyResult> results = new ArrayList<>();

        //接口超时
        for (PromotionInfo info : promotionInfos) {
            List<SkuIdParam> skuIdParams = new ArrayList<>();
            SkuIdParam skuIdParam = new SkuIdParam();
            skuIdParam.referenceNo = info.referenceNo;
            skuIdParam.referenceType = info.referenceType;
            skuIdParam.skuId = Integer.valueOf(info.skuId);
            skuIdParams.add(skuIdParam);

            //价格配置集合
            List<ActivityPriceConfigResponse> priceSchemeBySkuIds = innerPriceService.getPriceSchemeBySkuIds(
                skuIdParams);

            if (priceSchemeBySkuIds == null) {
                //没查到对应的活动价格配置
                VerifyResult result = new VerifyResult();
                result.reason = "未查到活动价格配置";
                result.skuId = Integer.valueOf(info.skuId);
                result.promotionId = Integer.valueOf(info.promotionId);
                results.add(result);
            }
            //this response
            ActivityPriceConfigResponse response = priceSchemeBySkuIds.get(0);
            //获取到对应的价格配置 校验价格是否和活动提报一致
            if (Double.valueOf(info.price) != Double.valueOf(response.price)) {
                VerifyResult result = new VerifyResult();
                result.reason = "活动价格配置不匹配";
                result.skuId = Integer.valueOf(info.skuId);
                result.promotionId = Integer.valueOf(info.promotionId);
                results.add(result);
            }

        }

        String sql2 = ReadUtils.read(new File("sql/querySkuPromotionInfo.sql"));
        results.forEach(x -> {
            sqlHelper.exec(sql2, new Object[] {x.skuId, x.promotionId}, (row) -> {
                String bdName = row.getString("bdName");
                String spuId = row.getString("spuId");
                String skuId = row.getString("skuId");
                String name = row.getString("name");
                String startTime = row.getString("startTime");
                String endTime = row.getString("endTime");
                String status = row.getString("status");
                exportTable.addRow(k -> {
                    k.put("spuId", spuId);
                    k.put("skuId", skuId);
                    k.put("活动名称", name);
                    k.put("开始时间", startTime);
                    k.put("结束时间", endTime);
                    k.put("活动状态", Promotion.Status.getStatus(Integer.valueOf(status)));
                    k.put("所属BD", bdName);
                    k.put("导出原因", x.reason);
                });
            });
        });

        ExportUtil.export(exportTable, ExportType.CSV, "/Users/sober/Desktop/八月份后提报无活动配置/无活动配置列表");

    }

    class PromotionInfo {
        String referenceType;
        String referenceNo;
        String skuId;
        String promotionId;
        String price;
    }

    class VerifyResult {
        String reason;
        int skuId;
        int promotionId;
    }
}
