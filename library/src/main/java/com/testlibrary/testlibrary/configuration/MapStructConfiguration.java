package com.testlibrary.testlibrary.configuration;

import com.testlibrary.testlibrary.mapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapStructConfiguration {

    @Bean
    public BookMapper bookMapper() {
        return new BookMapperImpl();
    }

    @Bean
    public RentalMapper rentalMapper() {
        return new RentalMapperImpl();
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapperImpl();
    }

    @Bean
    public CategoryMapper categoryMapper() {
        return new CategoryMapperImpl();
    }
}
