package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseNullException;
import by.tsuprikova.adapter.exceptions.SmvServiceException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
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
public class NaturalPersonRequestServiceImpl implements NaturalPersonRequestService {

    private final RestTemplate restTemplate;

    private final RetryTemplate retryTemplate;


    private void transferClientRequest(NaturalPersonRequest naturalPersonRequest) {
        log.info("Sending natural person request with sts ='{}' for saving on smv", naturalPersonRequest.getSts());

        try {
            restTemplate.postForEntity("/natural_person/request", naturalPersonRequest, NaturalPersonRequest.class);

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");

        }


    }


    private ResponseEntity<NaturalPersonResponse> getResponse(NaturalPersonRequest naturalPersonRequest) {
        log.info("Getting a natural person response with sts ='{}' from SMV", naturalPersonRequest.getSts());

        ResponseEntity<NaturalPersonResponse> response = null;
        try {
            response = retryTemplate.execute(retryContext ->
                    restTemplate.postForEntity("/natural_person/response", naturalPersonRequest, NaturalPersonResponse.class));

        } catch (HttpClientErrorException e) {
            throw new ResponseNullException("No information found for sts='" + naturalPersonRequest.getSts() + "'");

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");
        }
        return response;
    }


    private void deleteResponse(UUID id) {

        log.info("Sending id='{}' for delete natural person response from smv ", id);

        try {
            restTemplate.exchange("/natural_person/response/" + id, HttpMethod.DELETE, null, Void.class, UUID.class);

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");

        }

    }


    @Override
    public ResponseEntity<NaturalPersonResponse> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest) {

        transferClientRequest(naturalPersonRequest);
        ResponseEntity<NaturalPersonResponse> responseWithFineEntity = getResponse(naturalPersonRequest);
        deleteResponse(responseWithFineEntity.getBody().getId());

        return responseWithFineEntity;

    }

}

