package com.testlibrary.testlibrary.model.category;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryCommand {

    @NotBlank(message = "Category cannot be null")
    @Pattern(regexp = "[A-ZŁŻ][a-ząćęłńóśźż]+", message = "Invalid category format. The name should start with an uppercase letter followed by lowercase letters.")
    private String name;
}
