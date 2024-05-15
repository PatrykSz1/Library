package pl.masters.testmail.mapper;

import org.mapstruct.Mapper;
import pl.masters.testmail.model.book.Book;
import pl.masters.testmail.model.book.BookDto;


@Mapper
public interface BookMapper {

    BookDto toDto(Book book);

    Book toEntity(BookDto bookDto);
}
