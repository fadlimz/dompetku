package com.fadlimz.dompetku.master.transactionCode.services;

import com.fadlimz.dompetku.base.services.BaseService;
import com.fadlimz.dompetku.config.StringUtil;
import com.fadlimz.dompetku.master.transactionCode.dtos.TransactionCodeDto;
import com.fadlimz.dompetku.master.transactionCode.entities.TransactionCode;
import com.fadlimz.dompetku.master.transactionCode.repositories.TransactionCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionCodeService extends BaseService<TransactionCode> {

    private final TransactionCodeRepository transactionCodeRepository;

    public TransactionCodeService(TransactionCodeRepository transactionCodeRepository) {
        super(transactionCodeRepository);
        this.transactionCodeRepository = transactionCodeRepository;
    }

    public TransactionCode create(TransactionCodeDto dto) {
        // Auto-generate transactionCode from transactionName if not provided
        String transactionCode = dto.transactionCode;
        if (StringUtil.isBlank(transactionCode) && !StringUtil.isBlank(dto.transactionName)) {
            transactionCode = generateCode(dto.transactionName);
        }

        if (transactionCodeRepository.findByTransactionCode(transactionCode).isPresent()) {
            throw new RuntimeException("Transaction Code already exists.");
        }

        TransactionCode entity = dto.toEntity();
        entity.setTransactionCode(transactionCode);
        return save(entity);
    }

    public TransactionCode update(String id, TransactionCodeDto dto) {
        TransactionCode existing = findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction Code not found"));

        if (!existing.getTransactionCode().equals(dto.transactionCode)) {
             if (transactionCodeRepository.findByTransactionCode(dto.transactionCode).isPresent()) {
                throw new RuntimeException("Transaction Code already exists.");
            }
        }

        existing.setTransactionCode(dto.transactionCode);
        existing.setTransactionName(dto.transactionName);
        
        return update(existing);
    }

    public Optional<TransactionCode> findByTransactionCode(String transactionCode) {
        return transactionCodeRepository.findByTransactionCode(transactionCode);
    }

    public List<TransactionCode> search(String keyword) {
        if (StringUtil.isBlank(keyword)) {
            return findAll();
        }
        return transactionCodeRepository.search(keyword);
    }

    /**
     * Generates a unique code from a name string.
     * Format: trim, toUpperCase, replace spaces with underscore
     * @param name the source name
     * @return unique code
     */
    private String generateCode(String name) {
        String baseCode = name.trim().toUpperCase().replaceAll("\\s+", "_");
        return findUniqueCode(baseCode);
    }

    /**
     * Finds a unique code by adding suffix if necessary.
     * @param baseCode the base code to check
     * @return unique code with suffix if needed
     */
    private String findUniqueCode(String baseCode) {
        String uniqueCode = baseCode;
        int counter = 1;

        while (transactionCodeRepository.findByTransactionCode(uniqueCode).isPresent()) {
            uniqueCode = baseCode + "_" + counter;
            counter++;
        }

        return uniqueCode;
    }
}
