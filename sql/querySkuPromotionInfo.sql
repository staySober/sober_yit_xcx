select sku.spu_id as spuId,
	   sku.id as skuId,
	   p.name as name,
	   p.type as type,
	   p.start_time as startTime,
	   p.end_time as endTime,
	   p.status as status,
	   p.submit_status as submitStatus,
	   bd.fullname as bdName
			 from yitiao_product_promotion_sku psku
						left join yitiao_product_sku sku on sku.id = psku.sku_id
						left join yitiao_product_spu_owner o on o.spu_id = sku.spu_id
						left join yitiao_admin bd on bd.id = o.bd_owner_id
						left join yitiao_product_promotion p on p.id = psku.promotion_id
        where sku.id = ?
        and promotion_id = ?
        and psku.is_deleted = 0;