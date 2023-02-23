package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.service.impl.LegalPersonRequestServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LegalPersonServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    private LegalPersonRequestService requestService;

    private LegalPersonRequest request;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setup() {

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        requestService = new LegalPersonRequestServiceImpl(webClient);
        request = new LegalPersonRequest();
        request.setInn(1234567890L);

    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void transferValidNaturalPersonRequestTest() throws Exception {

        MockResponse response = new MockResponse().setBody(objectMapper.writeValueAsString(request)).
                setResponseCode(202).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(response);
        ResponseEntity<LegalPersonRequest> result = requestService.transferClientRequest(request);

        assertThat(result.getBody().getInn(), is(1234567890L));
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));


    }


    @Test
    void getResponseIsNotNullTest() throws Exception {

        LegalPersonResponse response = new LegalPersonResponse();
        BigDecimal amountOfAccrual = new BigDecimal(28);
        BigDecimal amountOfPaid = new BigDecimal(28);
        int numberOfResolution = 321521;
        String articleOfKoap = "21.3";
        response.setInn(1234567890L);
        response.setAmountOfAccrual(amountOfAccrual);
        response.setArticleOfKoap(articleOfKoap);
        response.setAmountOfPaid(amountOfPaid);
        response.setNumberOfResolution(numberOfResolution);

        MockResponse mockResponse = new MockResponse().setBody(objectMapper.writeValueAsString(response)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(mockResponse);
        ResponseEntity<LegalPersonResponse> resultResponse = requestService.getResponse(request);

        assertThat(resultResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(resultResponse.getBody().getInn(), is(1234567890L));
        assertThat(resultResponse.getBody().getAmountOfAccrual(), is(amountOfAccrual));
        assertThat(resultResponse.getBody().getAmountOfPaid(), is(amountOfPaid));
        assertThat(resultResponse.getBody().getArticleOfKoap(), is(articleOfKoap));
        assertThat(resultResponse.getBody().getNumberOfResolution(), is(numberOfResolution));

    }


    @Test
    void getResponseIsNullTest() throws Exception {

        MockResponse mockResponse = new MockResponse().
                setResponseCode(404);

        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);

        ResponseWithFineNullException thrown = assertThrows(ResponseWithFineNullException.class, () -> requestService.getResponse(request));

        String errorMessage = "No information found for '1234567890'";
        Assertions.assertEquals(errorMessage, thrown.getMessage());


    }


    @Test
    void deleteResponseWithFineById() throws Exception {

        UUID id = UUID.randomUUID();
        ResponseEntity<Void> res = new ResponseEntity<>(HttpStatus.OK);
        MockResponse response = new MockResponse().setBody(objectMapper.writeValueAsString(res)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(response);

        ResponseEntity<Void> deleteResult = requestService.deleteResponse(id);
        assertThat(deleteResult.getStatusCode(), is(HttpStatus.OK));

    }


}
