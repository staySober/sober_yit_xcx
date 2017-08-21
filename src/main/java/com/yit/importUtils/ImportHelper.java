package com.yit.importUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.yit.common.utils.import2.ImportUtil;
import com.yit.test.BaseTest;

/**
 * Created by sober on 2017/8/8.
 *
 * @author sober
 * @date 2017/08/08
 */
public class ImportHelper extends BaseTest {

    private static final String FILE_PATH = "/Users/sober/Desktop/无罔珠宝价格调整.xls";

    List<String> sqls = new ArrayList<>();
    @Override
    public void run() throws Exception {
        ImportUtil.doImport(FILE_PATH,(row)->{
            String skuId = row.get("SKU ID");
            String newPrice = row.get("新价格");
            String sql = "update yitiao_product_sku sku set market_price = " + newPrice + " where id = " + skuId +";";
            sqls.add(sql);
        });

        OutputStream os = new FileOutputStream(new File("/Users/sober/Desktop/update_product_price.sql"));
        sqls.forEach(sql->{
            try {
                os.write(sql.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        os.close();
    }

    public static void main(String[] args) {
        runTest(ImportHelper.class);
    }
}
