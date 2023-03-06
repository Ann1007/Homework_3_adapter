package by.tsuprikova.adapter.integrationTest;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import by.tsuprikova.adapter.service.impl.LegalPersonRequestServiceImpl;
import by.tsuprikova.adapter.service.impl.NaturalPersonRequestServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    @Autowired
    private MockMvc mockMvc;

    private LegalPersonRequestService legalPersonRequestService;
    private NaturalPersonRequestService requestService;


    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setup() {

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        legalPersonRequestService = new LegalPersonRequestServiceImpl(webClient);
        requestService = new NaturalPersonRequestServiceImpl(webClient);

    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void getLegalPersonResponseTest() throws Exception {

        LegalPersonRequest request = new LegalPersonRequest();
        Long inn = 1234567890L;
        request.setInn(inn);

        UUID id = UUID.randomUUID();
        LegalPersonResponse legalPersonResponse = new LegalPersonResponse();
        BigDecimal amountOfAccrual = new BigDecimal(28);
        BigDecimal amountOfPaid = new BigDecimal(28);
        int numberOfResolution = 321521;
        String articleOfKoap = "21.3";
        legalPersonResponse.setInn(inn);
        legalPersonResponse.setAmountOfAccrual(amountOfAccrual);
        legalPersonResponse.setArticleOfKoap(articleOfKoap);
        legalPersonResponse.setAmountOfPaid(amountOfPaid);
        legalPersonResponse.setNumberOfResolution(numberOfResolution);

        mockWebServer.enqueue(new MockResponse().
                setBody(objectMapper.writeValueAsString(request)).
                setResponseCode(202).
                addHeader("Content-Type", "application/json"));

        ResponseEntity<LegalPersonRequest> result = legalPersonRequestService.transferClientRequest(request);
        assertThat(result.getBody().getInn(), is(inn));
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(legalPersonResponse)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json"));
        ResponseEntity<LegalPersonResponse> resultResponse = legalPersonRequestService.getResponse(result.getBody());

        assertThat(resultResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(resultResponse.getBody().getInn(), is(inn));


        ResponseEntity<Void> res = new ResponseEntity<>(HttpStatus.OK);
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(res)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json"));

        ResponseEntity<Void> deleteResult = legalPersonRequestService.deleteResponse(id);
        assertThat(deleteResult.getStatusCode(), is(HttpStatus.OK));

    }


    @Test
    void getNaturalPersonResponseTest() throws Exception {

        NaturalPersonRequest naturalPersonRequest = new NaturalPersonRequest();
        String sts = "59 ут 123456";
        naturalPersonRequest.setSts(sts);

        NaturalPersonResponse responseWithFine = new NaturalPersonResponse();
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
        ResponseEntity<NaturalPersonResponse> resultResponse = requestService.getResponse(result.getBody());

        assertThat(resultResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(resultResponse.getBody().getSts(), is(sts));


        ResponseEntity<Void> res = new ResponseEntity<>(HttpStatus.OK);
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(res)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json"));

        ResponseEntity<Void> deleteResult = requestService.deleteResponse(id);
        assertThat(deleteResult.getStatusCode(), is(HttpStatus.OK));


    }



}
