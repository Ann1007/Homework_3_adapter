package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import org.springframework.http.ResponseEntity;

public interface LegalPersonRequestService {

    ResponseEntity<LegalPersonResponse> getResponseWithFineFromSMV(LegalPersonRequest legalPersonRequest);
}
