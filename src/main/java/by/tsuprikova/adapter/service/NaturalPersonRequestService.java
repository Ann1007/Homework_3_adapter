package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface NaturalPersonRequestService {

    ResponseEntity<NaturalPersonResponse> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest) throws Exception;
}
