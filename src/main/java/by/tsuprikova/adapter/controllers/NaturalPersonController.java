package by.tsuprikova.adapter.controllers;


import by.tsuprikova.adapter.model.NaturalPersonRequest;

import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
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
@RequestMapping("/adapter/natural_person")
@Slf4j
public class NaturalPersonController {

    private final NaturalPersonRequestService naturalPersonRequestService;


    @PostMapping(value = "/get_response")
    public ResponseEntity<ResponseWithFine> getResponseWithFine(@Valid @RequestBody NaturalPersonRequest clientReq) {
        return naturalPersonRequestService.getResponseWithFineFromSMV(clientReq);

    }


}
