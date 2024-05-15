package com.testlibrary.testlibrary.model.rental;

import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Book book;

    @ManyToOne
    private User user;

    private LocalDate startDate;
    private LocalDate endDate;
}
