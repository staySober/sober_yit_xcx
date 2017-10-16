/*
package com.yit.promotion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.yit.common.entity.PageParameter;
import com.yit.common.utils.import2.ImportUtil;
import com.yit.entity.ApiReturnCode;
import com.yit.entity.ServiceException;
import com.yit.entity.ServiceRuntimeException;
import com.yit.promotion.api.PromotionService.Sort;
import com.yit.promotion.entity.PromotionSku2;
import com.yit.promotion.entity.SpuInfo;
import com.yit.promotion.entity.SpuInfo.PromotionInfo;
import com.yit.promotion.entity.SpuInfo.SpuDesc;
import com.yit.promotion.api.PromotionService;
import com.yit.promotion.entity.Promotion;
import com.yit.promotion.entity.Promotion.Sku;
import com.yit.promotion.entity.Promotion.Status;
import com.yit.promotion.entity.SkuResultInfo;
import com.yit.test.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

*/
/**
 * Created by sober on 2017/6/12.
 *
 * @author sober
 * @date 2017/06/12
 *//*

public class BatchImportPromotionUtils extends BaseTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionImportUtils.class);

    private static final String FILE_PATH = "/Users/sober/Desktop/promotionImport.xls";

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Map<String, List<PromotionSku2>> promotionMap = new HashMap<>();

    private static final String SKU_DEFAULT_END_TIME = "2027-06-05 00:00:00";

    @Autowired
    private PromotionService promotionService;

    @Override
    public void run() throws Exception {
        readPromotionExcel();
    }

    public static void main(String[] args) {
        runTest(BatchImportPromotionUtils.class);
    }

    */
/**
     * 批量提报sku
     *
     * @throws ServiceException
     * @throws ParseException
     * @throws IOException
     *//*

    private void readPromotionExcel() throws Exception {
        //setup 1准备数据
        List<Promotion> promotionList = preData();
        //setup 2校验
        afterDataValidate(promotionList);
        // setup 3 活动提报
        int beforePromotionId = 0;
        int[] beforePromotions = new int[0];
        try {
            for (Map.Entry<String, List<PromotionSku2>> entry : promotionMap.entrySet()) {
                Promotion promotion = promotionList.stream().filter(x -> entry.getKey().equals(x.name)).findFirst()
                    .get();
                List<PromotionSku2> value = entry.getValue();
                List<Sku> promotionSkus = value.stream().map(x -> x.sku).collect(Collectors.toList());
                promotionService.createOrUpdatePromotionSku(promotion.id, promotionSkus, "系统", 0);
                beforePromotionId = promotion.id;
                beforePromotions = promotionSkus.stream().mapToInt(x -> x.skuId).toArray();
            }
            LOGGER.info("活动提报导入数据成功!");

        } catch (Exception e) {
            promotionService.deletePromotionSku(beforePromotionId, beforePromotions, "系统", 0);
            LOGGER.error(e.toString());
        }
        System.out.println("SUCCESS!===========================");
    }

    */
/**
     * 准备数据
     *//*

    public List<Promotion> preData() throws Exception {
        //读文件
        File excel = new File(FILE_PATH);
        HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(excel));
        //解析第一个sheet
        HSSFSheet sheet = book.getSheetAt(0);
        //promotionList
        PageParameter parameter = new PageParameter();
        parameter.limit = 100;
        parameter.offset = 0;
        List<Promotion> promotionList = promotionService.getPromotionList(null, null, null, null, null, null, null,
            null, parameter, false, 0, "系统").rows;
        //解析该sheet  略过第一行标题

        ImportUtil.doImport(FILE_PATH, (row) -> {

            // 解析数据
            String spuId = row.get("SPU ID");
            String skuId = row.get("SKU ID");
            String promotionName = row.get("活动名称");
            String startTime = row.get("开始时间");
            String endTime = row.get("结束时间");
            String haveGift = row.get("赠品信息");
            String promotionPrice = row.get("活动价格");
            String promotionStock = row.get("活动库存");
            String originalPrice = row.get("活动后价格");
            String originalStock = row.get("活动后库存");

            // 对数据校验
            //todo 生成描述的时候关闭该校验
            try {
                validateParams(promotionList, spuId, skuId, promotionName, startTime, endTime, promotionPrice);
            } catch (ServiceException e) {
                e.printStackTrace();
            }

            PromotionSku2 sku = new PromotionSku2();
            sku.sku.skuId = Integer.parseInt(skuId);

            try {
                sku.sku.startTime = dateFormat.parse(startTime);
                sku.sku.endTime = StringUtils.isNotEmpty(endTime) ? dateFormat.parse(endTime) : dateFormat.parse(
                    SKU_DEFAULT_END_TIME);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sku.sku.haveGift = haveGift;
            sku.sku.promotionPrice = BigDecimal.valueOf(Double.parseDouble(promotionPrice));
            sku.sku.promotionStock = StringUtils.isNotEmpty(promotionStock) ? Integer.parseInt(promotionStock) : -1;
            sku.sku.originalPrice = StringUtils.isNotEmpty(originalPrice) ? BigDecimal.valueOf(
                Double.parseDouble(originalPrice)) : BigDecimal.valueOf(Double.parseDouble(promotionPrice));
            sku.sku.originalStock = StringUtils.isNotEmpty(originalStock) ? Integer.parseInt(originalStock) : -1;
            sku.sku.modifyOriginalPrice = StringUtils.isNotEmpty(originalPrice) ? true : false;
            sku.sku.autoUnshelve = false;
            sku.spuId = Integer.parseInt(spuId);

            List<PromotionSku2> skus = promotionMap.get(promotionName);
            if (skus == null) {
                List<PromotionSku2> skuList = new ArrayList<>();
                skuList.add(sku);
                promotionMap.put(promotionName, skuList);
            } else {
                skus.add(sku);
            }
        });

        return promotionList;
    }

    */
/**
     * 校验数据
     *//*

    private void validateParams(List<Promotion> promotionList, String spuId, String skuId, String promotionName,
                                String startTime, String endTime, String promotionPrice) throws ServiceException {
        if (StringUtils.isEmpty(spuId)) {
            LOGGER.error("请检查导入的excel中是否有 :SPUID 为空的数据");
            throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR);
        }
        if (StringUtils.isEmpty(skuId)) {
            LOGGER.error("请检查导入的excel中是否有 :SKUID 为空的数据");
            throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR);
        }
        if (StringUtils.isEmpty(promotionName)) {
            LOGGER.error("请检查导入的excel中是否有 :活动名称 为空的数据");
            throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR);
        }
        if (StringUtils.isEmpty(startTime)) {
            LOGGER.error("请检查导入的excel中是否有 :开始时间 为空的数据");
            throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR);
        }
        if (!isValidDate(startTime)) {
            LOGGER.error("请检查导入的excel中是否有 :开始时间 格式不正确的数据");
            throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR);
        }
        if (StringUtils.isNotEmpty(endTime) && !isValidDate(endTime)) {
            LOGGER.error("请检查导入的excel中是否有 :结束时间 格式不正确的数据");
            throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR);
        }
        if (StringUtils.isEmpty(promotionPrice)) {
            LOGGER.error("请检查导入的excel中是否有 :活动价格 为空的数据");
            throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR);
        }
        //通过活动名称获取活动实体
        Promotion promotion = null;
        Optional<Promotion> first = promotionList.stream().filter(x -> promotionName.equals(x.name)).findFirst();
        if (first.isPresent()) {
            promotion = first.get();
        }
        if (promotion == null) {
            throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR,
                "不存在名称为: " + promotionName + " 的活动,请检查名称是否有误!");
        }
        //校验该sku所属的spu下是否之前已经有sku在该活动进行中
        SkuResultInfo resultInfo = promotionService.getPromotionSkuList(promotion.id, null, null, null, null, null,null,Sort.DAY_1,true,true,true,"sober",114);

        resultInfo.brandList.forEach(b -> {
            b.spuList.forEach(spu -> {
                if (spu.id == Integer.parseInt(spuId)) {
                    spu.skuList.forEach(sku -> {
                        if (sku.promotionSku != null) {
                            if (sku.promotionSku.status != Status.END) {
                                throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR,
                                    "SKU ID :" + skuId + "所属的SPU (ID) :" + spuId + " 已经有sku提报在活动: " + promotionName
                                        + " 中,提报失败!");
                            }

                        }
                    });
                }
            });
        });

    }

    public void afterDataValidate(List<Promotion> promotionList) throws ServiceException {
        //校验spu下的sku的起止时间是否一致
        SpuInfo spuInfo = getSpuInfo();
        spuInfo.promotionInfoList.forEach(promotionInfo -> {
            promotionInfo.spuDescList.forEach(spuDesc -> {
                for (int i = 0; i < spuDesc.skuList.size(); i++) {
                    if (i + 1 <= spuDesc.skuList.size()) {
                        for (int y = i + 1; y < spuDesc.skuList.size(); y++) {
                            if (spuDesc.skuList.get(i).startTime.compareTo(spuDesc.skuList.get(y).startTime) !=
                                0 || spuDesc.skuList.get(i).endTime.compareTo(spuDesc.skuList.get(y).endTime) != 0) {
                                throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR,
                                    "同Spu下的sku的活动提报起止时间必须向相同! 在活动 :" + promotionInfo.promotionName + "中, SkuId:"
                                        + spuDesc.skuList.get(i).skuId + "与 SkuId:"
                                        + spuDesc.skuList.get(y).skuId + "的起止时间不同!提报失败");
                            }
                        }
                    }
                }
            });
        });

        //校验excel表中同一个spu 在不同活动中的 sku的起止时间不能有交集
        List<PromotionInfo> promotionInfos = spuInfo.promotionInfoList;
        for (int i = 0; i < promotionInfos.size(); i++) {
            if (i + 1 <= promotionInfos.size()) {
                for (int y = i + 1; y < promotionInfos.size(); y++) {
                    int finalY = y;
                    promotionInfos.get(i).spuDescList.forEach(spu1 -> {
                        promotionInfos.get(finalY).spuDescList.forEach(spu2 -> {
                            if (spu1.spuId == spu2.spuId) {
                                //校验活动时间不能有交集
                                if (isOverlap(spu1.skuList.get(0).startTime, spu1.skuList.get(0).endTime,
                                    spu2.skuList.get(0).startTime, spu2.skuList.get(0).endTime)) {
                                    throw new ServiceRuntimeException(ApiReturnCode.DUBBO_SERVICE_ERROR,
                                        "活动提报导入的Excel表中,SpuId:" + spu1.spuId + "在多个活动中的活动起止时间有冲突!");
                                }
                            }
                        });
                    });
                }
            }
        }

    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            dateFormat.setLenient(false);
            dateFormat.parse(str);
        } catch (ParseException e) {
            // 如果throw java.text.ParseException 就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    private SpuInfo getSpuInfo() {
        //封装数据
        SpuInfo spuInfo = new SpuInfo();

        for (Map.Entry<String, List<PromotionSku2>> entry : promotionMap.entrySet()) {

            String promotionName = entry.getKey();
            List<PromotionSku2> skuList = entry.getValue();
            for (PromotionSku2 sku : skuList) {
                SpuDesc spu = spuInfo.getPromotionInfo(promotionName, x -> x.promotionName = promotionName)
                    .getSpu(sku.spuId, y -> {
                        y.spuId = sku.spuId;
                        y.promotionName = promotionName;
                    });
                spu.getSku(sku.sku.skuId, z -> {
                    z.skuId = sku.sku.skuId;
                    z.haveGift = sku.sku.haveGift;
                    z.startTime = sku.sku.startTime;
                    z.endTime = sku.sku.endTime;
                    z.promotionPrice = sku.sku.promotionPrice;
                    z.originalPrice = sku.sku.originalPrice;
                    z.originalStock = sku.sku.originalStock;
                    z.promotionStock = sku.sku.promotionStock;

                });

            }
        }
        return spuInfo;
    }

    */
/**
     * 判断2个时间段有没有交集
     *//*

    private static boolean isOverlap(Date leftStartDate, Date leftEndDate, Date rightStartDate, Date rightEndDate) {
        return !(leftEndDate.before(rightStartDate) || rightEndDate.before(leftStartDate));
    }

}
*/
