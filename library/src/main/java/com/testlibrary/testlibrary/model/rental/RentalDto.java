package com.testlibrary.testlibrary.model.rental;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RentalDto {

    private int id;
    private int bookId;
    private int userId;
    private String userEmail;
    private LocalDate startDate;
    private LocalDate endDate;
}
