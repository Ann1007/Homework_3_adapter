package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.exceptions.SmvServiceException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Slf4j
public class NaturalPersonRequestServiceImpl implements NaturalPersonRequestService {

    private final RestTemplate restTemplate;

    private final RetryTemplate retryTemplate;


    public ResponseEntity<NaturalPersonRequest> transferClientRequest(NaturalPersonRequest naturalPersonRequest) {
        log.info("Sending natural person request with sts ='{}' for saving on smv", naturalPersonRequest.getSts());

        ResponseEntity<NaturalPersonRequest> request = null;
        try {
            request = restTemplate.postForEntity("/natural_person/request", naturalPersonRequest, NaturalPersonRequest.class);

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");

        }

        return request;

    }


    public ResponseEntity<NaturalPersonResponse> getResponse(NaturalPersonRequest naturalPersonRequest) {
        log.info("Getting a natural person response with sts ='{}' from SMV", naturalPersonRequest.getSts());

        ResponseEntity<NaturalPersonResponse> response = null;
        try {
            response = retryTemplate.execute(retryContext ->
                    restTemplate.postForEntity("/natural_person/response", naturalPersonRequest, NaturalPersonResponse.class));

        } catch (HttpClientErrorException e) {
            throw new ResponseWithFineNullException("No information found for '" + naturalPersonRequest.getSts() + "'");

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");
        }
        return response;
    }


    public ResponseEntity<Void> deleteResponse(UUID id) {

        log.info("Sending id='{}' for delete natural person response from smv ", id);
        ResponseEntity<Void> responseEntity = null;

        try {
            responseEntity = restTemplate.exchange("/natural_person/response/" + id, HttpMethod.DELETE, null, Void.class, UUID.class);

        } catch (HttpServerErrorException e) {
            throw new SmvServiceException("SMV service is unavailable");

        }
        return responseEntity;
    }


    @Override
    public ResponseEntity<NaturalPersonResponse> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest) {

        ResponseEntity<NaturalPersonRequest> responseEntity = transferClientRequest(naturalPersonRequest);
        ResponseEntity<NaturalPersonResponse> responseWithFineEntity = getResponse(responseEntity.getBody());
        deleteResponse(responseWithFineEntity.getBody().getId());

        return responseWithFineEntity;

    }

}

