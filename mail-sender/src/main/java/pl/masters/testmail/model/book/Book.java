package pl.masters.testmail.model.book;


import jakarta.persistence.ManyToOne;
import lombok.*;
import pl.masters.testmail.model.category.Category;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Book {

    private String title;
    private Category category;
    private boolean availability = false;
}
