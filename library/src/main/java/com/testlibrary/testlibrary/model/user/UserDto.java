package com.testlibrary.testlibrary.model.user;

import com.testlibrary.testlibrary.common.Role;
import com.testlibrary.testlibrary.model.category.CategoryDto;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {

    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean blocked = false;
    private int subscriptionsAmount;
//    private Set<CategoryDto> subscriptions = new HashSet<>();
}