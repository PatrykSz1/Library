package pl.masters.testmail.model.user;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pl.masters.testmail.model.category.CategoryDto;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {

    private int id;

    @NotNull(message = "Email is required")
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role can not be null")
    private String role;

    private Set<CategoryDto> subscriptions;

    private boolean blocked = false;
}