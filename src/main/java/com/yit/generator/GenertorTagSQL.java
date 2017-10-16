package com.yit.generator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.yit.common.utils.SqlHelper;
import com.yit.quartz.api.JobService;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 一条家装节刷TAG
 */
public class GenertorTagSQL extends BaseTest {

    @Autowired
    SqlHelper sqlHelper;

    @Autowired
    JobService jobService;

    List<Integer> spuIds = new ArrayList<>();

    public static void main(String[] args) {
        runTest(GenertorTagSQL.class);
    }

    @Override
    public void run() throws Exception {
        reloadSpu();
    }

    public void reloadSpu() {
        sqlHelper.exec(sql, (row) -> {
            spuIds.add(row.getInt("spu_id"));
        });

        int[] ints = spuIds.stream().mapToInt(x -> x).toArray();
        jobService.addSpuReloadJob(ints);

        System.out.println("success");
    }

    public void generatorSQLMain() throws IOException {
        sqlHelper.exec(sql, (row) -> {
            spuIds.add(row.getInt("spu_id"));
        });

        OutputStream os = new FileOutputStream("/Users/sober/Desktop/家装节.sql");
        //generator
        for (Integer spuId : spuIds) {
            String gerentorSQL =
                "INSERT INTO `yitiao_product_spu_tags` (`spu_id`, `external_key`, `label`, `type`, `promotion_price`,"
                    + " `warm_up_time`, `start_time`, `end_time`) VALUES ("
                    + spuId + ", 'promotion-service_15_" + spuId
                    + "', '家装节特惠', 1, NULL, NULL, '2017-10-10 00:00:00', '2017-10-16 23:59:00');\n";
            os.write(gerentorSQL.getBytes());
        }
        os.close();
        print("success!");
    }

    String sql = " "
        + "select  "
        + "  distinct(spu_id) "
        + "from  "
        + "  yitiao_product_promotion_sku psku "
        + "  left join yitiao_product_sku sku on psku.sku_id = sku.id  "
        + "  where "
        + "  psku.promotion_id = 15 "
        + "  and psku.is_deleted = 0   "
        + "  and sku.is_deleted = 0";
}
