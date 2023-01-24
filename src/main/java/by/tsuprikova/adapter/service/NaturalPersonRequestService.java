package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface NaturalPersonRequestService {

    //Mono<NaturalPersonRequest> transferClientRequest(NaturalPersonRequest naturalPersonRequest);

  // Mono<ResponseEntity<ResponseWithFine>> getResponse(NaturalPersonRequest naturalPersonRequest);

    //ResponseEntity<Void> deleteResponse(int id);

    ResponseEntity<ResponseWithFine> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest);
}
