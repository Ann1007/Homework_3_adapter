package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.exceptions.SmvServerException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface NaturalPersonRequestService {

    ResponseEntity<NaturalPersonRequest> transferClientRequest(NaturalPersonRequest naturalPersonRequest);

    ResponseEntity<ResponseWithFine> getResponse(NaturalPersonRequest naturalPersonRequest);

    void deleteResponse(UUID id);

    ResponseEntity<ResponseWithFine> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest);
}
