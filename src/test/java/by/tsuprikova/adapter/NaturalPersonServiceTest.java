package by.tsuprikova.adapter;

import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import by.tsuprikova.adapter.service.impl.NaturalPersonRequestServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NaturalPersonServiceTest {


    @LocalServerPort
    private int port;


    @Autowired
    private ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    private NaturalPersonRequestService requestService;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setup(){

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/smv").toString()).build();
        requestService = new NaturalPersonRequestServiceImpl(webClient);


    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void transferRequestTest() throws Exception {

        NaturalPersonRequest naturalPersonRequest = new NaturalPersonRequest();
        String sts = "59 ут 123456";
        naturalPersonRequest.setSts(sts);

        MockResponse response = new MockResponse().setBody(objectMapper.writeValueAsString(naturalPersonRequest)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(response);
        Mono<NaturalPersonRequest> resultMono = requestService.transferClientRequest(naturalPersonRequest);
        assertThat(resultMono.block().getSts(), is(sts));

    }


    @Test
    void getResponseIsNotNullTest() throws Exception {

        NaturalPersonRequest naturalPersonRequest = new NaturalPersonRequest();
        String sts = "59 ут 123456";
        naturalPersonRequest.setSts(sts);

        ResponseWithFine responseWithFine = new ResponseWithFine();
        BigDecimal amountOfAccrual = new BigDecimal(28);
        BigDecimal amountOfPaid = new BigDecimal(28);
        int numberOfResolution = 321521;
        String articleOfKoap = "21.3";
        responseWithFine.setSts(sts);
        responseWithFine.setAmountOfAccrual(amountOfAccrual);
        responseWithFine.setArticleOfKoap(articleOfKoap);
        responseWithFine.setAmountOfPaid(amountOfPaid);
        responseWithFine.setNumberOfResolution(numberOfResolution);

        ResponseEntity<ResponseWithFine> res = new ResponseEntity<>(responseWithFine, HttpStatus.OK);

        MockResponse retryResponse = new MockResponse().setResponseCode(500);
        mockWebServer.enqueue(retryResponse);

        MockResponse mockResponse = new MockResponse().setBody(objectMapper.writeValueAsString(res)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(mockResponse);
        Mono<ResponseEntity<ResponseWithFine>> resultResponse = requestService.getResponse(naturalPersonRequest);

        assertThat(resultResponse.block().getStatusCode(), is(HttpStatus.OK));



    }



    @Test
    void deleteResponseWithFineById() throws Exception {

        UUID id = UUID.randomUUID();
        ResponseEntity<Void> res = new ResponseEntity<>(HttpStatus.OK);
        MockResponse response = new MockResponse().setBody(objectMapper.writeValueAsString(res)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(response);
        ResponseEntity<Void> result = requestService.deleteResponse(id);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

    }

}
