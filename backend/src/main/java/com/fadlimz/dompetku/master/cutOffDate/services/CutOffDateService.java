package com.fadlimz.dompetku.master.cutOffDate.services;

import com.fadlimz.dompetku.base.services.BaseService;
import com.fadlimz.dompetku.config.StringUtil;
import com.fadlimz.dompetku.master.cutOffDate.dtos.CutOffDateDto;
import com.fadlimz.dompetku.master.cutOffDate.entities.CutOffDate;
import com.fadlimz.dompetku.master.cutOffDate.repositories.CutOffDateRepository;
import com.fadlimz.dompetku.master.user.entities.User;
import com.fadlimz.dompetku.master.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CutOffDateService extends BaseService<CutOffDate> {

    private final CutOffDateRepository cutOffDateRepository;
    private final UserService userService;

    public CutOffDateService(CutOffDateRepository cutOffDateRepository, UserService userService) {
        super(cutOffDateRepository);
        this.cutOffDateRepository = cutOffDateRepository;
        this.userService = userService;
    }

    public CutOffDate create(CutOffDateDto dto) {
        User user = userService.getLoggedInUser();

        // Validate cut-off date must be between 1-31
        if (dto.getCutOffDate() == null || dto.getCutOffDate() < 1 || dto.getCutOffDate() > 31) {
            throw new RuntimeException("Cut-off date must be between 1 and 31");
        }

        // Check duplicate for this user
        if (cutOffDateRepository.findByCutOffDateAndUser(dto.getCutOffDate(), user).isPresent()) {
            throw new RuntimeException("Cut-off date already exists for this user.");
        }

        CutOffDate cutOffDate = dto.toEntity();
        cutOffDate.setUser(user);

        return save(cutOffDate);
    }

    public CutOffDate update(String id, CutOffDateDto dto) {
        User user = userService.getLoggedInUser();

        CutOffDate existing = findById(id)
                .orElseThrow(() -> new RuntimeException("Cut-off date not found"));

        // Validate cut-off date must be between 1-31
        if (dto.getCutOffDate() == null || dto.getCutOffDate() < 1 || dto.getCutOffDate() > 31) {
            throw new RuntimeException("Cut-off date must be between 1 and 31");
        }

        // Check if cut-off date is being changed and if it already exists
        if (!existing.getCutOffDate().equals(dto.getCutOffDate())) {
            if (cutOffDateRepository.findByCutOffDateAndUser(dto.getCutOffDate(), user).isPresent()) {
                throw new RuntimeException("Cut-off date already exists for this user.");
            }
        }

        existing.setCutOffDate(dto.getCutOffDate());

        return update(existing);
    }

    @Override
    public void delete(String id) {
        findById(id).orElseThrow(() -> new RuntimeException("Cut-off date not found"));
        super.delete(id);
    }

    @Override
    public Optional<CutOffDate> findById(String id) {
        return cutOffDateRepository.findByIdAndUser(id, userService.getLoggedInUser());
    }

    @Override
    public List<CutOffDate> findAll() {
        return cutOffDateRepository.findByUser(userService.getLoggedInUser());
    }

    public Optional<CutOffDate> findByCutOffDate(Integer cutOffDate) {
        return cutOffDateRepository.findByCutOffDateAndUser(cutOffDate, userService.getLoggedInUser());
    }

    public List<CutOffDate> search(String keyword) {
        User user = userService.getLoggedInUser();
        if (StringUtil.isBlank(keyword)) {
            return findAll();
        }
        // Simple search by cut-off date number
        try {
            Integer dateValue = Integer.parseInt(keyword.trim());
            return cutOffDateRepository.findByCutOffDateAndUser(dateValue, user)
                    .map(List::of)
                    .orElse(List.of());
        } catch (NumberFormatException e) {
            return List.of();
        }
    }
}
