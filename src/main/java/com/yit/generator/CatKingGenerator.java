package com.yit.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.common.utils.StringUtils;

import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.import2.ImportUtil;
import com.yit.quartz.api.JobService;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/25.
 *
 * @author sober
 * @date 2017/08/25
 * 猫王barcode 的生成
 */
public class CatKingGenerator extends BaseTest {

    List<String> sqlList = new ArrayList<>();
    List<Integer> ids = new ArrayList<>();
    @Autowired
    SqlHelper sqlHelper;
    @Autowired
    JobService jobService;

    @Override
    public void run() throws Exception {
        reloadSpu2();
    }

    public static void main(String[] args) {
        runTest(CatKingGenerator.class);
    }

    private void generator() throws IOException {
        sqlList.add(
            "update yitiao_product_sku set vendor_sku_code = '6970022630865' where spu_id = 13413 and is_deleted = 0;"
                + "\n");
        String sql = "select tag1 as tag,  "
            + "  child_spu_id as spuId   "
            + "  from   "
            + "  yitiao_product_spu_relation";
        sqlHelper.exec(sql, (row) -> {
            String tag = row.getString("tag");
            String spuId = row.getString("spuId");
            String thisStr = "update yitiao_product_sku set vendor_sku_code = '6970022630865" + tag
                + "' where spu_id = " + spuId + " and is_deleted = 0;\n";
            sqlList.add(thisStr);
        });

        OutputStream os = new FileOutputStream(new File("/Users/sober/Desktop/猫王barcode/update_cat_king_barcode_sql"));
        for (String s : sqlList) {
            os.write(s.getBytes());
        }
        os.close();

    }

    private void reloadSpu() {
        ids.add(13413);
        String sql = "select tag1 as tag,  "
            + "  child_spu_id as spuId   "
            + "  from   "
            + "  yitiao_product_spu_relation";
        sqlHelper.exec(sql, (row) -> {
            int spuId = row.getInt("spuId");
            ids.add(spuId);
        });
        int[] ints = ids.stream().mapToInt(x -> x).toArray();
        jobService.addSpuReloadJob(ints);
    }

    //barcode
    private void generator2() throws IOException {
        ImportUtil.doImport("/Users/sober/desktop/barcode.xls", (row) -> {
            String sku = row.get("SKUID");
            String vendorSkuCode = row.get("商品唯一编码");
            if (!StringUtils.isBlank(vendorSkuCode)) {
                String sql = "generator sql example : select * from ..........";
                sqlList.add(sql);
            }
        });
        OutputStream os = new FileOutputStream(
            new File("/Users/sober/Desktop/20170901_update_product_barcode.sql"));
        for (String s : sqlList) {
            os.write(s.getBytes());
        }
        os.close();
    }

    //barcode
    private void reloadSpu2() {

        ImportUtil.doImport("/Users/sober/desktop/商品系统与成本系统价格不同(9月2号0点27分).xls", (row) -> {
            String sku = row.get("SKUID");
            /*String vendorSkuCode = row.get("商品唯一编码");
            if (!StringUtils.isBlank(vendorSkuCode)) {
                Integer skuId = Integer.valueOf(sku);*/
                ids.add(Integer.parseInt(sku));
            //}
        });
        //skuids
        List<Integer> spuIds = new ArrayList<>();
        for (Integer id : ids) {
            sqlHelper.exec("select spu_id from yitiao_product_sku where id = ? ", new Object[] {id}, (row) -> {
                int spu_id = row.getInt("spu_id");
                spuIds.add(spu_id);
            });
        }
        int[] ints = spuIds.stream().distinct().mapToInt(x -> x).toArray();

        jobService.addSpuReloadJob(ints);
        System.out.println("SUCCESS.");
    }

    //deleted
    private void generator3() throws IOException {
        ImportUtil.doImport("/Users/sober/desktop/删除部分sku.csv", (row) -> {
            String sku = row.get("skUId");
            String del = row.get("失效");
            if (!StringUtils.isBlank(del)) {
                if (Integer.valueOf(del) == 1) {

                    String sql = "update yitiao_product_sku set is_deleted = 1,on_sale = 0 where id = "
                        + sku
                        + " and is_deleted = 0;\n";
                    sqlList.add(sql);
                }
            }
        });
        OutputStream os = new FileOutputStream(
            new File("/Users/sober/Desktop/8月28导barcode/delete_product_sku.sql"));
        for (String s : sqlList) {
            os.write(s.getBytes());
        }
        os.close();
    }

    //deleted
    private void reloadSpu3() {

        ImportUtil.doImport("/Users/sober/desktop/删除部分sku.csv", (row) -> {
            String sku = row.get("skUId");
            String del = row.get("失效");
            if (!StringUtils.isBlank(del)) {
                Integer skuId = Integer.valueOf(sku);
                ids.add(skuId);

            }
        });
        //skuids
        List<Integer> spuIds = new ArrayList<>();
        for (Integer id : ids) {
            sqlHelper.exec("select spu_id from yitiao_product_sku where id = ? ", new Object[] {id}, (row) -> {
                int spu_id = row.getInt("spu_id");
                spuIds.add(spu_id);
            });
        }
        int[] ints = spuIds.stream().distinct().mapToInt(x -> x).toArray();

        jobService.addSpuReloadJob(ints);
        System.out.println("SUCCESS.");
    }

}


