package com.testlibrary.testlibrary.exception;


import com.testlibrary.testlibrary.exception.book.BookCreationException;
import com.testlibrary.testlibrary.exception.book.EntityIsUnavailableException;
import com.testlibrary.testlibrary.exception.model.ExceptionDto;
import com.testlibrary.testlibrary.exception.model.ValidationExceptionDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = "Data integrity violation occurred.";

        if (ex.getMessage().contains("ConstraintViolationException")) {
            if (ex.getMessage().contains("name_UNIQUE")) {
                errorMessage = "Category with this name already exists.";
            } else {
                errorMessage = "Other constraint violation occurred.";
            }
        }
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityIsUnavailableException.class)
    public ResponseEntity<String> handleUnavailableBookException(EntityIsUnavailableException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ExceptionDto(ex.getMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleEntityNotFoundException(EntityExistsException ex) {
        return new ExceptionDto(ex.getMessage());
    }

    @ExceptionHandler(BookCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleCreationException(BookCreationException ex) {
        return new ExceptionDto(ex.getMessage());
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ExceptionDto(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleIllegalStateException(IllegalStateException ex) {
        return new ExceptionDto(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return new ExceptionDto(ex.getMethodParameter().getParameterName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleMethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        ValidationExceptionDto exceptionDto = new ValidationExceptionDto();
        ex.getFieldErrors()
                .forEach(fieldError -> exceptionDto.addViolations(fieldError.getField(), fieldError.getDefaultMessage()));
        return exceptionDto;
    }
}
