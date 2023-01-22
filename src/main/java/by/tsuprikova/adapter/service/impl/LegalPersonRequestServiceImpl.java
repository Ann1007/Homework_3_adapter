package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.time.Duration;


@Service
@RequiredArgsConstructor
public class LegalPersonRequestServiceImpl implements LegalPersonRequestService {

    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder().baseUrl("http://localhost:9000/smv/legal_person").
                build();

    }


    @Override
    public Mono<ResponseEntity<Void>> transferClientRequest(LegalPersonRequest legalPersonRequest) {
        return webClient.
                post().
                uri("/save_request")
                .accept(MediaType.APPLICATION_JSON).
                bodyValue(legalPersonRequest).
                retrieve().
                toEntity(Void.class);

    }


    @Override
    public ResponseEntity<ResponseWithFine> getClientResponseFromSVM(LegalPersonRequest legalPersonRequest) {
        return webClient.post().
                uri("/get_response").
                bodyValue(legalPersonRequest).
                retrieve().
                onStatus(
                        HttpStatus::is5xxServerError,
                        response ->
                                Mono.error(new ResponseWithFineNullException("No information found on "
                                        + legalPersonRequest.getSts() +
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
