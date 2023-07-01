package by.tsuprikova.adapter.service.impl;

import by.tsuprikova.adapter.exceptions.ResponseNullException;
import by.tsuprikova.adapter.exceptions.SmvServiceException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@Service
@AllArgsConstructor
@Slf4j
public class NaturalPersonRequestServiceImpl implements NaturalPersonRequestService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<NaturalPersonResponse> getResponseWithFineFromSMV(NaturalPersonRequest naturalPersonRequest) throws Exception {

        String res1 = "{\n" +
                "    \"id\": \"111aab7b-7f4b-4852-8784-55d4b2482e31\",\n" +
                "    \"amountOfAccrual\": 22,\n" +
                "    \"amountOfPaid\": 22,\n" +
                "    \"numberOfResolution\": 0,\n" +
                "    \"dateOfResolution\": 22,\n" +
                "    \"articleOfKoap\": 22,\n" +
                "    \"sts\": \"11 11\"\n" +
                "}";

        WireMock.configureFor("localhost", 9000);
        stubFor(WireMock.post(urlEqualTo("/api/v1/smv/natural_person/request"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(res1)));

        HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:9000/api/v1/smv/natural_person/request"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(naturalPersonRequest)))
                .build();

        HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        NaturalPersonResponse npr = objectMapper.readValue(res.body(), NaturalPersonResponse.class);

        log.info("get a res with '{}'", naturalPersonRequest.getSts());
        return new ResponseEntity<>(npr, HttpStatus.valueOf(res.statusCode()));

    }

}

