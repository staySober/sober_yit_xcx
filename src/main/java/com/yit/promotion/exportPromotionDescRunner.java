package com.yit.promotion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yit.ReadUtils;
import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.export.ExportTable;
import com.yit.common.utils.export.ExportType;
import com.yit.common.utils.export.ExportUtil;
import com.yit.common.utils.import2.ImportUtil;
import com.yit.test.BaseTest;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/24.
 *
 * @author sober
 * @date 2017/08/24
 *
 *
 *
 * spuid, skuid, 商品名，规格，标签价，参与活动（开始／即将开始），促销描述
 */
public class exportPromotionDescRunner extends BaseTest {


    @Autowired
    SqlHelper sqlHelper;

    @Override
    public void run() throws Exception {
        List<Integer> skuIdList = new ArrayList<>();
        String file = new String("/Users/reid.zeng/Downloads/参与常规大促活动sku标签价导出挑选.xlsx");

        ImportUtil.doImport(file, (row) -> {
            Integer skuId = Integer.valueOf(row.get("skuId"));
            skuIdList.add(skuId);
        });

        String sql2 = ReadUtils.read(new File("/Users/sober/Desktop/第二批标签价sql/sql.sql"));

        class Entity {
            public int count;
            public String spuId;
            public String skuId;
            public String promotionName;
            public String promotionDesc;
        }

        ExportTable table = new ExportTable();
        for (int integer : skuIdList) {
            Entity entity = new Entity();
            entity.skuId = String.valueOf(integer);

            sqlHelper.exec(sql2, new Object[] {integer}, (row) -> {
                entity.spuId = row.getString("spu_id");
                entity.skuId = row.getString("sku_id");
                entity.promotionName = row.getString("参与活动");
                entity.promotionDesc = row.getString("促销描述");
                entity.count++;
            });

            if (entity.count > 1) {
                print("SKU ID: %s 返回了多行", integer);
            }

            table.addRow((row2) -> {
                    row2.put("spu_id", entity.spuId);
                    row2.put("sku_id", entity.skuId);
                    row2.put("参与活动", entity.promotionName);
                    row2.put("促销描述", entity.promotionDesc);
                }
            );
        }

        ExportUtil.export(table, ExportType.XLS, "/Users/reid.zeng/Downloads/参与常规大促活动sku标签价导出挑选-导出");

    }

    public static void main(String[] args) {
        runTest(exportPromotionDescRunner.class);
    }
}
