package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseNullException;
import by.tsuprikova.adapter.exceptions.SmvServiceException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
public class NaturalPersonRequestServiceImpl implements NaturalPersonRequestService {

    private final WebClient webClient;


    private void transferClientRequest(NaturalPersonRequest naturalPersonRequest) {
        log.info("Sending natural person request with sts ='{}' for saving on smv", naturalPersonRequest.getSts());

        webClient.
                post().
                uri("/natural_person/request").
                bodyValue(naturalPersonRequest).
                retrieve().
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new SmvServiceException("SMV service is unavailable"))).
                toEntity(NaturalPersonRequest.class).
                block();


    }


    public ResponseEntity<NaturalPersonResponse> getResponse(NaturalPersonRequest naturalPersonRequest) {

        log.info("Getting a natural person response with sts ='{}' from SMV", naturalPersonRequest.getSts());

        return webClient.post().
                uri("/natural_person/response").
                bodyValue(naturalPersonRequest).
                retrieve().
                onStatus(
                        HttpStatus::is4xxClientError,
                        response ->
                                Mono.error(new ResponseNullException("No information found for '" + naturalPersonRequest.getSts() + "'"))).
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new SmvServiceException("SMV service is unavailable"))).
                toEntity(NaturalPersonResponse.class).
                retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(throwable -> throwable instanceof ResponseNullException).
                                onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                {
                                    throw new ResponseNullException("No information found for '" + naturalPersonRequest.getSts() + "'");
                                })).block();

    }


    private void deleteResponse(UUID id) {
        log.info("Sending id='{}' for delete natural person response from smv ", id);

        webClient.delete()
                .uri("/natural_person/response/{id}", id).
                retrieve().
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new SmvServiceException("SMV service is unavailable"))).
                toEntity(Void.class).
                block();
    }


    @Override
    public ResponseEntity<NaturalPersonResponse> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest) {

        transferClientRequest(naturalPersonRequest);
        ResponseEntity<NaturalPersonResponse> responseWithFineEntity = getResponse(naturalPersonRequest);

        deleteResponse(responseWithFineEntity.getBody().getId());

        return responseWithFineEntity;

    }

}

