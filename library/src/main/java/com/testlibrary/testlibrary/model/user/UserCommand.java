package com.testlibrary.testlibrary.model.user;


import com.testlibrary.testlibrary.common.Role;
import com.testlibrary.testlibrary.model.category.CategoryDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserCommand {

    @NotNull(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Password can not be null")
    private String password;

    @NotBlank(message = "First Name can not be null")
    @Pattern(regexp = "[A-ZŁŻ][a-ząćęłńóśźż]+", message = "Invalid first name format. The name should start with an uppercase letter followed by lowercase letters.")
    private String firstName;

    @NotBlank(message = "Last Name can not be null")
    @Pattern(regexp = "[A-ZŁŻ][a-ząćęłńóśźż]+", message = "Invalid last name format. The name should start with an uppercase letter followed by lowercase letters.")
    private String lastName;

    @NotNull(message = "Role can not be null")
    private Role role;

    private boolean blocked = false;

    private Set<CategoryDto> subscriptions = new HashSet<>();
}
