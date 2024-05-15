package com.testlibrary.testlibrary.controller;

import com.testlibrary.testlibrary.mapper.RentalMapper;
import com.testlibrary.testlibrary.model.rental.RentalCommand;
import com.testlibrary.testlibrary.model.rental.Rental;
import com.testlibrary.testlibrary.model.rental.RentalDto;
import com.testlibrary.testlibrary.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final RentalMapper rentalMapper;

    @GetMapping
    public ResponseEntity<Page<RentalDto>> getAllRentals(Pageable pageable) {
        Page<Rental> rentalPage = rentalService.getAllRentals(pageable);
        Page<RentalDto> rentalDtoPage = rentalPage.map(rentalMapper::toDto);
        return new ResponseEntity<>(rentalDtoPage, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDto> getRentalById(@PathVariable int id) {
        RentalDto rentalDto = rentalMapper.toDto(rentalService.getRentalById(id));
        return new ResponseEntity<>(rentalDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<RentalDto> createRental(@RequestBody @Valid RentalCommand rentalCommand) {
        Rental createdRental = rentalService.createRental((rentalCommand));
        RentalDto createdRentalDto = rentalMapper.toDto(createdRental);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRentalDto);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<RentalDto> updateRental(@PathVariable int id, @RequestBody RentalCommand rentalCommand) {
        Rental updatedRental = rentalService.updateRental(id, (rentalCommand));
        RentalDto updatedRentalDto = rentalMapper.toDto(updatedRental);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRentalDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<Void> deleteRental(@PathVariable int id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}
