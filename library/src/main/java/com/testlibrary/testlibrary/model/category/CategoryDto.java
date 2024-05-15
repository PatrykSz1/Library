package com.testlibrary.testlibrary.model.category;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryDto {

    private int id;
    private String name;
    private int bookAmount;
}
