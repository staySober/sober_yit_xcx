package com.yit.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.export.ExportTable;
import com.yit.common.utils.export.ExportType;
import com.yit.common.utils.export.ExportUtil;

import com.yit.product.entity.Product;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/24.
 *
 * 查询没有日常销售方案的产品数据
 */
public class ExportRunner3 extends BaseTest {
    @Autowired
    SqlHelper sqlHelper;
    List<Integer> removeSpuList = new ArrayList<>();
    List<Integer> removeSkuList = new ArrayList<>();
    List<SPUInfo> sourceList = new ArrayList<>();
    Map<String, List<ExportRunner3.SPUInfo>> exportDatas = new HashMap<>();

    @Override
    public void run() throws Exception {
        removeSpuList = getSpuSaleInfo();
        removeSkuList = getRemoveSkuIdList2();
        export();
    }

    //不分组
    private void export() throws IOException {

        String sql = "select "
            + "    channel.vendor_name as 发货渠道, "
            + "    ifnull(supplier.company_name, '') as 合同抬头, "
            + "    supplier.name as 供应商名称, "
            + "    brand.brand_name as 品牌, "
            + "    replace(spu.original_name,'', '') as 原商品名, "
            + "    spu.id as SPU_ID, "
            + "    sku.id as SKU_ID, "
            + "    sku.option_text as SKU规格, "
            + "    concat(replace(spu.original_name,'', ''), '&', sku.option_text) as `商品名&规格`, "
            + "    sku.vendor_sku_code as 供应商SKU, "
            + "    cast(sku.price as signed integer) as SKU价格, "
            + "    cast(sku.market_price as signed integer) as 标签价, "
            + "    bd.fullname as 所属bd "
            + "from yitiao_product_spu spu "
            + "left join yitiao_brand brand on brand.entity_id = spu.brand_id "
            + "left join yitiao_product_spu_channel spu_channel on spu_channel.spu_id = spu.id "
            + "left join yitiao_vendor channel on channel.entity_id = spu_channel.channel_id "
            + "inner join yitiao_product_sku sku on sku.spu_id = spu.id and sku.is_deleted = 0 "
            + "left join yitiao_supplier supplier on supplier.id = channel.supplier_id "
            + "left join yitiao_product_spu_owner o on o.spu_id = spu.id "
            + "left join yitiao_admin bd on bd.id = o.bd_owner_id "
            + "where  "
            + " spu.id not in(800,8530,18216,22290,30254,18223,27378) "
            + "order by spu.id, sku.id";

        sqlHelper.exec(sql, (row) -> {
            SPUInfo spuInfo = new SPUInfo();
            spuInfo.channel = row.getString("发货渠道");
            spuInfo.contractHead = row.getString("合同抬头");
            spuInfo.supplierName = row.getString("供应商名称");
            spuInfo.brandName = row.getString("品牌");
            spuInfo.originalName = row.getString("原商品名");
            spuInfo.spuId = row.getInt("SPU_ID");
            spuInfo.skUId = row.getInt("SkU_ID");
            spuInfo.skuOption = row.getString("SKU规格");
            spuInfo.productNameAndOption = row.getString("商品名&规格");
            spuInfo.vendorSKU = row.getString("供应商SKU");
            spuInfo.skuPrice = row.getString("SKU价格");
            spuInfo.marketPrice = row.getString("标签价");
            spuInfo.bdName = row.getString("所属bd");
            sourceList.add(spuInfo);
        });

        sourceList = sourceList.stream().filter(spuInfo -> !removeSpuList.contains(spuInfo.spuId))
            .filter(spuInfo -> !removeSkuList.contains(spuInfo.skUId))
            .collect(Collectors.toList());

        ExportTable exportTable = new ExportTable();
        for (SPUInfo info : sourceList) {
            exportTable.addRow(x -> {
                x.put("发货渠道", info.channel);
                x.put("合同抬头", info.contractHead);
                x.put("供应商名称", info.supplierName);
                x.put("品牌", info.brandName);
                x.put("原商品名", info.originalName);
                x.put("SPU_ID", info.spuId);
                x.put("SKU_ID", info.skUId);
                x.put("SKU规格", info.skuOption);
                x.put("商品名&规格", info.productNameAndOption);
                x.put("供应商SKU", info.vendorSKU);
                x.put("SKU价格", info.skuPrice);
                x.put("标签价", info.marketPrice);
                x.put("所属bd", info.bdName);
            });
        }
        ExportUtil.export(exportTable, ExportType.XLS, "/Users/sober/Desktop/8月28导出/无日常销售方案商品2");

    }


    public static void main(String[] args) {
        runTest(ExportRunner3.class);
    }

    //查询日常销售方案的数据 剔除这些sku
    public List<Integer> getRemoveSkuIdList2() {
        List<Integer> removeSkuList = new ArrayList<>();
        String sql = "SELECT\n"
            + " t1.sku_id as id\n"
            + "FROM\n"
            + " yitiao_price_contract_effective t1,\n"
            + " yitiao_finance_contract_detail t2,\n"
            + " yitiao_finance_price_scheme t3,\n"
            + "    yitiao_finance_contract t4\n"
            + "WHERE\n"
            + " t1.contract_detail_id = t2.id\n"
            + "AND t2.scheme_id = t3.id\n"
            + "and t3.contract_id = t4.id\n"
            + "AND t3.scheme_type = 'DAILY_SALE'\n"
            + "AND t2.is_deleted = 0\n"
            + "and t4.is_deleted = 0\n"
            + "AND t3.is_deleted = 0\n"
            + "and t4.end_time > DATE_ADD(NOW(),INTERVAL 1 day);";

        sqlHelper.exec(sql, (row) -> {
            int id = row.getInt("id");
            removeSkuList.add(id);
        });

        System.out.println("剔除有日常销售方案的sku");
        return removeSkuList;
    }

    /**
     * SPU下架超过1个月的剔除  八月份之前下架的SPU
     */
    public List<Integer> getSpuSaleInfo() {
        List<Integer> extraList = new ArrayList<>();

        List<Integer> idList = new ArrayList<>();
        String queryId = "\t\t\t\t\n"
            + "select id from yitiao_product_spu spu where\n"
            + "\t\t\t\t created_time < '2017-08-01 00:00:00' \n"
            + "\t\t\t\t and (on_sale = 0 or not exists(select * from yitiao_product_sku where on_sale = 1 and "
            + "is_deleted = 0 and spu_id = spu.id))";

        sqlHelper.exec(queryId, (row) -> {
            int id = row.getInt("id");
            idList.add(id);
        });

        for (Integer spuId : idList) {
            String queryAudit = "select body from yitiao_audit where "
                + "main_type = 'product' and  sub_type = ? and created_time >= '2017-08-01 00:00:00' order by "
                + "sub_type, created_time";
            final boolean[] outSale = {false};
            sqlHelper.exec(queryAudit, new Object[] {spuId}, (row) -> {
                String p = row.getString("body");
                Product product = JSON.parseObject(p, Product.class);
                if (product.saleInfo != null && product.saleInfo.onSale == true) {
                    if (product.skuInfo.skus.stream().filter(sku -> sku.saleInfo.onSale == true).findAny()
                        .isPresent()) {
                        outSale[0] = true;
                    }
                }
            });

            if (outSale[0] == true) {
                //doNothing
            } else {
                extraList.add(spuId);
            }
            //重置flag
            outSale[0] = false;
        }
        System.out.println("SPU下架超过1个月的剔除");
        return extraList;

    }

    private static class SPUInfo {
        public String channel;
        public String contractHead;
        public String supplierName;
        public String brandName;
        public String originalName;
        public int spuId;
        public int skUId;
        public String skuOption;
        public String productNameAndOption;
        public String vendorSKU;
        public String skuPrice;
        public String marketPrice;
        public String bdName;
    }

    //按照bd名称和供应商分组
    public void sortData() {
        sourceList.forEach(x -> {
            List<ExportRunner3.SPUInfo> spuInfos = exportDatas.get(x.bdName + "_" + x.supplierName);
            if (spuInfos == null) {
                spuInfos = new ArrayList<>();
                spuInfos.add(x);
                exportDatas.put(x.bdName + "_" + x.supplierName, spuInfos);
            } else {
                spuInfos.add(x);
                exportDatas.put(x.bdName + "_" + x.supplierName, spuInfos);
            }
        });
    }
}
