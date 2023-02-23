package by.tsuprikova.adapter.controllers;


import by.tsuprikova.adapter.exceptions.SmvServerException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;

import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
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
@Tag(name="Controller for natural person request", description="accepts a request from an natural person and returns a response with a fine")
@RequestMapping("/adapter/natural_person")

public class NaturalPersonController {

    private final NaturalPersonRequestService naturalPersonRequestService;


    @PostMapping(value = "/get_response")
    public ResponseEntity<NaturalPersonResponse> getResponseWithFine(@Valid @RequestBody NaturalPersonRequest clientReq) {
        return naturalPersonRequestService.getResponseWithFineFromSMV(clientReq);

    }


}
