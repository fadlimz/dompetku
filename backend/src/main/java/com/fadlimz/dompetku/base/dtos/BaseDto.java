package com.fadlimz.dompetku.base.dtos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

import com.fadlimz.dompetku.base.entities.BaseEntity;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseDto implements Serializable {

    public String id;
    public Integer version;
    public String createdBy;
    public Date createdTime;
    public String modifiedBy;
    public Date modifiedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public <E extends BaseEntity> E toEntity(Class<E> classEntity) {
        E entity = null;

        try {
            entity = classEntity.getDeclaredConstructor().newInstance();
            entity.setId(id);
            entity.setVersion(version);
            entity.setCreatedBy(createdBy);
            entity.setCreatedTime(createdTime);
            entity.setModifiedBy(modifiedBy);
            entity.setModifiedTime(modifiedTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return entity;
    }

    // public static <P> P fromEntity(Class<P> classPojo, <E> entity) {
        
        
    //     return null;
    // }

    public static <P extends BaseDto> P fromEntity(Class<P> classDto, Object entity) {
        P dto = null;

        try {
            dto = classDto.getDeclaredConstructor().newInstance();
           
            if (entity instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) entity;
                dto.id = baseEntity.getId();
                dto.version = baseEntity.getVersion();
                dto.createdBy = baseEntity.getCreatedBy();
                dto.createdTime = baseEntity.getCreatedTime();
                dto.modifiedBy = baseEntity.getModifiedBy();
                dto.modifiedTime = baseEntity.getModifiedTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dto;
    }
}
