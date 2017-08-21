package com.yit.promotion;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yit.common.utils.TransactionUtil;

import com.yit.promotion.entity.PromotionSku2;
import com.yit.promotion.api.PromotionService;
import com.yit.test.BaseTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by sober on 2017/6/5.
 *
 * @author sober
 * @date 2017/06/05
 */
public class PromotionImportUtils extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(PromotionImportUtils.class);

    private static final String filePath = "/Users/sober/Desktop/promotionExcel4.xls";

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Map<String, List<PromotionSku2>> promotionMap = new HashMap<>();

    @Autowired
    private PromotionService promotionService;

    /*
    @Autowired
    private SqlHelper sqlHelper;
    */

    @Autowired
    private TransactionUtil transactionUtil;

    @Override
    public void run() throws Exception {
        //generatorDesc();
        //readPromotionExcel();
    }



    public static void main(String[] args) throws Exception {
        runTest(PromotionImportUtils.class);
    }

    /**
     * 修改促销信息生成规则
     */
    /*public void generatorDesc()
        throws ParseException, IOException, ServiceException {
        preData();
        SpuInfo spuInfo = getSpuInfo();

        //过滤相同的Desc
        spuInfo.promotionInfoList.forEach(promotionInfo -> {
            promotionInfo.spuDescList.forEach(spuDesc -> {
                for (int i = 0; i < spuDesc.skuList.size(); i++) {
                    if (i + 1 <= spuDesc.skuList.size()) {
                        for (int y = i + 1; y < spuDesc.skuList.size(); y++) {
                            if (StringUtils.isNotEmpty(spuDesc.skuList.get(i).haveGift) && StringUtils.isNotEmpty(
                                spuDesc.skuList.get(y).haveGift)) {
                                if (spuDesc.skuList.get(i).haveGift.equals(spuDesc.skuList.get(y).haveGift)) {
                                    spuDesc.skuList.get(y).haveGift = "";
                                }
                            }
                        }
                    }
                }
            });
        });

        List<GeneratorSpuDesc> descList = new ArrayList<>();
        spuInfo.promotionInfoList.forEach(promotionInfo -> {
            promotionInfo.spuDescList.forEach(spuDesc -> {
                GeneratorSpuDesc desc = new GeneratorSpuDesc();
                desc.spuId = spuDesc.spuId;
                spuDesc.skuList.forEach(sku -> {
                    //todo 拼接desc信息
                    //如果商品SKU的原价与活动价相同，同时也有赠品信息时，头部信息显示为
                    if (sku.originalPrice == sku.promotionPrice && StringUtils.isNotEmpty(
                        sku.haveGift)) {
                        if (StringUtils.isEmpty(desc.promotionDesc)) {
                            desc.promotionDesc = "限时特惠:现购买" + sku.haveGift + ";";
                        } else {
                            desc.promotionDesc = desc.promotionDesc + sku.haveGift + ";";
                        }

                    }

                    //商品SKU的原价与活动价不同，同时也有赠品信息时，头部信息显示为
                    if (sku.originalPrice != sku.promotionPrice && StringUtils.isNotEmpty(sku.haveGift)) {

                    }

                });
            });

            if ("48小时特惠".equals(promotionInfo.promotionName)) {
                //desc.promotionDesc +=
            }
        });

        //调用promotion.getPromotionSpuOriginInfoBySpuId
        //调用promotion.updatePromotionSpuOriginInfo
    }
*/
}
