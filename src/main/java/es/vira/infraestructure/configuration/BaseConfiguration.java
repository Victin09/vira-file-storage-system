package es.vira.infraestructure.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
@PropertySource({"classpath:base.properties", "classpath:swagger.properties", "classpath:security.properties"})
@ComponentScan("es.vira")
public class BaseConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
