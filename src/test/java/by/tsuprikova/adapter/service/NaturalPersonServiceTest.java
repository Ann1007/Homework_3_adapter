package by.tsuprikova.adapter.service;

import by.tsuprikova.adapter.exceptions.ResponseNullException;
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
    private NaturalPersonResponse response;


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

        response = new NaturalPersonResponse();

        UUID id = UUID.randomUUID();

        response.setSts("59 ут 123456");
        response.setAmountOfAccrual(new BigDecimal(28));
        response.setArticleOfKoap("21.3");
        response.setAmountOfPaid(new BigDecimal(28));
        response.setNumberOfResolution(321521);
        response.setId(id);


    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }


    @Test
    void getNullResponseTest() throws Exception {

        MockResponse response = new MockResponse().setBody(objectMapper.writeValueAsString(request)).
                setResponseCode(202).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(response);

        MockResponse mockResponse = new MockResponse().
                setResponseCode(404);

        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);

        ResponseNullException thrown = assertThrows(ResponseNullException.class,
                () -> requestService.getResponseWithFineFromSMV(request));

        String errorMessage = "No information found for '59 ут 123456'";
        Assertions.assertEquals(errorMessage, thrown.getMessage());
    }


    @Test
    void getNotNullJsonResponseWithValidJsonRequestTest() throws Exception {

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(request)).
                setResponseCode(202).
                addHeader("Content-Type", "application/json"));

        MockResponse mockResponse = new MockResponse().setBody(objectMapper.writeValueAsString(response)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json");

        mockWebServer.enqueue(mockResponse);

        ResponseEntity<Void> res = new ResponseEntity<>(HttpStatus.OK);

        mockWebServer.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(res)).
                setResponseCode(200).
                addHeader("Content-Type", "application/json"));

        ResponseEntity<NaturalPersonResponse> resultResponse = requestService.getResponseWithFineFromSMV(request);

        assertThat(resultResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(resultResponse.getBody().getSts(), is("59 ут 123456"));
        assertThat(resultResponse.getBody().getAmountOfAccrual(), is(new BigDecimal(28)));
        assertThat(resultResponse.getBody().getAmountOfPaid(), is(new BigDecimal(28)));
        assertThat(resultResponse.getBody().getArticleOfKoap(), is("21.3"));
        assertThat(resultResponse.getBody().getNumberOfResolution(), is(321521));

    }

    @Test
    void getNotNullXmlResponseWithValidXmlRequestTest() throws Exception {


        JAXBContext context = JAXBContext.newInstance(NaturalPersonRequest.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter sw = new StringWriter();
        mar.marshal(request, sw);
        String xmlRequest = sw.toString();

        mockWebServer.enqueue(new MockResponse().setBody(xmlRequest).
                setResponseCode(202).
                addHeader("Content-Type", "application/xml"));


        JAXBContext jaxbContext = JAXBContext.newInstance(NaturalPersonResponse.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter stringWriter = new StringWriter();
        marshaller.marshal(response, stringWriter);
        String xmlResponse = stringWriter.toString();

        mockWebServer.enqueue(new MockResponse().setBody(xmlResponse).
                setResponseCode(200).
                addHeader("Content-Type", "application/xml"));

        mockWebServer.enqueue(new MockResponse().
                setResponseCode(200).
                addHeader("Content-Type", "application/xml"));

        ResponseEntity<NaturalPersonResponse> resultResponse = requestService.getResponseWithFineFromSMV(request);

        assertThat(resultResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(resultResponse.getBody().getSts(), is("59 ут 123456"));
        assertThat(resultResponse.getBody().getAmountOfAccrual(), is(new BigDecimal(28)));
        assertThat(resultResponse.getBody().getAmountOfPaid(), is(new BigDecimal(28)));
        assertThat(resultResponse.getBody().getArticleOfKoap(), is("21.3"));
        assertThat(resultResponse.getBody().getNumberOfResolution(), is(321521));

    }


}
