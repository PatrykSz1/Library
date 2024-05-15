package com.testlibrary.testlibrary.mapper;

import com.testlibrary.testlibrary.model.book.Book;
import com.testlibrary.testlibrary.model.category.Category;
import com.testlibrary.testlibrary.model.rental.Rental;
import com.testlibrary.testlibrary.model.rental.RentalDto;
import com.testlibrary.testlibrary.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = {RentalMapperImpl.class})
class RentalMapperTest {

    @Autowired
    private RentalMapper rentalMapper;

    @Test
    void testToDto_shouldMapRentalToDto() {
        Book book = Book.builder()
                .id(1)
                .title("Sample Book")
                .category(createCategory())
                .availability(true)
                .build();

        User user = User.builder()
                .email("john.doe@example.com")
                .build();

        LocalDateTime startDate = LocalDateTime.of(2023, 7, 18, 12, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 7, 25, 12, 0);

        Rental rental = Rental.builder()
                .id(1)
                .book(book)
                .user(user)
                .startDate(LocalDate.from(startDate))
                .endDate(LocalDate.from(endDate))
                .build();

        RentalDto rentalDto = rentalMapper.toDto(rental);


        assertEquals(rentalDto.getId(), rental.getId());
        assertEquals(rentalDto.getBookId(), rental.getBook().getId());
        assertEquals(rentalDto.getUserEmail(), rental.getUser().getEmail());
        assertEquals(rentalDto.getStartDate(), rental.getStartDate());
        assertEquals(rentalDto.getEndDate(), rental.getEndDate());
    }

    private Category createCategory() {
        return Category.builder()
                .id(1)
                .name("Romantic")
                .build();
    }
}
