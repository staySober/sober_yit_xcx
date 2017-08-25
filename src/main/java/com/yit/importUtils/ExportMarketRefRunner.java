package com.yit.importUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.yit.ReadUtils;
import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.export.ExportTable;
import com.yit.common.utils.export.ExportType;
import com.yit.common.utils.export.ExportUtil;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/24.
 *
 * @author sober
 * @date 2017/08/24
 *
 *
 * spuid, skuid, 商品名，规格，标签价，参与活动（开始／即将开始），促销描述
 * 标签价 导表单
 */
public class ExportMarketRefRunner extends BaseTest {
    @Autowired
    SqlHelper sqlHelper;

    @Override
    public void run() throws Exception {
        //export1();
        export2();
    }

    private void export2() throws IOException {
        String read = ReadUtils.read(new File("/Users/sober/Desktop/第二批标签价sql/skuIds.txt"));
        String[] split = read.split(",");
        List<Integer> skuIds = Arrays.stream(split).map(x -> Integer.parseInt(x)).collect(Collectors.toList());
        //stage
        String sql = "select "
            + " sku.spu_id as spuId, "
            + " sku.id as skuId, "
            + "sku.market_price as marketPrice  "
            + " from yitiao_product_sku sku where sku.id = ?";
        ExportTable table = new ExportTable();
        for (Integer skuId : skuIds) {
            sqlHelper.exec(sql, new Object[] {skuId}, (row) -> {
                String spuId = row.getString("spuId");
                String skuId1 = row.getString("skuId");
                String marketPrice = row.getString("marketPrice");
                table.addRow(x -> {
                    x.put("spuId", spuId);
                    x.put("skuId", skuId1);
                    x.put("促销描述", marketPrice);
                });
            });
        }
        ExportUtil.export(table, ExportType.XLS, "/Users/sober/Desktop/第二批标签价sql/标签价导出2");
    }

    private void export1() throws IOException {
        String read = ReadUtils.read(new File("/Users/sober/Desktop/第二批标签价sql/skuIds.txt"));
        String[] split = read.split(",");
        List<Integer> skuIds = Arrays.stream(split).map(x -> Integer.parseInt(x)).collect(Collectors.toList());

        //查生产
        String sql = "select "
            + " sku.spu_id as spuId, "
            + " sku.id as skuId, "
            + " spu.name as name, "
            + " sku.option_text as optionText, "
            + " sku.market_price as marketPrice, "
            + " spu.promotion_desc as promotionDesc, "
            + " ( "
            + "  select "
            + "   p.name "
            + "  from yitiao_product_promotion_sku psku "
            + "  left join yitiao_product_promotion p on psku.promotion_id = p.id "
            + "     where "
            + "      psku.is_deleted = 0 "
            + "      and psku.sku_id = sku.id "
            + "      and case when p.type = 5 then psku.status = 2 else p.status = 2 end "
            + " ) as promotionName "
            + "from yitiao_product_sku sku "
            + "left join yitiao_product_spu spu on sku.spu_id = spu.id "
            + "where  "
            + " sku.id = ?";
        ExportTable table = new ExportTable();
        for (Integer skuId : skuIds) {
            sqlHelper.exec(sql, new Object[] {skuId}, (row) -> {
                String spuId = row.getString("spuId");
                String skuId1 = row.getString("skuId");
                String name = row.getString("name");
                String optionText = row.getString("optionText");
                String promotionDesc = row.getString("promotionDesc");
                String promotionName = row.getString("promotionName");
                table.addRow(x -> {
                    x.put("spuId", spuId);
                    x.put("skuId", skuId1);
                    x.put("商品名", name);
                    x.put("规格", optionText);
                    x.put("参与活动", promotionName);
                    x.put("促销描述", promotionDesc);
                });
            });
        }
        ExportUtil.export(table, ExportType.XLS, "/Users/sober/Desktop/第二批标签价sql/标签价导出");
    }

    public static void main(String[] args) {
        runTest(ExportMarketRefRunner.class);
    }
}
