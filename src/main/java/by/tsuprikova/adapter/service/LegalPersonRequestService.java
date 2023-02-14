package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import org.springframework.http.ResponseEntity;

import java.util.UUID;


public interface LegalPersonRequestService {

    ResponseEntity<LegalPersonRequest> transferClientRequest(LegalPersonRequest legalPersonRequest);

    ResponseEntity<ResponseWithFine> getResponse(LegalPersonRequest legalPersonRequest);

    void deleteResponse(UUID id);

    ResponseEntity<ResponseWithFine> getResponseWithFineFromSMV(LegalPersonRequest legalPersonRequest);
}
