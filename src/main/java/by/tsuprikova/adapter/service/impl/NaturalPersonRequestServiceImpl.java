package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.time.Duration;


@Service
@RequiredArgsConstructor

public class NaturalPersonRequestServiceImpl implements NaturalPersonRequestService {

    private WebClient webClient;


    @PostConstruct
    public void init() {
        webClient = WebClient.builder().baseUrl("http://localhost:9000/smv/natural_person").build();

    }


    @Override
    public Mono<ResponseEntity<Void>> transferClientRequest(NaturalPersonRequest naturalPersonRequest) {
        return webClient.
                post().
                uri("/save_request")
                .accept(MediaType.APPLICATION_JSON).
                bodyValue(naturalPersonRequest).
                retrieve().
                toEntity(Void.class);

    }


    @Override
    public ResponseEntity<ResponseWithFine> getClientResponseFromSVM(NaturalPersonRequest naturalPersonRequest) {

        return webClient.post().
                uri("/get_response").
                bodyValue(naturalPersonRequest).
                retrieve().
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new ResponseWithFineNullException("No information found on "
                                        + naturalPersonRequest.getSts() +
                                        " try again later"))).
                toEntity(ResponseWithFine.class).
                retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                                .filter(throwable -> throwable instanceof ResponseWithFineNullException))
                .block();
    }


    @Override
    public ResponseEntity<Void> deleteResponse(int id) {

        return webClient.delete()
                .uri("/response/{id}", id).
                retrieve().
                toEntity(Void.class).
                block();
    }


}
