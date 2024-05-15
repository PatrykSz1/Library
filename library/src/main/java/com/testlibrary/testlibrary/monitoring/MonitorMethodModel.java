package com.testlibrary.testlibrary.monitoring;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MonitorMethodModel {

    private String user;
    private long timestamp;
    private String method;
    private LocalDateTime datetime;
}