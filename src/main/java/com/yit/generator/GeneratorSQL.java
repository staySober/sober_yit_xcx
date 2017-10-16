package com.yit.generator;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.common.utils.StringUtils;

import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.import2.ImportUtil;
import com.yit.quartz.api.JobService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/9/26.
 *
 * @author sober
 * @date 2017/09/26
 */
public class GeneratorSQL {
    List<String> sqlList = new ArrayList<>();

    @Autowired
    SqlHelper sqlHelper;

    @Autowired
    JobService jobService;
    List<Integer> ids = new ArrayList<>();

    public void generator() {
        ImportUtil.doImport("/Users/sober/desktop/barcode.xls", (row) -> {
            String sku = row.get("SKUID");
            String vendorSkuCode = row.get("商品唯一编码");
            if (!StringUtils.isBlank(vendorSkuCode)) {
                String sql = "generator sql example : select * from ..........";
                sqlList.add(sql);
            }
        });
    }

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
