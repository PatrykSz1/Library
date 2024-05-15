package com.testlibrary.testlibrary.model.book;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookDto {

    private int id;
    private String title;
    private int categoryId;
    private boolean availability = true;
}
