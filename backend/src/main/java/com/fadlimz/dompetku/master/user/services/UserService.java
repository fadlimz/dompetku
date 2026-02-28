package com.fadlimz.dompetku.master.user.services;

import com.fadlimz.dompetku.base.services.BaseService;
import com.fadlimz.dompetku.config.StringUtil;
import com.fadlimz.dompetku.master.user.dtos.UserDto;
import com.fadlimz.dompetku.master.user.entities.User;
import com.fadlimz.dompetku.master.user.repositories.UserRepository;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService extends BaseService<User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getLoggedInUser() {
        String identifier = getCurrentUser(); // Inherited from BaseService
        return userRepository.findByUserName(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new RuntimeException("User not found in context: " + identifier));
    }

    public User create(UserDto userDto) {
        User user = userDto.toEntity();
        validation(user);
        
        // Encode and set password if provided
        if (!StringUtil.isBlank(userDto.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        User savedUser = save(user);
        return savedUser;
    }

    public User update(UserDto userDto) {
        User existingUser = findById(userDto.id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userDto.id));

        existingUser.setUserName(userDto.getUserName());
        existingUser.setFullName(userDto.getFullName());
        existingUser.setEmail(userDto.getEmail());

        if (!StringUtil.isBlank(userDto.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        } else {
            // Jika password tidak diupdate, pertahankan password lama
            existingUser.setPassword(existingUser.getPassword());
        }

        validation(existingUser);

        User updatedUser = update(existingUser);
        return updatedUser;
    }

    public void deleteUser(String id) {
        delete(id);
    }

    private void validation(User user) {
        // Validate Email format
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (StringUtil.isBlank(user.getEmail()) || !user.getEmail().matches(emailRegex)) {
            throw new RuntimeException("Format email tidak valid");
        }
        // Validate UserName duplicate
        Optional<User> userByUserName = userRepository.findByUserName(user.getUserName());
        if (userByUserName.isPresent()) {
            User existing = userByUserName.get();
            if (!existing.getId().equals(user.getId())) {
                throw new RuntimeException("UserName " + user.getUserName() + " sudah terdaftar");
            }
        }

        // Validate Email duplicate
        Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            User existing = userByEmail.get();
            if (!existing.getId().equals(user.getId())) {
                throw new RuntimeException("UserName " + user.getEmail() + " sudah terdaftar");
            }
        }
    }
}
