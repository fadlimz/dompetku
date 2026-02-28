package com.fadlimz.dompetku.base.services;

import com.fadlimz.dompetku.base.entities.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<T extends BaseEntity>  {

    protected final JpaRepository<T, String> repository;

    public BaseService(JpaRepository<T, String> repository) {
        this.repository = repository;
    }

    @Transactional
    public List<T> findAll() {
        return repository.findAll();
    }

    public Optional<T> findById(String id) {
        return repository.findById(id);
    }

    @Transactional
    public T save(T entity) {
        String currentUser = getCurrentUser();

        // Check if this is a new entity (no ID or ID is null)
        boolean isNew = entity.getId() == null || isNewEntity(entity);

        if (isNew) {
            // Set fields for new entity
            entity.setVersion(1); // Initialize version to 1 for new entities
            entity.setCreatedBy(currentUser);
            entity.setCreatedTime(new Date());
        }

        // Always update modified fields
        entity.setModifiedBy(currentUser);
        entity.setModifiedTime(new Date());

        return repository.save(entity);
    }

    @Transactional
    public T update(T entity) {
        String id = entity.getId();

        // Retrieve existing entity to preserve certain fields if needed
        T existingEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found with id: " + id));

        // Copy the audit fields from the existing entity to preserve them
        entity.setId(id);
        entity.setVersion(existingEntity.getVersion()); // Version will be incremented by JPA due to @Version annotation
        entity.setCreatedBy(existingEntity.getCreatedBy());
        entity.setCreatedTime(existingEntity.getCreatedTime());

        String currentUser = getCurrentUser();
        entity.setModifiedBy(currentUser);
        entity.setModifiedTime(new Date());

        return repository.save(entity);
    }

    @Transactional
    public void delete(String id) {
        repository.deleteById(id);
    }

    /**
     * Method to determine if an entity is new even if it has an ID
     * Override this method if you have custom logic to determine if an entity is new
     */
    protected boolean isNewEntity(T entity) {
        // Default implementation assumes if ID is not null, it's not new
        // Override if you have custom logic
        return false;
    }

    /**
     * Gets the current authenticated user
     * @return Username of the current user or "system" if not authenticated
     */
    protected String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        return "system"; // Fallback if no authenticated user
    }
}
