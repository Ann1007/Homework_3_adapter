package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.exceptions.SmvServerException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
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

    public Mono<NaturalPersonRequest> transferClientRequest(NaturalPersonRequest naturalPersonRequest) {
        log.info("Sending natural person request with sts ='{}' for saving on smv", naturalPersonRequest.getSts());

        return webClient.
                post().
                uri("/natural_person/save_request").
                bodyValue(naturalPersonRequest).
                retrieve().
                bodyToMono(NaturalPersonRequest.class);


    }


    public Mono<ResponseEntity<ResponseWithFine>> getResponse(NaturalPersonRequest naturalPersonRequest) {

        return webClient.post().
                uri("/natural_person/get_response").
                bodyValue(naturalPersonRequest).
                retrieve().
                onStatus(
                        HttpStatus::is4xxClientError,
                        response ->
                                Mono.error(new ResponseWithFineNullException("No information found for  "
                                        + naturalPersonRequest.getSts() + "' "
                                ))).
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new SmvServerException("SMV service is  is unavailable"))).
                toEntity(ResponseWithFine.class).
                retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(throwable -> throwable instanceof ResponseWithFineNullException).
                                onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                {
                                    throw new ResponseWithFineNullException("No information found for  "
                                            + naturalPersonRequest.getSts() + "' ");
                                }));

    }


    public ResponseEntity<Void> deleteResponse(UUID id) {
        log.info("Sending id='{}' for delete natural person response from smv ", id);

        return webClient.delete()
                .uri("/natural_person/response/{id}", id).
                retrieve().
                toEntity(Void.class).
                block();
    }


    @Override
    public ResponseEntity<ResponseWithFine> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest) {

        Mono<NaturalPersonRequest> savedRequest = transferClientRequest(naturalPersonRequest);

        Mono<ResponseEntity<ResponseWithFine>> response = savedRequest.flatMap(this::getResponse);

        ResponseEntity<ResponseWithFine> responseEntity = response.block();
        if (responseEntity != null) {
            log.info("Get a natural person request with sts ='{}' from SMV", naturalPersonRequest.getSts());
            deleteResponse(responseEntity.getBody().getId());
        }

        return responseEntity;


    }

}

