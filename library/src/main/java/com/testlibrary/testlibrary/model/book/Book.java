package com.testlibrary.testlibrary.model.book;

import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.rental.Rental;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE book SET availability = false WHERE id = ?")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String title;

    @ManyToOne
    private Category category;

    private boolean availability;

    @OneToMany(mappedBy = "book")
    private Set<Rental> rentals;
}
