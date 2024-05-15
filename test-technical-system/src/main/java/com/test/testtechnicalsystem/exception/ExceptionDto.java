package com.test.testtechnicalsystem.exception;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExceptionDto {

    private String message;
    private final LocalDateTime dateTime = LocalDateTime.now();
}
