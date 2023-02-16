package by.tsuprikova.adapter;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.ResponseWithFine;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
import by.tsuprikova.adapter.service.impl.NaturalPersonRequestServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NaturalPersonServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    private NaturalPersonRequestService requestService;

    private NaturalPersonRequest naturalPersonRequest;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setup() {

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/smv").toString()).build();
        requestService = new NaturalPersonRequestServiceImpl(webClient);
        naturalPersonRequest = new NaturalPersonRequest();
        String sts = "59 ут 123456";
        naturalPersonRequest.setSts(sts);

    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void transferValidNaturalPersonRequestTest() throws Exception {

        MockResponse response = new MockResponse().setBody(objectMapper.writeValueAsString(naturalPersonRequest)).
                setResponseCode(202).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(response);
        ResponseEntity<NaturalPersonRequest> result = requestService.transferClientRequest(naturalPersonRequest);

        assertThat(result.getBody().getSts(), is("59 ут 123456"));
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));

    }


    @Test
    void getResponseIsNotNullTest() throws Exception {

        ResponseWithFine responseWithFine = new ResponseWithFine();
        BigDecimal amountOfAccrual = new BigDecimal(28);
        BigDecimal amountOfPaid = new BigDecimal(28);
        int numberOfResolution = 321521;
        String articleOfKoap = "21.3";
        responseWithFine.setSts("59 ут 123456");
        responseWithFine.setAmountOfAccrual(amountOfAccrual);
        responseWithFine.setArticleOfKoap(articleOfKoap);
        responseWithFine.setAmountOfPaid(amountOfPaid);
        responseWithFine.setNumberOfResolution(numberOfResolution);

        MockResponse mockResponse = new MockResponse().setBody(objectMapper.writeValueAsString(responseWithFine)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(mockResponse);
        ResponseEntity<ResponseWithFine> resultResponse = requestService.getResponse(naturalPersonRequest);

        assertThat(resultResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(resultResponse.getBody().getSts(), is("59 ут 123456"));


    }


    @Test
    void getResponseIsNullTest() throws Exception {

        MockResponse mockResponse = new MockResponse().
                setResponseCode(404);

        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);

        ResponseWithFineNullException thrown = assertThrows(ResponseWithFineNullException.class, () -> {
            requestService.getResponse(naturalPersonRequest);
        });

        String errorMessage = "No information found for  59 ут 123456";
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
        requestService.deleteResponse(id);


    }

}
