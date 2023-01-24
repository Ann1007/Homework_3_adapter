package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class NaturalPersonRequestServiceImpl implements NaturalPersonRequestService {

    private WebClient webClient;


    @PostConstruct
    public void init() {
        webClient = WebClient.builder().baseUrl("http://localhost:9000/smv/natural_person").
                        build();

    }


    private Mono<NaturalPersonRequest> transferClientRequest(NaturalPersonRequest naturalPersonRequest) {
        log.info("Sending natural person request with sts ='{}' for saving on smv", naturalPersonRequest.getSts());

        return webClient.
                post().
                uri("/save_request").
                bodyValue(naturalPersonRequest).
                retrieve().
                bodyToMono(NaturalPersonRequest.class);

    }



    private Mono<ResponseEntity<ResponseWithFine>> getResponse(NaturalPersonRequest naturalPersonRequest) {

        return webClient.post().
                uri("/get_response").
                bodyValue(naturalPersonRequest).
                retrieve().
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new ResponseWithFineNullException("No information found for  "
                                        + naturalPersonRequest.getSts() +"' "
                                       ))).
                toEntity(ResponseWithFine.class).
                retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(throwable -> throwable instanceof ResponseWithFineNullException).
                                onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                {
                                    throw new ResponseWithFineNullException("No information found for  "
                                        + naturalPersonRequest.getSts() +"' ");
                                }));

    }



    private ResponseEntity<Void> deleteResponse(int id) {
        log.info("Sending id='{}' for delete natural person response from smv ", id);

        return webClient.delete()
                .uri("/response/{id}", id).
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

