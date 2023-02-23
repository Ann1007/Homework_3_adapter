package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.model.ResponseWithFine;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface NaturalPersonRequestService {

    ResponseEntity<NaturalPersonRequest> transferClientRequest(NaturalPersonRequest naturalPersonRequest);

    ResponseEntity<NaturalPersonResponse> getResponse(NaturalPersonRequest naturalPersonRequest);

    ResponseEntity<Void> deleteResponse(UUID id);

    ResponseEntity<NaturalPersonResponse> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest);
}
