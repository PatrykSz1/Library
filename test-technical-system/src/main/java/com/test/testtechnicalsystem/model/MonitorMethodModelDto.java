package com.test.testtechnicalsystem.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MonitorMethodModelDto {


    @NotBlank(message = "User cannot be empty")
    private String user;

    @NotBlank(message = "Timestamp cannot be empty")
    private long timestamp;

    @NotBlank(message = "Method cannot be empty")
    private String method;

    @NotBlank(message = "User cannot be empty")
    @FutureOrPresent(message = "Time has to be in the future or present time")
    private LocalDateTime datetime;
}
