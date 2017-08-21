package com.yit.export;

import com.yit.promotion.api.PromotionService;
import com.yit.test.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/8/4.
 *
 * @author sober
 * @date 2017/08/04
 *
 *
 * notes:
 * 操作说明：
1、选择供应商名称为：ever thames、Fulton、boogie board、广州智黑科技有限公司的供应商；
2、选择时间为：2017年7月1日00:00:00 ～ 2017年7月31日23:59:59 范围内，1中供应商的所有报名记录（大促、限时特惠）；
3、给出的Excel的格式：供应商名称、SKU名称、供应商SKU、活动名称、报名开始时间、报名结束时间、活动价格。
 */
public class ExportPromotionRunner extends BaseTest {

    @Autowired
    PromotionService promotionService;

    @Override
    public void run() throws Exception {
        export();
    }

    public static void main(String[] args) {
        runTest(ExportPromotionRunner.class);
    }

    private void export() {

    }
}
