package by.tsuprikova.adapter.integrationTest;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import by.tsuprikova.adapter.service.impl.LegalPersonRequestServiceImpl;
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
public class GetLegalPersonResponseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    @Autowired
    private MockMvc mockMvc;


    private LegalPersonRequestService requestService;


    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setup() {

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        requestService = new LegalPersonRequestServiceImpl(webClient);


    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void getResponseWithFineIntegrationTest() throws Exception {

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

        ResponseEntity<LegalPersonRequest> result = requestService.transferClientRequest(request);
        assertThat(result.getBody().getInn(), is(inn));
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(legalPersonResponse)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json"));
        ResponseEntity<LegalPersonResponse> resultResponse = requestService.getResponse(result.getBody());

        assertThat(resultResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(resultResponse.getBody().getInn(), is(inn));


        ResponseEntity<Void> res = new ResponseEntity<>(HttpStatus.OK);
        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(res)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json"));

        ResponseEntity<Void> deleteResult = requestService.deleteResponse(id);
        assertThat(deleteResult.getStatusCode(), is(HttpStatus.OK));


    }
}
