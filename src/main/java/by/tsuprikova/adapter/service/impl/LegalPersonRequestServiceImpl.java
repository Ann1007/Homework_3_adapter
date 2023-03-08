package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.exceptions.SmvServiceException;
import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class LegalPersonRequestServiceImpl implements LegalPersonRequestService {

    private final RestTemplate restTemplate;

    private final RetryTemplate retryTemplate;


    public ResponseEntity<LegalPersonRequest> transferClientRequest(LegalPersonRequest legalPersonRequest) {
        log.info("sending legal person request with inn ='{}' for saving on smv", legalPersonRequest.getInn());
        ResponseEntity<LegalPersonRequest> request = null;
        try {

            request = retryTemplate.execute(retry ->
                    restTemplate.postForEntity("/legal_person/request", legalPersonRequest, LegalPersonRequest.class));

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");

        }
        return request;
    }


    @Override
    public ResponseEntity<LegalPersonResponse> getResponse(LegalPersonRequest legalPersonRequest) {
        ResponseEntity<LegalPersonResponse> response = null;
        try {
            response = retryTemplate.execute(retryContext ->
                    restTemplate.postForEntity("/legal_person/response", legalPersonRequest, LegalPersonResponse.class));

        } catch (HttpClientErrorException e) {
            throw new ResponseWithFineNullException("No information found for '" + legalPersonRequest.getInn() + "'");

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");
        }
        return response;

    }


    public ResponseEntity<Void> deleteResponse(UUID id) {

        log.info("sending id={} for delete legal person response from smv ", id);
        ResponseEntity<Void> responseEntity = null;

        try {
            responseEntity = retryTemplate.execute(retry ->
                    restTemplate.exchange("/legal_person/response/" + id, HttpMethod.DELETE, null, Void.class, UUID.class));

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");

        }
        return responseEntity;

    }


    @Override
    public ResponseEntity<LegalPersonResponse> getResponseWithFineFromSMV(LegalPersonRequest legalPersonRequest) {

        ResponseEntity<LegalPersonRequest> savedRequest = transferClientRequest(legalPersonRequest);
        ResponseEntity<LegalPersonResponse> responseWithFineEntity = null;

        if (savedRequest.getStatusCode() == HttpStatus.ACCEPTED) {

            responseWithFineEntity = getResponse(legalPersonRequest);
            log.info("get a legal person response for INN ='{}' from SMV", legalPersonRequest.getInn());

            if (responseWithFineEntity != null) {
                deleteResponse(responseWithFineEntity.getBody().getId());
            }
        }

        return responseWithFineEntity;
    }
}
