package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
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


    public Mono<LegalPersonRequest> transferClientRequest(LegalPersonRequest legalPersonRequest) {
        log.info("sending legal person request with sts{} for saving on smv", legalPersonRequest.getSts());
        return webClient.
                post().
                uri("/legal_person/save_request")
                .accept(MediaType.APPLICATION_JSON).
                bodyValue(legalPersonRequest).
                retrieve().
                bodyToMono(LegalPersonRequest.class);

    }


    public Mono<ResponseEntity<ResponseWithFine>> getResponse(LegalPersonRequest legalPersonRequest) {

        return webClient.post().
                uri("/legal_person/get_response").
                bodyValue(legalPersonRequest).
                retrieve().
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new ResponseWithFineNullException("No information found on "
                                        + legalPersonRequest.getSts() +
                                        " try again later"))).
                toEntity(ResponseWithFine.class).
                retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof ResponseWithFineNullException).
                        onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        {
                            throw new ResponseWithFineNullException("No information found for  "
                                    + legalPersonRequest.getSts() + "' ");
                        }));
    }


    public void deleteResponse(UUID id) {
        log.info("sending id={} for delete legal person response from smv ", id);
        webClient.delete()
                .uri("/legal_person/response/{id}", id).
                retrieve().
                toEntity(Void.class).
                block();
    }

    @Override
    public ResponseEntity<ResponseWithFine> getResponseWithFineFromSMV(LegalPersonRequest legalPersonRequest) {

        Mono<LegalPersonRequest> savedRequest = transferClientRequest(legalPersonRequest);
        Mono<ResponseEntity<ResponseWithFine>> response = savedRequest.flatMap(this::getResponse);

        ResponseEntity<ResponseWithFine> responseEntity = response.block();
        if (responseEntity != null) {
            log.info("get a legal person response for sts ='{}' from SMV", legalPersonRequest.getSts());
            deleteResponse(responseEntity.getBody().getId());
        }

        return responseEntity;
    }
}
