package com.testlibrary.testlibrary.model.rental;


import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RentalCommand {

    @Min(value = 1, message = "Value cannot be less than 1!")
    private int bookId;

    @Min(value = 1, message = "Value cannot be less than 1!")
    private int userId;

    @Email
    private String userEmail;

    @NotNull(message = "Date cannot be null")
    @FutureOrPresent(message = "Start date has to be planned in the present or future time")
    private LocalDate startDate;

    @NotNull(message = "Date cannot be null")
    @Future(message = "End date has to be in the future time")
    private LocalDate endDate;

    @AssertTrue(message = "End date cannot be before the start date")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }
}
