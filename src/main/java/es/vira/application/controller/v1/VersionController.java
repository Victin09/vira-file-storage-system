package es.vira.application.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "API Info")
@RestController
@RequestMapping("/api/v1")
public class VersionController {

    @Value("${application.options.apiVersion}")
    private String apiVersion;

    @Operation(summary = "Returns the API version.")
    @GetMapping(value = "/version")
    public String version() {
        return apiVersion;
    }
}
