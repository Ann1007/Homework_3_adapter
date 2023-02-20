package by.tsuprikova.adapter.integrationTest;


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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GetNaturalPersonResponseIntegrationTest {

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
    void setup() {

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        requestService = new NaturalPersonRequestServiceImpl(webClient);


    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getResponseWithFineIntegrationTest() throws Exception {

       NaturalPersonRequest naturalPersonRequest = new NaturalPersonRequest();
        String sts = "59 ут 123456";
        naturalPersonRequest.setSts(sts);


        ResponseWithFine responseWithFine = new ResponseWithFine();
        UUID id = UUID.randomUUID();
        responseWithFine.setSts(sts);
        responseWithFine.setAmountOfAccrual(new BigDecimal(28));
        responseWithFine.setArticleOfKoap("21.3");
        responseWithFine.setAmountOfPaid(new BigDecimal(28));
        responseWithFine.setNumberOfResolution(321521);
        responseWithFine.setId(id);


        mockWebServer.enqueue(new MockResponse().
                setBody(objectMapper.writeValueAsString(naturalPersonRequest)).
                setResponseCode(202).
                addHeader("Content-Type", "application/json"));

        ResponseEntity<NaturalPersonRequest> result = requestService.transferClientRequest(naturalPersonRequest);
        assertThat(result.getBody().getSts(), is(sts));
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(responseWithFine)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json"));

        ResponseEntity<ResponseWithFine> resultResponse = requestService.getResponse(result.getBody());

        assertThat(resultResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(resultResponse.getBody().getSts(), is(sts));


        ResponseEntity<Void> res = new ResponseEntity<>(HttpStatus.OK);
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(res)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json"));

        ResponseEntity<Void> deleteResult = requestService.deleteResponse(id);
        assertThat(deleteResult.getStatusCode(),is(HttpStatus.OK));


    }
}
