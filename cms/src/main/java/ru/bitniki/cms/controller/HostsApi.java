package ru.bitniki.cms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;
import ru.bitniki.cms.controller.model.ErrorResponse;
import ru.bitniki.cms.controller.model.HostResponse;

@SuppressWarnings("checkstyle:LineLength")
@Validated
public interface HostsApi {

    @Operation(summary = "Get all hosts", tags = { "hosts" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "An existing hosts", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HostResponse.class)))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(
            value = "/hosts",
            produces = { "application/json" },
            method = RequestMethod.GET
    )
    Mono<ResponseEntity<List<HostResponse>>> hostsGet();


    @Operation(summary = "Get host by id", tags = { "hosts" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "An existing host with given id", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HostResponse.class)))),
        @ApiResponse(responseCode = "404", description = "Host not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(
            value = "/hosts/{id}",
            produces = { "application/json" },
            method = RequestMethod.GET
    )
    Mono<ResponseEntity<HostResponse>> hostsIdGet(@Parameter(in = ParameterIn.PATH, description = "host id", required = true, schema = @Schema()) @PathVariable("id") Long id);

}

