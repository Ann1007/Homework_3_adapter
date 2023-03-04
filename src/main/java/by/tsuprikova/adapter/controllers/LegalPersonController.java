package by.tsuprikova.adapter.controllers;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "Controller for legal person request", description = "accepts a request from legal person and returns a response with a fine")

@RequestMapping("api/v1/adapter/legal_person")
public class LegalPersonController {

    private final LegalPersonRequestService legalPersonRequestService;

    @Operation(summary = "Get a response by request from legal person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The response is found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LegalPersonResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "The response is not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "SMV service is  is unavailable", content = @Content)})

    @PostMapping("/response")
    public ResponseEntity<LegalPersonResponse> getResponse(@RequestBody(description = "legal person request", required = true,
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = LegalPersonRequest.class)),
                    @Content(mediaType = "application/xml",
                            schema = @Schema(implementation = LegalPersonRequest.class))})
                                                           @Valid @org.springframework.web.bind.annotation.RequestBody LegalPersonRequest request) {

        return legalPersonRequestService.getResponseWithFineFromSMV(request);

    }

}
