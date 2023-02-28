package by.tsuprikova.adapter.controllers;

import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Controller for natural person request", description = "accepts a request from natural person and returns a response with a fine")

@RequestMapping("api/v1/adapter/natural_person")
public class NaturalPersonController {

    private final NaturalPersonRequestService naturalPersonRequestService;


    @Operation(summary = "Get a response by request from natural person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The response is found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NaturalPersonResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "404", description = "The response is not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "SMV service is  is unavailable", content = @Content)})

    @PostMapping(value = "/response")
    public ResponseEntity<NaturalPersonResponse> getResponseWithFine(@Valid @RequestBody NaturalPersonRequest clientReq) {
        return naturalPersonRequestService.getResponseWithFineFromSMV(clientReq);

    }


}
