package com.yit.export;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.export.ExportTable;
import com.yit.common.utils.export.ExportType;
import com.yit.common.utils.export.ExportUtil;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 同一供应商下 barcode 重复使用的数据拉取
 */
public class ExportRunner7 extends BaseTest {

    @Autowired
    SqlHelper sqlHelper;

    List<BarcodeEntity> barcodeEntities = new ArrayList<>();

    @Override
    public void run() throws Exception {
        sqlHelper.exec(sql, (row) -> {
            int supplierID = row.getInt("供应商id");
            String barcode = row.getString("barcode");
            BarcodeEntity entity = new BarcodeEntity();
            entity.barcode = barcode;
            entity.supplierId = supplierID;
            barcodeEntities.add(entity);
        });

        ExportTable table = new ExportTable();
        for (BarcodeEntity entity : barcodeEntities) {
            sqlHelper.exec(sql2, new Object[] {entity.barcode, entity.supplierId}, (row) -> {
                table.addRow(x -> {
                    try {
                        x.put("供应商ID", row.getString("供应商ID"));
                        x.put("供应商名称", row.getString("供应商名称"));
                        x.put("品牌名称",row.getString("brandName"));
                        x.put("SPUID",row.getString("SPUID"));
                        x.put("SKUID",row.getString("SKUID"));
                        x.put("商品名&规格",row.getString("商品名&规格"));
                        x.put("barcode",row.getString("barcode"));
                        x.put("SKU上下架状态",row.getString("SKU状态"));
                        x.put("所属BD",row.getString("所属bd"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            });
        }

        ExportUtil.export(table, ExportType.CSV,"/Users/sober/Desktop/barcode重复的sku");
        System.out.println("finish!");

    }

    public static void main(String[] args) {
        runTest(ExportRunner7.class);
    }

    String sql = "select "
        + "supplier.id as 供应商id, "
        + "spu.id as SPUID, "
        + "sku.id as SKUID, "
        + "sku.vendor_sku_code as barcode, "
        + "count(*) "
        + " from yitiao_product_sku sku "
        + "      left join yitiao_product_spu spu on spu.id = sku.spu_id "
        + "     left join yitiao_brand brand on brand.entity_id = spu.brand_id "
        + "     left join yitiao_product_spu_channel spu_channel on spu_channel.spu_id = spu.id "
        + "     left join yitiao_vendor channel on channel.entity_id = spu_channel.channel_id "
        + "     left join yitiao_supplier supplier on supplier.id = channel.supplier_id "
        + "where vendor_sku_code is not NULL "
        + "    and vendor_sku_code != '' "
        + "    and sku.is_deleted = 0      "
        + "      and spu.id not in (select spu_id from yitiao_product_test_spu_id) "
        + "group by "
        + "    supplier.id, "
        + "    vendor_sku_code "
        + "having count(*) > 2 "
        + "order by spu.id ";

    String sql2 = "select "
        + "supplier.id as 供应商ID, "
        + "supplier.name as 供应商名称, "
        + "spu.id as SPUID, "
        + "sku.vendor_sku_code as barcode, "
        + "sku.id as SKUID, "
        + "case when sku.on_sale = 1 then '上架' else '下架' end as SKU状态, "
        + " bd.fullname as 所属bd,"
        + " concat(replace(spu.original_name,'', ''), '&', sku.option_text) as `商品名&规格`,"
        + "brand.brand_name as brandName "
        + " from yitiao_product_sku sku "
        + "     left join yitiao_product_spu spu on spu.id = sku.spu_id "
        + "    left join yitiao_brand brand on brand.entity_id = spu.brand_id "
        + "    left join yitiao_product_spu_channel spu_channel on spu_channel.spu_id = spu.id "
        + "    left join yitiao_vendor channel on channel.entity_id = spu_channel.channel_id "
        + "    left join yitiao_supplier supplier on supplier.id = channel.supplier_id "
        +"     left join yitiao_product_spu_owner o on o.spu_id = spu.id  "
        + "    left join yitiao_admin bd on bd.id = o.bd_owner_id  "
        + "where vendor_sku_code = ? "
        + "   and supplier.id = ? "
        + "   and sku.is_deleted = 0";

    class BarcodeEntity {
        int supplierId;
        String barcode;
    }
}
