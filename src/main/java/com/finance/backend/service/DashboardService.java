package com.finance.backend.service;

import com.finance.backend.dto.DashboardSummaryResponse;
import com.finance.backend.enums.TransactionType;
import com.finance.backend.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    
    private final FinancialRecordRepository financialRecordRepository;
    
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        log.info("Calculating dashboard summary");
        
        BigDecimal totalIncome = financialRecordRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = financialRecordRepository.sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);
        
        log.info("Summary calculated - Income: {}, Expenses: {}, Net: {}", 
                totalIncome, totalExpenses, netBalance);
        
        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .build();
    }
}
