package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.exceptions.SmvServiceException;
import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class LegalPersonRequestServiceImpl implements LegalPersonRequestService {

    private final WebClient webClient;


    public ResponseEntity<LegalPersonRequest> transferClientRequest(LegalPersonRequest legalPersonRequest) {
        log.info("sending legal person request with inn ='{}' for saving on smv", legalPersonRequest.getInn());
        return webClient.
                post().
                uri("/legal_person/request")
                .accept(MediaType.APPLICATION_JSON).
                bodyValue(legalPersonRequest).
                retrieve().
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new SmvServiceException("SMV service is unavailable"))).
                toEntity(LegalPersonRequest.class).
                block();

    }


    @Override
    public ResponseEntity<LegalPersonResponse> getResponse(LegalPersonRequest legalPersonRequest) {

        return webClient.post().
                uri("/legal_person/response").
                bodyValue(legalPersonRequest).
                retrieve().
                onStatus(
                        HttpStatus::is4xxClientError,
                        response ->
                                Mono.error(new ResponseWithFineNullException("No information found for '" + legalPersonRequest.getInn()+"'"))).
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new SmvServiceException("SMV service is unavailable"))).
                toEntity(LegalPersonResponse.class).
                retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof ResponseWithFineNullException).
                        onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        {
                            throw new ResponseWithFineNullException("No information found for '" + legalPersonRequest.getInn()+"'");
                        })).block();
    }


    public ResponseEntity<Void> deleteResponse(UUID id) {

        log.info("sending id={} for delete legal person response from smv ", id);

        return webClient.delete()
                .uri("/legal_person/response/{id}", id).
                retrieve().
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new SmvServiceException("SMV service is unavailable"))).
                toEntity(Void.class).
                block();
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
