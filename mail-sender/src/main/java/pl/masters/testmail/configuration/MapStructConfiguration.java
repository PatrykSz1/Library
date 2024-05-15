package pl.masters.testmail.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.masters.testmail.mapper.*;

@Configuration
public class MapStructConfiguration {

    @Bean
    public UserMapper userMapperImpl() {
        return new UserMapperImpl();
    }

    @Bean
    public MessageMapper messageMapperImpl() {
        return new MessageMapperImpl();
    }

    @Bean
    public BookMapper bookMapperImpl() {
        return new BookMapperImpl();
    }

    @Bean
    public CategoryMapper categoryMapper() {
        return new CategoryMapperImpl();
    }
}
