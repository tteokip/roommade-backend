package com.roommade.domain.financialproduct.mapper;

import com.roommade.domain.financialproduct.domain.FinancialInstitution;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FinancialInstitutionMapper { int upsert(FinancialInstitution institution); }
