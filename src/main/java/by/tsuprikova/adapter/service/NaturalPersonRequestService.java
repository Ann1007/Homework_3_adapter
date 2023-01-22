package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface NaturalPersonRequestService {

    Mono<ResponseEntity<Void>> transferClientRequest(NaturalPersonRequest naturalPersonRequest);

    ResponseEntity<ResponseWithFine> getClientResponseFromSVM(NaturalPersonRequest naturalPersonRequest);

    ResponseEntity<Void> deleteResponse(int id);

}
