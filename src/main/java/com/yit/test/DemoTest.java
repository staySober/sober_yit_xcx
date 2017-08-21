package com.yit.test;

import com.yit.common.entity.PageParameter;

import com.yit.common.utils.cache.CacheService;
import com.yit.common.utils.import2.ImportUtil;
import com.yit.common.utils.topic.TopicService2;
import com.yit.entity.ServiceException;
import com.yit.product.api.FullSpuService;
import com.yit.product.api.MediaService;
import com.yit.product.api.ProductService;
import com.yit.product.entity.FullSpu;
import com.yit.product.entity.ProductInfo;
import com.yit.product.entity.SkuInfo;
import com.yit.promotion.api.PromotionService;
import com.yit.promotion.api.PromotionService.Sort;
import com.yit.promotion.entity.ExportPromotionInfo;
import com.yit.promotion.entity.Promotion;
import com.yit.promotion.entity.Promotion.Sku;
import com.yit.promotion.entity.Promotion.SubmitStatus;
import com.yit.promotion.entity.PromotionPageResult;
import com.yit.promotion.entity.SkuResultInfo;
import com.yit.quartz.api.JobService;
import com.yit.quartz.api.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DemoTest extends BaseTest {
    // region 依赖

  @Autowired
    ProductService productService;

   @Autowired
    PromotionService promotionService;
 /*
    @Autowired
    FullSpuService fullSpuService;
*/

   /* @Autowired
    TopicService2 topicService2;



    @Autowired
    MediaService mediaService;

    @Autowired
    CacheService cacheService;*/

    //endregion 依赖

    @Autowired
    JobService jobService;

    private static final String FILE_PATH = "/Users/sober/Desktop/无罔珠宝价格调整.xls";

    public static void main(String[] args) throws Exception {
        runTest(DemoTest.class);
    }

    @Override
    public void run() throws Exception {
        //testPromotion();
        testProduct();
    }

    private void testProduct() {
        List<Integer> list = new ArrayList<>();
        list.add(31327);

        List<SkuInfo> skuInfo = productService.getSkuInfo(list);
        System.out.println(skuInfo);
    }

    private Set<Integer> spuSet  = new HashSet<>();

    private void relaodSpu() {
        //reload spu  !!!
     /*    ImportUtil.doImport(FILE_PATH,(row)->{
            String spuId = row.get("SPU ID");
            spuSet.add(Integer.valueOf(spuId));
        });
        int[] spuIds = spuSet.stream().mapToInt(x -> x).toArray();*/

        boolean result = jobService.addSpuReloadJob(new int[]{1139
,1140
,1141
,4694
,4705
,4799
,5152
,5153
,5154
,6108
,6111
,6696
,10287
,11122
,11123
,11124
,12458
,13148
,13152
,17347
,17348
,17349
,17350
,19241
,19242
,19337
,19563
,19920
,20762
,21741
,21742
,21743
,21744
,21745
,22267
,22270
,22271
,22272
,24162
,24163
,24164
,24165
,24255
,24256
,24257
,24258
,24272
,24552
,24760
,24761
,24762
,24763
,25116
,25273
,25274
,25576
,25577
,25578
,25579
,26036
,27613
,27941
,27942
,27943
,27944
,27957
,27958
,27971
,27972
,28781});
        System.out.println(result);
    }


    public void testPromotion() {
        String s = promotionService.exportConflictSpu();
        System.out.println(s);
    }
    //region testCase

   /*
   private void fun1() throws InterruptedException {
        while (true) {
            topicService2.publish("SoberTest2Topic",null, UUID.randomUUID());
            System.out.println("Success.......");
            Thread.sleep(10000);
        }
    }

    private void fun2() {
        GetHistoryPromotionSkuListRequest request = new GetHistoryPromotionSkuListRequest();
        request.promotionId = 4;
        request.lastPromotionDay = 180;
        request.vendorSkuCode = "6900077062397";

        SkuResultInfo historyPromotionSkuList = promotionService.getHistoryPromotionSkuList(request);
        System.out.println();
    }

   private void fun3() throws ServiceException {
       String s = mediaService.processSegment("1");
       System.out.println(s);

   }

   public void deleteCache() {
        cacheService.delete("process_segment","https://asset.yit.com/media/0d42a7a1-bdff-4f60-ad91-bf3ae0f4ab60.seg");
   }

   */

    //endregion testCase
}
