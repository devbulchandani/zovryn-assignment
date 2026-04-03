package com.finance.backend.config;

import com.finance.backend.entity.FinancialRecord;
import com.finance.backend.entity.User;
import com.finance.backend.enums.TransactionType;
import com.finance.backend.enums.UserRole;
import com.finance.backend.repository.FinancialRecordRepository;
import com.finance.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        log.info("Initializing database with seed data...");
        
        // Create users
        User admin = createUser("admin", "admin", UserRole.ADMIN);
        User analyst = createUser("analyst", "analyst", UserRole.ANALYST);
        User viewer = createUser("viewer", "viewer", UserRole.VIEWER);
        
        log.info("Created 3 users: admin, analyst, viewer");
        
        // Create financial records
        List<FinancialRecord> records = new ArrayList<>();
        
        records.add(createRecord(admin, new BigDecimal("5000.00"), TransactionType.INCOME, 
                "Salary", LocalDate.now().minusDays(30), "Monthly salary payment"));
        
        records.add(createRecord(admin, new BigDecimal("1200.00"), TransactionType.EXPENSE, 
                "Rent", LocalDate.now().minusDays(28), "Monthly rent payment"));
        
        records.add(createRecord(admin, new BigDecimal("350.50"), TransactionType.EXPENSE, 
                "Groceries", LocalDate.now().minusDays(25), "Weekly grocery shopping"));
        
        records.add(createRecord(admin, new BigDecimal("150.00"), TransactionType.INCOME, 
                "Freelance", LocalDate.now().minusDays(20), "Freelance project payment"));
        
        records.add(createRecord(admin, new BigDecimal("89.99"), TransactionType.EXPENSE, 
                "Utilities", LocalDate.now().minusDays(18), "Electricity and water bill"));
        
        records.add(createRecord(admin, new BigDecimal("2500.00"), TransactionType.INCOME, 
                "Bonus", LocalDate.now().minusDays(15), "Performance bonus"));
        
        records.add(createRecord(admin, new BigDecimal("75.00"), TransactionType.EXPENSE, 
                "Entertainment", LocalDate.now().minusDays(12), "Movie tickets and dinner"));
        
        records.add(createRecord(admin, new BigDecimal("200.00"), TransactionType.EXPENSE, 
                "Transportation", LocalDate.now().minusDays(10), "Monthly transit pass"));
        
        records.add(createRecord(admin, new BigDecimal("500.00"), TransactionType.INCOME, 
                "Investment", LocalDate.now().minusDays(7), "Dividend payment"));
        
        records.add(createRecord(admin, new BigDecimal("120.00"), TransactionType.EXPENSE, 
                "Healthcare", LocalDate.now().minusDays(5), "Doctor visit and medication"));
        
        records.add(createRecord(admin, new BigDecimal("300.00"), TransactionType.EXPENSE, 
                "Shopping", LocalDate.now().minusDays(3), "Clothing and accessories"));
        
        records.add(createRecord(admin, new BigDecimal("1000.00"), TransactionType.INCOME, 
                "Consulting", LocalDate.now().minusDays(1), "Consulting services payment"));
        
        financialRecordRepository.saveAll(records);
        
        log.info("Created {} financial records", records.size());
        log.info("Database initialization completed successfully!");
        log.info("Test credentials - admin:admin, analyst:analyst, viewer:viewer");
    }
    
    private User createUser(String username, String password, UserRole role) {
        return userRepository.save(User.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .isActive(true)
                .build());
    }
    
    private FinancialRecord createRecord(User user, BigDecimal amount, TransactionType type, 
                                        String category, LocalDate date, String notes) {
        return FinancialRecord.builder()
                .amount(amount)
                .type(type)
                .category(category)
                .transactionDate(date)
                .notes(notes)
                .createdBy(user)
                .build();
    }
}
