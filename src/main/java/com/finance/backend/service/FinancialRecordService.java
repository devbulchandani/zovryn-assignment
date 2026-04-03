package com.finance.backend.service;

import com.finance.backend.dto.FinancialRecordRequest;
import com.finance.backend.dto.FinancialRecordResponse;
import com.finance.backend.entity.FinancialRecord;
import com.finance.backend.entity.User;
import com.finance.backend.enums.TransactionType;
import com.finance.backend.exception.ResourceNotFoundException;
import com.finance.backend.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialRecordService {
    
    private final FinancialRecordRepository financialRecordRepository;
    private final UserService userService;
    
    @Transactional
    public FinancialRecordResponse createRecord(FinancialRecordRequest request, String username) {
        log.info("Creating financial record for user: {}", username);
        
        User user = userService.findByUsername(username);
        
        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .transactionDate(request.getTransactionDate())
                .notes(request.getNotes())
                .createdBy(user)
                .build();
        
        FinancialRecord savedRecord = financialRecordRepository.save(record);
        log.info("Financial record created with ID: {}", savedRecord.getId());
        
        return mapToResponse(savedRecord);
    }
    
    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> getRecords(
            TransactionType type, 
            String category, 
            LocalDate startDate, 
            LocalDate endDate) {
        log.info("Fetching records with filters - type: {}, category: {}, startDate: {}, endDate: {}", 
                type, category, startDate, endDate);
        
        List<FinancialRecord> records = financialRecordRepository.findByFilters(
                type, category, startDate, endDate);
        
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public FinancialRecordResponse updateRecord(Long id, FinancialRecordRequest request, String username) {
        log.info("Updating financial record ID: {} by user: {}", id, username);
        
        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with ID: " + id));
        
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setTransactionDate(request.getTransactionDate());
        record.setNotes(request.getNotes());
        
        FinancialRecord updatedRecord = financialRecordRepository.save(record);
        log.info("Financial record updated successfully");
        
        return mapToResponse(updatedRecord);
    }
    
    @Transactional
    public void deleteRecord(Long id) {
        log.info("Deleting financial record ID: {}", id);
        
        if (!financialRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Financial record not found with ID: " + id);
        }
        
        financialRecordRepository.deleteById(id);
        log.info("Financial record deleted successfully");
    }
    
    private FinancialRecordResponse mapToResponse(FinancialRecord record) {
        return FinancialRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .transactionDate(record.getTransactionDate())
                .notes(record.getNotes())
                .createdById(record.getCreatedBy().getId())
                .createdByUsername(record.getCreatedBy().getUsername())
                .build();
    }
}
