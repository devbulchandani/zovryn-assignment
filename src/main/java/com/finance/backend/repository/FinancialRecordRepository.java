package com.finance.backend.repository;

import com.finance.backend.entity.FinancialRecord;
import com.finance.backend.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>, 
                                                    JpaSpecificationExecutor<FinancialRecord> {
    
    @Query("SELECT COALESCE(SUM(fr.amount), 0) FROM FinancialRecord fr WHERE fr.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);
    
    @Query("SELECT fr FROM FinancialRecord fr WHERE " +
           "(:type IS NULL OR fr.type = :type) AND " +
           "(:category IS NULL OR fr.category = :category) AND " +
           "(:startDate IS NULL OR fr.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR fr.transactionDate <= :endDate)")
    List<FinancialRecord> findByFilters(
        @Param("type") TransactionType type,
        @Param("category") String category,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
