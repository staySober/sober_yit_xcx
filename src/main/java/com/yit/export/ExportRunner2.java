package com.yit.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.yit.EXCELUtils;
import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.export.ExportTable;
import com.yit.common.utils.export.ExportType;
import com.yit.common.utils.export.ExportUtil;
import com.yit.common.utils.import2.ImportUtil;
import com.yit.product.entity.Product;
import com.yit.test.BaseTest;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/9.
 *
 * @author sober
 * @date 2017/08/09
 *
 * 8月份上架或上过架的无barcode在售商品导出
 */
public class ExportRunner2 extends BaseTest {
    @Autowired
    SqlHelper sqlHelper;

    @Autowired
    SqlSessionFactory sqlSessionFactory2;

    @Autowired
    ExportUtil exportUtil;

    List<Integer> removeSpuIdList;
    List<Integer> removeSkuIdList;
    List<Integer> alreadyImportSkuList;
    List<SPUInfo> sourceList = new ArrayList<>();
    Map<String, List<SPUInfo>> exportDatas = new HashMap<>();

    @Override
    public void run() throws Exception {
        removeSpuIdList = getSpuSaleInfo();
        //removeSkuIdList = getRemoveSkuIdList2();
        //removeSkuIdList = getRemoveSkuIdList();
        //alreadyImportSkuList = getAlreadyImportSkuList();
        doExport();
        System.out.println("=========================SUCCESS=========================");
    }

    public static void main(String[] args) {
        runTest(ExportRunner2.class);
    }

    /**
     * 在售无barcode导出
     */
    public void doExport() {
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
            + " where (sku.vendor_sku_code is null or vendor_sku_code = '') "
            + " and spu.id not in(800,18216,22290,30254,18223,27378) "
            + "order by bd.fullname,supplier.company_name,brand.brand_name,spu.original_name";
        ExportTable exportTable = new ExportTable();

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

        List<SPUInfo> newSourceList = sourceList.stream().filter(spuInfo -> !removeSpuIdList.contains(spuInfo.spuId))
            .collect(Collectors.toList());

        for (SPUInfo spuInfo : newSourceList) {
            exportTable.addRow(x -> {
                x.put("发货渠道", spuInfo.channel);
                x.put("合同抬头", spuInfo.contractHead);
                x.put("供应商名称", spuInfo.supplierName);
                x.put("品牌", spuInfo.brandName);
                x.put("原商品名", spuInfo.originalName);
                x.put("spuId", spuInfo.spuId);
                x.put("skUId", spuInfo.skUId);
                x.put("sku规格", spuInfo.skuOption);
                x.put("productNameAndOption", spuInfo.productNameAndOption);
                x.put("供应商SKU", spuInfo.vendorSKU);
                x.put("sku价格", spuInfo.skuPrice);
                x.put("标签价", spuInfo.marketPrice);
                x.put("所属bd", spuInfo.bdName);
            });
        }
        exportUtil.export(exportTable, ExportType.CSV, "/Users/sober/Desktop/在售无barcode商品");
    }

    /**
     * SPU下架超过1个月的剔除  八月份之前下架的SPU
     */
    public List<Integer> getSpuSaleInfo() {
        List<Integer> extraList = new ArrayList<>();

        List<Integer> idList = new ArrayList<>();
        String queryId = " "
            + "select id from yitiao_product_spu spu where "
            + " created_time < '2017-08-01 00:00:00' "
            + " and (on_sale = 0 or not exists(select * from yitiao_product_sku where on_sale = 1 and "
            + "is_deleted = 0 and spu_id = spu.id))";

        sqlHelper.exec(queryId, (row) -> {
            int id = row.getInt("id");
            idList.add(id);
        });

        for (Integer spuId : idList) {
            String queryAudit = " select body from yitiao_audit where "
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

    /**
     * 发货时间过期(发货日期在8月1号之前)的剔除
     */
    public List<Integer> getRemoveSkuIdList() {
        List<Integer> removeSkuList = new ArrayList<>();
        String sql = "select id from yitiao_product_sku where "
            + "(option_text like '%2016%' "
            + "or option_text like '%2017年1月%' "
            + "or option_text like '%2017年2月%' "
            + "or option_text like '%2017年3月%' "
            + "or option_text like '%2017年4月%' "
            + "or option_text like '%2017年5月%' "
            + "or option_text like '%2017年6月%' "
            + "or option_text like '%2017年7月%' )"
            + "and is_deleted=0";

        sqlHelper.exec(sql, (row) -> {
            int id = row.getInt("id");
            removeSkuList.add(id);
        });

        System.out.println("发货时间过期(发货日期在8月1号之前)的剔除");
        return removeSkuList;
    }

    /**
     * 剔除已经导过的SKU
     */
    public List<Integer> getAlreadyImportSkuList() {
        List<Integer> removeSkuList = new ArrayList<>();
        String path = "/Users/sober/Desktop/已上架SKU列表-2017-07-28.csv";
        ImportUtil.doImport(path, (row) -> {
            int skuId = Integer.parseInt(row.get("SKU_ID"));
            removeSkuList.add(skuId);
        });

        System.out.println("剔除已经导过的SKU");
        return removeSkuList;
    }

    //bd 分组导出数据

    public void doExport2() throws IOException {

        String sql2 = "select "
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
            + "left join yitiao_product_sku sku on sku.spu_id = spu.id "
            + "left join yitiao_supplier supplier on supplier.id = channel.supplier_id "
            + "left join yitiao_product_spu_owner o on o.spu_id = spu.id "
            + "left join yitiao_admin bd on bd.id = o.bd_owner_id "
            + " where sku.is_deleted = 0  "
            + "and spu.id not in(800,18216,22290,30254,18223,27378)  "
            + "order by spu.id, sku.id";

        sqlHelper.exec(sql2, (row) -> {
            SPUInfo spuInfo = new SPUInfo();
            spuInfo.contractHead = row.getString("合同抬头");
            spuInfo.supplierName = row.getString("供应商名称");
            spuInfo.skUId = row.getInt("SkU_ID");
            spuInfo.productNameAndOption = row.getString("商品名&规格");
            spuInfo.vendorSKU = row.getString("供应商SKU");
            spuInfo.bdName = row.getString("所属bd");
            sourceList.add(spuInfo);
        });

        sourceList = sourceList.stream()
            .filter(spuInfo -> !removeSpuIdList.contains(spuInfo.spuId))
            .collect(Collectors.toList());

        sortData();

        for (Entry<String, List<SPUInfo>> stringListEntry : exportDatas.entrySet()) {
            EXCELUtils.doExport(stringListEntry.getValue(),stringListEntry.getKey());
        }

        System.out.println("finish");
    }

    //按照bd名称和供应商分组
    public void sortData() {
        sourceList.forEach(x -> {
            List<SPUInfo> spuInfos = exportDatas.get(x.bdName + "_" + x.supplierName);
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

    public static class SPUInfo {
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
}
