package com.yit.deal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.JSONObject;

import com.yit.common.utils.SqlHelper;
import com.yit.common.utils.export.ExportTable;
import com.yit.common.utils.export.ExportType;
import com.yit.common.utils.export.ExportUtil;
import com.yit.product.entity.Product;
import com.yit.test.BaseTest;
import org.apache.commons.collections.ArrayStack;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 比对所有Spu的firstOnSaleTime并修正
 */
public class FixAllFirstOnSaleTimeRunner extends BaseTest {
    @Autowired
    SqlHelper sqlHelper;

    String queryAudit
        = "select * from yitiao_audit where main_type = 'product' and sub_type = ? order by created_time asc";

    String allSpuIds = "select id from yitiao_product_spu order by id ";

    String getFirstOnSaleTime = "select first_on_sale_time from yitiao_product_spu where id = ?;";

    List<Integer> spuIds = new ArrayList<>();

    List<HaveIssueProduct> haveIssueSpuIds = new ArrayList<>();
    private Object allSpuId;

    @Override
    public void run() throws Exception {
        getAllSpuId();
        for (Integer spuId : spuIds) {
            check(spuId);
        }
        exportReport();
    }

    // 导出compare 结果
    private void exportReport() {
        ExportTable table = new ExportTable();
        for (HaveIssueProduct issueProduct : haveIssueSpuIds) {
            table.addRow(x->{
                x.put("spuId",issueProduct.id);
                x.put("SPU表中的首次上架时间",issueProduct.firstTimeInProd);
                x.put("AUDIT表中的首次上架时间",issueProduct.firstTimeInAudit);
                x.put("实际的审计时间",issueProduct.auditTime);
            });
        }

        ExportUtil.export(table, ExportType.CSV,"/Users/Sober/Desktop/onsaleTimeError");

        System.out.println("SUCCESS!");
    }

    // 校验audit时间和firstOnSaleTime是否一致
    private void check(int spuId) {
        sqlHelper.exec(queryAudit, new Object[] {spuId}, (row) -> {
            String body = row.getString("body");
            Timestamp created_time = row.getTimestamp("created_time");
            Product product = JSON.parse(body, Product.class);
            Date firstOnSaleTime = product.saleInfo.firstOnSaleTime;
            if (firstOnSaleTime != null) {

                sqlHelper.exec(getFirstOnSaleTime, new Object[] {spuId}, (row2) -> {
                    Timestamp first_on_sale_time = row2.getTimestamp("first_on_sale_time");
                    Date prodFirstOnSaleTime = new Date(first_on_sale_time.getTime());
                    Date auditTime = new Date(created_time.getTime());

                    if (firstOnSaleTime.compareTo(prodFirstOnSaleTime) != 0) {
                        HaveIssueProduct issueProduct = new HaveIssueProduct();
                        issueProduct.id = product.id;
                        issueProduct.auditTime = auditTime;
                        issueProduct.firstTimeInAudit = firstOnSaleTime;
                        issueProduct.firstTimeInProd = prodFirstOnSaleTime;
                        haveIssueSpuIds.add(issueProduct);
                    }
                });
            }
        });
    }

    public void getAllSpuId() {
        sqlHelper.exec(allSpuIds, (row) -> {
            spuIds.add(row.getInt("id"));
        });
    }
}
