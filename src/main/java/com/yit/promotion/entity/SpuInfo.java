package com.yit.promotion.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.yit.common.entity.Action2;
import com.yit.promotion.entity.Promotion.Sku;

public class SpuInfo {
    public List<PromotionInfo> promotionInfoList;

    public SpuInfo() {
        promotionInfoList = new ArrayList<>();
    }

    public PromotionInfo getPromotionInfo(String promotionName, Action2<PromotionInfo> action) {
        Optional<PromotionInfo> any = promotionInfoList.stream().filter(x -> x.promotionName.equals(promotionName))
            .findAny();
        if (any.isPresent()) {
            return any.get();
        }
        PromotionInfo info = new PromotionInfo();
        info.promotionName = promotionName;
        action.invoke(info);
        promotionInfoList.add(info);
        return info;
    }

    public class PromotionInfo {
        public String promotionName;
        public List<SpuDesc> spuDescList;

        public PromotionInfo() {
            spuDescList = new ArrayList<>();
        }

        public SpuDesc getSpu(int spuId, Action2<SpuDesc> action) {
            Optional<SpuDesc> found = spuDescList.stream().filter(x -> x.spuId == spuId).findAny();
            if (found.isPresent()) {
                return found.get();
            }
            SpuDesc spuDesc = new SpuDesc();
            spuDesc.spuId = spuId;
            action.invoke(spuDesc);
            spuDescList.add(spuDesc);
            return spuDesc;
        }
    }

    public class SpuDesc {

        public int spuId;

        public String desc;

        public String promotionName;

        public List<Sku> skuList;

        public SpuDesc() {
            skuList = new ArrayList<>();
        }

        public Sku getSku(int skuId, Action2<Sku> action) {
            Optional<Sku> any = skuList.stream().filter(x -> x.skuId == skuId).findAny();
            if (any.isPresent()) {
                return any.get();
            }
            Sku sku = new Sku();
            sku.skuId = skuId;
            action.invoke(sku);
            skuList.add(sku);
            return sku;
        }

    }

}