package com.test.testtechnicalsystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class MonitorMethodModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String user;
    private long timestamp;
    private String method;
    private LocalDateTime datetime;
}
