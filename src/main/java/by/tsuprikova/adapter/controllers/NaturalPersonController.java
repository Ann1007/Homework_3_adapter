package by.tsuprikova.adapter.controllers;

import by.tsuprikova.adapter.model.NaturalPersonRequest;

import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/adapter")
public class NaturalPersonController {

    private final NaturalPersonRequestService naturalPersonRequestService;


    @PostMapping(value = "/natural_person")
    public ResponseEntity<ResponseWithFine> getResponseWithFine(@Valid @RequestBody NaturalPersonRequest clientReq) {

        naturalPersonRequestService.transferClientRequest(clientReq).subscribe();

        ResponseEntity<ResponseWithFine> responseWithFine = naturalPersonRequestService.getClientResponseFromSVM(clientReq);

        if (responseWithFine != null) {
            ResponseEntity<Void> resp = naturalPersonRequestService.deleteResponse(responseWithFine.getBody().getId());


        }

        return responseWithFine;

    }


}

