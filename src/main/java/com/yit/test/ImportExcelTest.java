package com.yit.test;

import com.yit.common.utils.import2.ImportType;
import com.yit.common.utils.import2.ImportUtil;

/**
 * Created by sober on 2017/6/6.
 *
 * @author sober
 * @date 2017/06/06
 */
public class ImportExcelTest extends BaseTest {

    @Override
    public void run() throws Exception {
        String path = "/Users/sober/Desktop/商品主标题2导入_1.xlsx";
  /*      ImportUtil.doImport(path, ImportType.XLSX, (row) -> {
            String orderNo = row.get("商品id");
            String title = row.get("主标题1（10字）");
            String s = row.get("副标题1（24字）");
            String s2 = row.get("测试小数");
            print(orderNo + "   " + title + "    " + s + "   " + s2);

        });*/
    }

    public static void main(String[] args) throws Exception {
        runTest(ImportExcelTest.class);
    }
}
