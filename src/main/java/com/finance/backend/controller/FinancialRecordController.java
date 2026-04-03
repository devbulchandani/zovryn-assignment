package com.finance.backend.controller;

import com.finance.backend.dto.FinancialRecordRequest;
import com.finance.backend.dto.FinancialRecordResponse;
import com.finance.backend.enums.TransactionType;
import com.finance.backend.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Financial Records", description = "Financial record management endpoints")
@SecurityRequirement(name = "basicAuth")
public class FinancialRecordController {
    
    private final FinancialRecordService financialRecordService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a financial record", description = "Creates a new financial record (ADMIN only)")
    public ResponseEntity<FinancialRecordResponse> createRecord(
            @Valid @RequestBody FinancialRecordRequest request,
            Authentication authentication) {
        FinancialRecordResponse response = financialRecordService.createRecord(
                request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    @Operation(summary = "Get financial records", description = "Retrieves financial records with optional filters (ANALYST, ADMIN)")
    public ResponseEntity<List<FinancialRecordResponse>> getRecords(
            @Parameter(description = "Filter by transaction type")
            @RequestParam(required = false) TransactionType type,
            
            @Parameter(description = "Filter by category")
            @RequestParam(required = false) String category,
            
            @Parameter(description = "Filter by start date (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Filter by end date (inclusive)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<FinancialRecordResponse> records = financialRecordService.getRecords(
                type, category, startDate, endDate);
        return ResponseEntity.ok(records);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a financial record", description = "Updates an existing financial record (ADMIN only)")
    public ResponseEntity<FinancialRecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequest request,
            Authentication authentication) {
        FinancialRecordResponse response = financialRecordService.updateRecord(
                id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a financial record", description = "Deletes a financial record (ADMIN only)")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        financialRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
