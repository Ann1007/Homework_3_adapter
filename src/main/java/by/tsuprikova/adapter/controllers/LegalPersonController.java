package by.tsuprikova.adapter.controllers;


import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
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
public class LegalPersonController {

    private final LegalPersonRequestService legalPersonRequestService;


    @PostMapping("/get_response")
    public ResponseEntity<ResponseWithFine> getResponse(@Valid @RequestBody LegalPersonRequest legalPersonRequest) {

        legalPersonRequestService.transferClientRequest(legalPersonRequest).subscribe();

        ResponseEntity<ResponseWithFine> responseWithFine = legalPersonRequestService.getClientResponseFromSVM(legalPersonRequest);

        if (responseWithFine != null) {
            ResponseEntity<Void> resp = legalPersonRequestService.deleteResponse(responseWithFine.getBody().getId());

        }

        return responseWithFine;

    }

}
