package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface LegalPersonRequestService {

    Mono<ResponseEntity<Void>> transferClientRequest(LegalPersonRequest legalPersonRequest);
    ResponseEntity<ResponseWithFine> getClientResponseFromSVM(LegalPersonRequest legalPersonRequest);
    ResponseEntity<Void> deleteResponse(int id);
}
