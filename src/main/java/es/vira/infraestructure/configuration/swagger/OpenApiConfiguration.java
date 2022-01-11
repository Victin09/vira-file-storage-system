package es.vira.infraestructure.configuration.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfiguration {

    private final String apiVersion;
    private final OpenApiProperties openApiProperties;

    public OpenApiConfiguration(@Value("${application.options.apiVersion}") String apiVersion,
                                OpenApiProperties openApiProperties) {
        this.apiVersion = apiVersion;
        this.openApiProperties = openApiProperties;
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("vira-rest-api")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .info(new Info().title(openApiProperties.getTitle())
                        .description(openApiProperties.getDescription())
                        .version(apiVersion)
                        .license(new License().name(openApiProperties.getLicense()).url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description(openApiProperties.getExternalDocDescription())
                        .url(openApiProperties.getExternalDocUrl()));
    }
}
