package pl.masters.testmail.model.user;

import jakarta.persistence.*;
import lombok.*;
import pl.masters.testmail.model.category.Category;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String email;

    @OneToMany
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> subscriptions = new HashSet<>();

    private String role;

    private boolean blocked = false;
}

