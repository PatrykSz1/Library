package com.testlibrary.testlibrary.controller;


import com.testlibrary.testlibrary.mapper.UserMapper;
import com.testlibrary.testlibrary.model.user.User;
import com.testlibrary.testlibrary.model.user.UserCommand;
import com.testlibrary.testlibrary.model.user.UserDto;
import com.testlibrary.testlibrary.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Page<UserDto>> getUsers(Pageable pageable) {
        Page<UserDto> userDtoPage = userService.getUsers(pageable);
        return new ResponseEntity<>(userDtoPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        UserDto userDto = userMapper.toDto(user);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCommand userCommand) {
        User createdUser = userService.createUser((userCommand));
        UserDto createdUserDto = userMapper.toDto(createdUser);
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/unlock")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<UserDto> unlockUser(@PathVariable int id) {
        UserDto updatedUserDto = userMapper.toDto(userService.unlockUser(id));
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @PatchMapping("/{id}/block")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<UserDto> blockUser(@PathVariable int id) {
        UserDto updatedUserDto = userMapper.toDto(userService.blockUser(id));
        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/categories/{categoryId}")
    public ResponseEntity<Void> addCategoryToUserSubscription(@PathVariable("id") int id, @PathVariable("categoryId") int categoryId) {
        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.addCategoryToSubscription(id, categoryId, userMail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}


