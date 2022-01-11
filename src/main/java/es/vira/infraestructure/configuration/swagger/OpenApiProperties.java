package es.vira.infraestructure.configuration.swagger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@NoArgsConstructor
@Getter
@Setter
@Validated
@ConfigurationProperties("application.swagger")
public class OpenApiProperties {
    private String title;
    private String description;
    private String contactEmail;
    private String contactName;
    private String contactUrl;
    private String license;
    private String externalDocDescription;
    private String externalDocUrl;
}
