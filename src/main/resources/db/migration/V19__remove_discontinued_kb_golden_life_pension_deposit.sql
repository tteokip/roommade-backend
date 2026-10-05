/* =========================================================
   판매 종료된 KB골든라이프연금예금 제거

   V2의 수동 KB 상품 시드에 포함됐으나 현재 국민은행에서 판매하지 않아
   금융상품 목록과 상세 조회에 노출되지 않도록 제거한다.
   ========================================================= */

DELETE fpl
FROM financial_product_link fpl
INNER JOIN financial_institution fi
    ON fi.financial_institution_id = fpl.financial_institution_id
WHERE fi.financial_institution_code = '0010927'
  AND fpl.product_type = 'DEPOSIT'
  AND fpl.product_code = 'DP01001667';

DELETE dp
FROM deposit_product dp
INNER JOIN financial_institution fi
    ON fi.financial_institution_id = dp.financial_institution_id
WHERE fi.financial_institution_code = '0010927'
  AND dp.product_code = 'DP01001667';
