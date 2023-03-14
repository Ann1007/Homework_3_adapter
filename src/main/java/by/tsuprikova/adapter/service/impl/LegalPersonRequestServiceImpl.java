package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseNullException;
import by.tsuprikova.adapter.exceptions.SmvServiceException;
import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class LegalPersonRequestServiceImpl implements LegalPersonRequestService {

    private final RestTemplate restTemplate;

    private final RetryTemplate retryTemplate;


    private void transferClientRequest(LegalPersonRequest legalPersonRequest) {
        log.info("sending legal person request with inn ='{}' for saving on smv", legalPersonRequest.getInn());

        try {

            restTemplate.postForEntity("/legal_person/request", legalPersonRequest, LegalPersonRequest.class);

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");

        }

    }



    private ResponseEntity<LegalPersonResponse> getResponse(LegalPersonRequest legalPersonRequest) {
        log.info("Getting a legal person response for inn ='{}' from SMV", legalPersonRequest.getInn());
        ResponseEntity<LegalPersonResponse> response = null;
        try {
            response = retryTemplate.execute(retryContext ->
                    restTemplate.postForEntity("/legal_person/response", legalPersonRequest, LegalPersonResponse.class));

        } catch (HttpClientErrorException e) {
            throw new ResponseNullException("No information found for inn='" + legalPersonRequest.getInn() + "'");

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");
        }
        return response;

    }


    private void deleteResponse(UUID id) {

        log.info("sending id={} for delete legal person response from smv ", id);

        try {
            restTemplate.exchange("/legal_person/response/" + id, HttpMethod.DELETE, null, Void.class, UUID.class);

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");

        }

    }


    @Override
    public ResponseEntity<LegalPersonResponse> getResponseWithFineFromSMV(LegalPersonRequest legalPersonRequest) {

        transferClientRequest(legalPersonRequest);
        ResponseEntity<LegalPersonResponse> responseWithFineEntity = getResponse(legalPersonRequest);

        deleteResponse(responseWithFineEntity.getBody().getId());

        return responseWithFineEntity;
    }
}
