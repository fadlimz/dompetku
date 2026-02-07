package com.fadlimz.dompetku.master.user.controllers;

import com.fadlimz.dompetku.master.user.dtos.UserDto;
import com.fadlimz.dompetku.master.user.entities.User;
import com.fadlimz.dompetku.master.user.services.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> dtoList = UserDto.fromEntityList(userService.findAll());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable String id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        UserDto dto = UserDto.fromEntity(user);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        User user = userService.create(userDto);
        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @PutMapping
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto) {
        User user = userService.update(userDto);
        return ResponseEntity.ok(UserDto.fromEntity(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
