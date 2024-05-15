package com.testlibrary.testlibrary.mapper;


import com.testlibrary.testlibrary.model.rental.Rental;
import com.testlibrary.testlibrary.model.rental.RentalDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RentalMapper {

    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "user.id", target = "userId")
    RentalDto toDto(Rental rental);

    Rental toEntity(RentalDto rentalDto);
}
