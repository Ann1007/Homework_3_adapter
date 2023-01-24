package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import org.springframework.http.ResponseEntity;


public interface LegalPersonRequestService {

    //Mono<ResponseEntity<Void>> transferClientRequest(LegalPersonRequest legalPersonRequest);
    //ResponseEntity<ResponseWithFine> getClientResponseFromSVM(LegalPersonRequest legalPersonRequest);
    //ResponseEntity<Void> deleteResponse(int id);
    ResponseEntity<ResponseWithFine> getResponseWithFineFromSMV(LegalPersonRequest legalPersonRequest);
}
