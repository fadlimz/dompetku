package com.fadlimz.dompetku.master.user.dtos;

import com.fadlimz.dompetku.base.dtos.BaseDto;
import com.fadlimz.dompetku.master.user.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserDto extends BaseDto {

    public String userName;
    public String fullName;
    public String email;
    public String password;

    /**
     * Converts this DTO to an Entity
     */
    public User toEntity() {
        User user = super.toEntity(User.class);
        user.setUserName(userName);
        user.setFullName(fullName);
        user.setEmail(email);
        // Password will be handled separately in the service layer for security

        return user;
    }

    /**
     * Converts an Entity to DTO
     */
    public static UserDto fromEntity(User entity) {
        if (entity == null) return null;
        UserDto dto = BaseDto.fromEntity(UserDto.class, entity);
        dto.userName = entity.getUserName();
        dto.fullName = entity.getFullName();
        dto.email = entity.getEmail();

        return dto;
    }

    /**
     * Converts a list of DTOs to a list of Entities
     */
    public static List<User> toEntityList(List<UserDto> dtos) {
        return dtos.stream()
                .map(dto -> dto.toEntity())
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of Entities to a list of DTOs
     */
    public static List<UserDto> fromEntityList(List<User> entities) {
        return entities.stream()
                .map(entity -> UserDto.fromEntity(entity))
                .collect(Collectors.toList());
    }
}
