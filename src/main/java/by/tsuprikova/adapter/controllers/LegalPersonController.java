package by.tsuprikova.adapter.controllers;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/adapter/legal_person")
@RequiredArgsConstructor
@Tag(name="Controller for legal person request", description="accepts a request from an natural person and returns a response with a fine")
public class LegalPersonController {

    private final LegalPersonRequestService legalPersonRequestService;


    @PostMapping("/get_response")
    public ResponseEntity<ResponseWithFine> getResponse(@Valid @RequestBody LegalPersonRequest request) {

        ResponseEntity<ResponseWithFine> responseWithFine = legalPersonRequestService.getResponseWithFineFromSMV(request);

        return responseWithFine;

    }

}
