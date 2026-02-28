package com.fadlimz.dompetku.master.category.services;

import com.fadlimz.dompetku.base.services.BaseService;
import com.fadlimz.dompetku.config.StringUtil;
import com.fadlimz.dompetku.master.category.dtos.CategoryDto;
import com.fadlimz.dompetku.master.category.entities.Category;
import com.fadlimz.dompetku.master.category.repositories.CategoryRepository;
import com.fadlimz.dompetku.master.user.entities.User;
import com.fadlimz.dompetku.master.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService extends BaseService<Category> {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public CategoryService(CategoryRepository categoryRepository, UserService userService) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    public Category create(CategoryDto dto) {
        User user = userService.getLoggedInUser();

        // Auto-generate categoryCode from categoryName if not provided
        String categoryCode = dto.getCategoryCode();
        if (StringUtil.isBlank(categoryCode) && !StringUtil.isBlank(dto.getCategoryName())) {
            categoryCode = generateCode(dto.getCategoryName(), user);
        }

        if (categoryRepository.findByCategoryCodeAndUser(categoryCode, user).isPresent()) {
            throw new RuntimeException("Category Code already exists for this user.");
        }

        Category category = dto.toEntity();
        category.setCategoryCode(categoryCode);
        category.setUser(user);

        return save(category);
    }

    public Category update(String id, CategoryDto dto) {
        User user = userService.getLoggedInUser();
        
        Category existing = findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!existing.getCategoryCode().equals(dto.getCategoryCode())) {
             if (categoryRepository.findByCategoryCodeAndUser(dto.getCategoryCode(), user).isPresent()) {
                throw new RuntimeException("Category Code already exists for this user.");
            }
        }

        existing.setCategoryCode(dto.getCategoryCode());
        existing.setCategoryName(dto.getCategoryName());
        existing.setCashFlowFlag(dto.getCashFlowFlag());
        existing.setActiveFlag(dto.getActiveFlag());
        
        return update(existing);
    }

    @Override
    public void delete(String id) {
        findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
        super.delete(id);
    }

    @Override
    public Optional<Category> findById(String id) {
        return categoryRepository.findByIdAndUser(id, userService.getLoggedInUser());
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findByUser(userService.getLoggedInUser());
    }

    public Optional<Category> findByCategoryCode(String categoryCode) {
        return categoryRepository.findByCategoryCodeAndUser(categoryCode, userService.getLoggedInUser());
    }

    public List<Category> search(String keyword) {
        User user = userService.getLoggedInUser();
        if (StringUtil.isBlank(keyword)) {
            return findAll();
        }
        return categoryRepository.search(user, keyword);
    }

    /**
     * Generates a unique code from a name string.
     * Format: trim, toUpperCase, replace spaces with underscore
     * @param name the source name
     * @param user the current user (for user-specific uniqueness)
     * @return unique code
     */
    private String generateCode(String name, User user) {
        String baseCode = name.trim().toUpperCase().replaceAll("\\s+", "_");
        return findUniqueCode(baseCode, user);
    }

    /**
     * Finds a unique code by adding suffix if necessary.
     * @param baseCode the base code to check
     * @param user the current user
     * @return unique code with suffix if needed
     */
    private String findUniqueCode(String baseCode, User user) {
        String uniqueCode = baseCode;
        int counter = 1;

        while (categoryRepository.findByCategoryCodeAndUser(uniqueCode, user).isPresent()) {
            uniqueCode = baseCode + "_" + counter;
            counter++;
        }

        return uniqueCode;
    }
}
