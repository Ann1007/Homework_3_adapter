package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.exceptions.ResponseWithFineNullException;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.service.impl.NaturalPersonRequestServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NaturalPersonServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockWebServer mockWebServer;

    private NaturalPersonRequestService requestService;

    private NaturalPersonRequest request;


    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setup() {

        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        requestService = new NaturalPersonRequestServiceImpl(webClient);
        request = new NaturalPersonRequest();
        String sts = "59 ут 123456";
        request.setSts(sts);

    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void transferValidJsonPersonRequestTest() throws Exception {

        MockResponse response = new MockResponse().setBody(objectMapper.writeValueAsString(request)).
                setResponseCode(202).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(response);
        ResponseEntity<NaturalPersonRequest> result = requestService.transferClientRequest(request);

        assertThat(result.getBody().getSts(), is("59 ут 123456"));
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));


    }


    @Test
    void transferValidXMLRequestTest() throws Exception {

        JAXBContext context = JAXBContext.newInstance(NaturalPersonRequest.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter sw = new StringWriter();
        mar.marshal(request, sw);
        String xmlRequest = sw.toString();

        MockResponse response = new MockResponse().setBody(xmlRequest).
                setResponseCode(202).
                addHeader("Content-Type", "application/xml");

        mockWebServer.enqueue(response);
        ResponseEntity<NaturalPersonRequest> result = requestService.transferClientRequest(request);

        assertThat(result.getBody().getSts(), is("59 ут 123456"));
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));

    }


    @Test
    void getNotNullResponseWithValidRequestTest() throws Exception {

        NaturalPersonResponse response = new NaturalPersonResponse();
        BigDecimal amountOfAccrual = new BigDecimal(28);
        BigDecimal amountOfPaid = new BigDecimal(28);
        int numberOfResolution = 321521;
        String articleOfKoap = "21.3";
        response.setSts("59 ут 123456");
        response.setAmountOfAccrual(amountOfAccrual);
        response.setArticleOfKoap(articleOfKoap);
        response.setAmountOfPaid(amountOfPaid);
        response.setNumberOfResolution(numberOfResolution);

        MockResponse mockResponse = new MockResponse().setBody(objectMapper.writeValueAsString(response)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(mockResponse);
        ResponseEntity<NaturalPersonResponse> resultResponse = requestService.getResponse(request);

        assertThat(resultResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(resultResponse.getBody().getSts(), is("59 ут 123456"));
        assertThat(resultResponse.getBody().getAmountOfAccrual(), is(amountOfAccrual));
        assertThat(resultResponse.getBody().getAmountOfPaid(), is(amountOfPaid));
        assertThat(resultResponse.getBody().getArticleOfKoap(), is(articleOfKoap));
        assertThat(resultResponse.getBody().getNumberOfResolution(), is(numberOfResolution));

    }


    @Test
    void getNullResponseTest() {

        MockResponse mockResponse = new MockResponse().
                setResponseCode(404);

        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);

        ResponseWithFineNullException thrown = assertThrows(ResponseWithFineNullException.class,
                () -> requestService.getResponse(request));

        String errorMessage = "No information found for '59 ут 123456'";
        Assertions.assertEquals(errorMessage, thrown.getMessage());
    }


    @Test
    void deleteResponseById() throws Exception {

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
