package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;


public interface LegalPersonRequestService {

    ResponseEntity<LegalPersonRequest> transferClientRequest(LegalPersonRequest legalPersonRequest);

    ResponseEntity<LegalPersonResponse> getResponse(LegalPersonRequest legalPersonRequest);

    void deleteResponse(UUID id);

    ResponseEntity<LegalPersonResponse> getResponseWithFineFromSMV(LegalPersonRequest legalPersonRequest);
}
