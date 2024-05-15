package com.testlibrary.testlibrary.model.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookCommand {

    @NotBlank(message = "Title can not be null")
    private String title;

    @Min(1)
    private int categoryId;

    private boolean availability = true;
}
