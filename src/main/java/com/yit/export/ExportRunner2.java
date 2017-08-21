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
 * 价格成本数据导出
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
       /* removeSpuIdList = getSpuSaleInfo();
        removeSkuIdList = getRemoveSkuIdList2();*/
        //removeSkuIdList = getRemoveSkuIdList();
        //alreadyImportSkuList = getAlreadyImportSkuList();
        doExport2();
        System.out.println("=========================SUCCESS=========================");
    }

    public static void main(String[] args) {
        runTest(ExportRunner2.class);
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
     * 供应商名称
     * 合同主体名称
     * 合同号
     * SKU ID
     * 负责BD
     * 商品编码Bracode
     * 商品名&规格
     * 厂商指导价
     * 日常售价
     * 日常结算价格/结算扣点(%)
     */
    public void doExport3() {
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
             /*" where spu.id not in(?) and sku.id not in (?) "*/
            + " where spu.name not like '%测试%' "
            + "order by spu.id, sku.id";

        //剔除掉已经导入的一部分
        sqlHelper.exec(sql, (row) -> {
            String 供应商名称 = row.getString("供应商名称");
            String 合同抬头 = row.getString("合同抬头");
            int sku_id = row.getInt("SKU_ID");
            String 所属bd = row.getString("所属bd");
            String 供应商SKU = row.getString("供应商SKU");
            String 商品名规格 = row.getString("商品名&规格");
            String 厂商指导价 = row.getString("标签价");
            int SPUID = row.getInt("SPU_ID");
            SPUInfo info = new SPUInfo();
            info.spuId = SPUID;
            info.productNameAndOption = 商品名规格;
            info.marketPrice = 厂商指导价;
            info.supplierName = 供应商名称;
            info.contractHead = 合同抬头;
            info.skUId = sku_id;
            info.bdName = 所属bd;
            info.vendorSKU = 供应商SKU;
            sourceList.add(info);
        });


        List<SPUInfo> newSourceList = sourceList.stream()
                                                .filter(spuInfo -> !removeSpuIdList.contains(spuInfo.spuId))
                                                .filter(spuInfo -> !removeSkuIdList.contains(spuInfo.skUId))
                                                .collect(Collectors.toList());

        ExportTable exportTable = new ExportTable();
        for (SPUInfo spuInfo : newSourceList) {
            exportTable.addRow(x -> {
                //do someThing
                x.put("供应商名称", spuInfo.supplierName);
                x.put("合同主体名称", spuInfo.contractHead);
                x.put("合同号", "");
                x.put("SKU_ID", spuInfo.skUId);
                x.put("负责BD", spuInfo.bdName);
                x.put("商品编码Barcode", spuInfo.vendorSKU);
                x.put("商品名&规格", spuInfo.productNameAndOption);
                x.put("厂商指导价", spuInfo.marketPrice);
                x.put("日常售价", "");
                x.put("日常结算价格/结算扣点(%)", "");
            });
        }

        exportUtil.export(exportTable, ExportType.XLS, "/Users/sober/Desktop/无日常销售方案列表");

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
    /**
     * 成本数据导出
     */
    public void doExport() {
        removeSkuIdList.addAll(alreadyImportSkuList);

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
            + "left join yitiao_product_sku sku on sku.spu_id = spu.id and sku.is_deleted = 0 "
            + "left join yitiao_supplier supplier on supplier.id = channel.supplier_id "
            + "left join yitiao_product_spu_owner o on o.spu_id = spu.id "
            + "left join yitiao_admin bd on bd.id = o.bd_owner_id "
            + " where spu.id not in(?) and sku.id not in (?) "
            + "order by spu.id, sku.id";
        ExportTable exportTable = new ExportTable();

        sqlHelper.exec(sql, new Object[] {removeSpuIdList, removeSkuIdList}, (row) -> {
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
            .filter(
                spuInfo -> !removeSkuIdList.contains(spuInfo.skUId)).collect(Collectors.toList());

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
        exportUtil.export(exportTable, ExportType.CSV, "/Users/sober/Desktop/成本导数据2");
    }

    public void doExport2() throws IOException {
        //removeSkuIdList.addAll(alreadyImportSkuList);
        /*

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
            + "left join yitiao_product_sku sku on sku.spu_id = spu.id "
            + "left join yitiao_supplier supplier on supplier.id = channel.supplier_id "
            + "left join yitiao_product_spu_owner o on o.spu_id = spu.id "
            + "left join yitiao_admin bd on bd.id = o.bd_owner_id "
            + " where sku.is_deleted = 0 and  sku.on_sale = 1 "
            + "order by spu.id, sku.id";
        */

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
            + " and bd.fullname in ('王丽媛','徐陈宠','孙沁卉','吴梦舒','程振尧') "
            + " and sku.id in (select "
            + "distinct (sku_id) "
            + "from yitiao_product_promotion_sku psku "
            + "left join yitiao_product_promotion p on p.id = psku.promotion_id  "
            + "where psku.is_deleted = 0 "
            + "and case when p.type = 5 then !(psku.end_time < '2017-08-01 00:00:00' or psku.start_time  "
            + ">'2017-10-01 23:59:59')  "
            + "         else !(p.end_time < '2017-08-01 00:00:00' or p.start_time > '2017-10-01 23:59:59') end) "
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

        sortData();

        for (Entry<String, List<SPUInfo>> stringListEntry : exportDatas.entrySet()) {
            String key = stringListEntry.getKey();
            List<SPUInfo> value = stringListEntry.getValue();
            EXCELUtils.doExport(value, key);
            System.out.println("doExport file name ------>" + key);
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

    public class SPUInfo {
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
