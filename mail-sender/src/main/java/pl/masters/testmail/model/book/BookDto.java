package pl.masters.testmail.model.book;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookDto {

    private int id;

    @NotNull(message = "Title can not be null")
    private String title;

    @Min(1)
    private int categoryId;

    private boolean availability = false;
}
