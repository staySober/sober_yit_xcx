package com.yit.importUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.common.utils.StringUtils;

import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.import2.ImportUtil;
import com.yit.test.BaseTest;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/14.
 *
 * @author sober
 * @date 2017/08/14
 *
 * 循环遍历文件夹生成更新标价的sql脚本
 */
public class CricleImportHelper extends BaseTest {

    @Autowired
    SqlHelper sqlHelper;

    private File file = new File("/Users/sober/Desktop/第一批标签价");

    private static final Logger logger = LoggerFactory.getLogger(CricleImportHelper.class);

    private List<String> filePath = new ArrayList<>();

    private List<String> importSql = new ArrayList<>();

    private List<String> skuIds = new ArrayList<>();

    private Map<String, List<String>> unmatchSkuId = new HashMap<>();

    public static void main(String args[]) throws Exception {
        runTest(CricleImportHelper.class);

    }

    @Override
    public void run() throws Exception {
        cricleReadFolder(file);
        importMarketPrice();
        exportFile();
    }

    //递归读取文件夹
    public void cricleReadFolder(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File thisFile : files) {
                cricleReadFolder(thisFile);
            }
        } else {
            String absolutePath = file.getAbsolutePath();
            filePath.add(absolutePath);
        }
    }

    //递归读取文件夹
    private void importMarketPrice() {
        for (String path : filePath) {
            if (path.endsWith("xls") || path.endsWith("xlsx")) {
                ImportUtil.doImport(path, (row) -> {
                    String skuId = row.get("sku id");
                    String marketPrice = row.get("标签价");

                    //标签价为空 忽略掉
                    if (StringUtils.isBlank(marketPrice)) {
                        return;
                    }
                    int thisRow = sqlHelper.execInt("select id from yitiao_product_sku where id = ?",
                        new Object[] {skuId});
                    if (thisRow == 0) {
                        //unmatching
                        String fileName = new File(path).getName();
                        List<String> nowUnMatchSkuIds = unmatchSkuId.get(fileName);
                        if (CollectionUtils.isEmpty(nowUnMatchSkuIds)) {
                            nowUnMatchSkuIds = new ArrayList<>();
                            unmatchSkuId.put(fileName, nowUnMatchSkuIds);
                        } else {
                            nowUnMatchSkuIds.add(skuId);
                            unmatchSkuId.put(fileName, nowUnMatchSkuIds);
                        }
                    } else {
                        skuIds.add(skuId);
                        //generator sql
                        String sql = String.format("update yitiao_product_sku set market_price = %s where id = %s;\n",
                            marketPrice, skuId);
                        importSql.add(sql);
                    }
                });
            }
            logger.info("read action : path -----> " + path);
        }
    }

    //导出文件
    private void exportFile() throws Exception {
        OutputStream os = new FileOutputStream(new File("/Users/sober/Desktop/update_product_market_price.sql"));
        importSql.forEach(sql -> {
            try {
                os.write(sql.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        os.close();

        if (!unmatchSkuId.isEmpty()) {
            OutputStream os2 = new FileOutputStream(new File("/Users/sober/Desktop/unmatch_sku_id.txt"));
            unmatchSkuId.forEach((key, value) -> {
                String thisStr = key + ":" + value + "\n";
                try {
                    os2.write(thisStr.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            os2.close();
        } else {
            logger.info("very well! -----------------> 不存在与数据库中不匹配的SKU ID.");
        }

        logger.info("export success!   ----------------->  Finish!");
    }

}
