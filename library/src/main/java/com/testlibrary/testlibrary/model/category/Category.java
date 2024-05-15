package com.testlibrary.testlibrary.model.category;


import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE category SET deleted = true WHERE id = ?")
@DynamicUpdate
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private Set<Book> books;

    @OneToMany
    private Set<User> users;

    private boolean deleted = false;
}
