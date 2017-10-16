SELECT
	p.id AS promotionId,
	p.NAME AS promotionName,
	psku.sku_id AS skuId,
	psku.promotion_price AS price,
	'promotion' AS referenceType,
	CONCAT( psku.sku_id, "_", psku.id ) AS referenceNo,
CASE

		WHEN p.type = 5 THEN
		psku.start_time ELSE p.start_time
	END AS startTime,
CASE

		WHEN p.type = 5 THEN
		psku.end_time ELSE p.end_time
	END AS endTime
FROM
	yitiao_product_promotion_sku psku
	LEFT JOIN yitiao_product_promotion p ON p.id = psku.promotion_id
WHERE
	psku.is_deleted = 0
	AND psku.promotion_id != 4
AND
CASE

	WHEN p.type = 5 THEN
! ( psku.end_time < '2017-08-01 00:00:00' OR psku.start_time > '2017-11-01 23:59:59' ) ELSE ! ( p.end_time < '2017-08-01 00:00:00' OR p.start_time > '2017-11-01 23:59:59' ) END;