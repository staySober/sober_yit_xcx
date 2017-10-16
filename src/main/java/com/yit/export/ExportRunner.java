package com.yit.export;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.common.utils.CollectionUtils;

import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.export.ExportService;
import com.yit.common.utils.export.ExportTable;
import com.yit.common.utils.export.ExportType;
import com.yit.common.utils.export.ExportUtil;
import com.yit.product.api.ProductService;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/7/31.
 *
 * @author sober
 * @date 2017/07/31
 *
 * 无标签价商品分类导出
 */
public class ExportRunner extends BaseTest {
    @Override
    public void run() throws Exception {
        exec();
    }

    @Autowired
    ProductService productService;

    @Autowired
    ExportUtil exportUtil;

    @Autowired
    SqlHelper sqlHelper;

    @Autowired
    ExportService exportService;

    String location = "/Users/sober/Desktop/无标签价商品";

    Map<String, List<ExportSku>> skuMaps = new HashMap<>();

    public static void main(String[] args) {
        runTest(ExportRunner.class);
    }

    private void exec() {
        String sql = "select spu.id as spuId,  "
            + " spu.original_name as originalName, "
            + " sku.id as skuId, "
            + "  sku.option_text as optionText,  "
            + "  sku.price as price, "
            + "  sku.market_price as marketPrice, "
            + "  brand.brand_name as brandName, "
            + "bd.fullname as bdName"
            + "   from  "
            + " yitiao_product_sku sku "
            + " left join yitiao_product_spu spu on sku.spu_id = spu.id "
            + " left join yitiao_brand brand on spu.brand_id = brand.entity_id "
            + " left join yitiao_product_spu_owner o on o.spu_id = spu.id "
            + " left join yitiao_admin bd on bd.id = o.bd_owner_id"
            + " where sku.is_deleted = 0 "
            + " and spu.id not in(800,18216,22290,30254,18223,27378)"
            + " and sku.market_price < sku.price "
            + " order by spu.id asc; ";
        sqlHelper.exec(sql, (row) -> {
            int spuId = row.getInt("spuId");
            String originalName = row.getString("originalName");
            int skuId = row.getInt("skuId");
            String optionText = row.getString("optionText");
            double price = row.getDouble("price");
            double marketPrice = row.getDouble("marketPrice");
            String brandName = row.getString("brandName");
            String bdName = row.getString("bdName");

            ExportSku sku = new ExportSku();
            sku.spuId = spuId;
            sku.brandName = brandName;
            sku.marketPrice = String.valueOf(marketPrice).replace(".0", "");
            sku.price = String.valueOf(price).replace(".0", "");
            sku.skuId = skuId;
            sku.optionText = optionText;
            sku.originalName = originalName;
            sku.bdName = bdName;

            List<ExportSku> exportSkus = skuMaps.get(sku.brandName);
            if (CollectionUtils.isEmpty(exportSkus)) {
                List<ExportSku> newSku = new ArrayList<ExportSku>();
                newSku.add(sku);
                skuMaps.put(sku.brandName, newSku);
            } else {
                exportSkus.add(sku);
                skuMaps.put(sku.brandName, exportSkus);
            }
        });

        for (Map.Entry<String, List<ExportSku>> thisSkuList : skuMaps.entrySet()) {
            List<ExportSku> value = thisSkuList.getValue();
            ExportTable exportTable = new ExportTable();
            value.forEach(x -> {
                exportTable.addRow((row) -> {
                    row.put("SPU ID", x.spuId);
                    row.put("商品原标题", x.originalName);
                    row.put("SKU ID", x.skuId);
                    row.put("规格", x.optionText);
                    row.put("当前售价", x.price);
                    row.put("标签价", x.marketPrice);
                    row.put("责任BD", x.bdName);
                });
            });
            exportUtil.export(exportTable, ExportType.XLS, location + "/" + thisSkuList.getKey());
        }
    }

    class ExportSku {
        int spuId;
        String originalName;
        int skuId;
        String optionText;
        String price;
        String marketPrice;
        String brandName;
        String bdName;
    }
}
