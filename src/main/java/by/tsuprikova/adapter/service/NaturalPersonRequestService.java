package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface NaturalPersonRequestService {

    Mono<NaturalPersonRequest> transferClientRequest(NaturalPersonRequest naturalPersonRequest);

  Mono<ResponseEntity<ResponseWithFine>> getResponse(NaturalPersonRequest naturalPersonRequest);

    ResponseEntity<Void> deleteResponse(UUID id);

    ResponseEntity<ResponseWithFine> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest);
}
