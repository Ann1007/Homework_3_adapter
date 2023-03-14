package by.tsuprikova.adapter.integrationTest;

import by.tsuprikova.adapter.exceptions.ResponseNullException;
import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private final String URL_SAVE_NATURAL_PERSON_REQUEST = "http://localhost:9000/api/v1/smv/natural_person/request";
    private final String URL_GET_NATURAL_PERSON_RESPONSE = "http://localhost:9000/api/v1/smv/natural_person/response";
    private final String URL_DELETE_NATURAL_PERSON_RESPONSE = "http://localhost:9000/api/v1/smv/natural_person/response/";

    private final String URL_SAVE_LEGAL_PERSON_REQUEST = "http://localhost:9000/api/v1/smv/legal_person/request";
    private final String URL_GET_LEGAL_PERSON_RESPONSE = "http://localhost:9000/api/v1/smv/legal_person/response";
    private final String URL_DELETE_LEGAL_PERSON_RESPONSE = "http://localhost:9000/api/v1/smv/legal_person/response/";

    private NaturalPersonRequest naturalPersonRequest;
    private NaturalPersonResponse naturalPersonResponse;
    private LegalPersonRequest legalPersonRequest;
    private LegalPersonResponse legalPersonResponse;


    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void createMock() {

        mockServer = MockRestServiceServer.createServer(restTemplate);

        naturalPersonRequest = new NaturalPersonRequest();
        naturalPersonRequest.setSts("59 ут 123456");

        naturalPersonResponse = new NaturalPersonResponse();
        naturalPersonResponse.setSts("59 ут 123456");
        naturalPersonResponse.setAmountOfAccrual(new BigDecimal(28));
        naturalPersonResponse.setArticleOfKoap("21.3");
        naturalPersonResponse.setAmountOfPaid(new BigDecimal(28));
        naturalPersonResponse.setNumberOfResolution(34441521);

        legalPersonRequest = new LegalPersonRequest();
        Long inn = 1234567890L;
        legalPersonRequest.setInn(inn);

        legalPersonResponse = new LegalPersonResponse();
        legalPersonResponse.setInn(inn);
        legalPersonResponse.setAmountOfAccrual(new BigDecimal(44));
        legalPersonResponse.setArticleOfKoap("21.7");
        legalPersonResponse.setAmountOfPaid(new BigDecimal(44));
        legalPersonResponse.setNumberOfResolution(321521);

    }

    @AfterEach
    public void verifyMock() {
        mockServer.verify();
    }


    @Test
    void getNotNullJsonNaturalPersonResponseTest() throws Exception {

        UUID id = UUID.randomUUID();
        naturalPersonResponse.setId(id);
        mockServer.expect(once(), requestTo(URL_SAVE_NATURAL_PERSON_REQUEST)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(naturalPersonRequest)));

        mockServer.expect(once(), requestTo(URL_GET_NATURAL_PERSON_RESPONSE)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(naturalPersonResponse)));

        mockServer.expect(once(), requestTo(URL_DELETE_NATURAL_PERSON_RESPONSE + id)).
                andExpect(method(HttpMethod.DELETE)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/natural_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(naturalPersonRequest))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value())).
                andReturn();

        String resultContext = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        NaturalPersonResponse resultResponse = objectMapper.readValue(resultContext, NaturalPersonResponse.class);

        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals("59 ут 123456", resultResponse.getSts());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfAccrual());
        Assertions.assertEquals(34441521, resultResponse.getNumberOfResolution());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfPaid());
        Assertions.assertEquals("21.3", resultResponse.getArticleOfKoap());

    }


    @Test
    void getNotNullXmlNaturalPersonResponseTest() throws Exception {

        UUID id = UUID.randomUUID();
        naturalPersonResponse.setId(id);

        JAXBContext context = JAXBContext.newInstance(NaturalPersonRequest.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter sw = new StringWriter();
        mar.marshal(naturalPersonRequest, sw);
        String xmlRequest = sw.toString();

        mockServer.expect(once(), requestTo(URL_SAVE_NATURAL_PERSON_REQUEST)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_XML)
                        .body(xmlRequest));

        JAXBContext jaxbContext = JAXBContext.newInstance(NaturalPersonResponse.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter swr = new StringWriter();
        marshaller.marshal(naturalPersonResponse, swr);
        String xmlResponse = swr.toString();

        mockServer.expect(once(), requestTo(URL_GET_NATURAL_PERSON_RESPONSE)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK).
                        contentType(MediaType.APPLICATION_XML)
                        .body(xmlResponse));

        mockServer.expect(once(), requestTo(URL_DELETE_NATURAL_PERSON_RESPONSE + id)).
                andExpect(method(HttpMethod.DELETE)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/natural_person/response").
                        contentType(MediaType.APPLICATION_XML).
                        accept(MediaType.APPLICATION_XML).
                        content(xmlRequest)).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value())).
                andExpect(MockMvcResultMatchers.xpath("//sts/text()").string("59 ут 123456")).
                andExpect(MockMvcResultMatchers.xpath("//amountOfAccrual/text()").string("28")).
                andExpect(MockMvcResultMatchers.xpath("//amountOfPaid/text()").string("28")).
                andExpect(MockMvcResultMatchers.xpath("//numberOfResolution/text()").string("34441521")).
                andExpect(MockMvcResultMatchers.xpath("//articleOfKoap/text()").string("21.3"));

    }


    @Test
    void getNullJsonNaturalPersonResponseTest() throws Exception {

        mockServer.expect(once(), requestTo(URL_SAVE_NATURAL_PERSON_REQUEST)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(naturalPersonRequest)));

        mockServer.expect(times(3), requestTo(URL_GET_NATURAL_PERSON_RESPONSE)).
                andExpect(method(HttpMethod.POST)).
                andRespond((response) -> {
                    throw new ResponseNullException("No information found for sts='" + naturalPersonRequest.getSts() + "'");
                });

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/natural_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(naturalPersonRequest))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value())).
                andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No information found for sts='59 ут 123456'"));


    }


    @Test
    void getNullXmlNaturalPersonResponseTest() throws Exception {

        JAXBContext context = JAXBContext.newInstance(NaturalPersonRequest.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter sw = new StringWriter();
        mar.marshal(naturalPersonRequest, sw);
        String xmlRequest = sw.toString();

        mockServer.expect(once(), requestTo(URL_SAVE_NATURAL_PERSON_REQUEST)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_XML)
                        .body(xmlRequest));

        mockServer.expect(times(3), requestTo(URL_GET_NATURAL_PERSON_RESPONSE)).
                andExpect(method(HttpMethod.POST)).
                andRespond((response) -> {
                    throw new ResponseNullException("No information found for sts='" + naturalPersonRequest.getSts() + "'");
                });

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/natural_person/response").
                        contentType(MediaType.APPLICATION_XML).
                        accept(MediaType.APPLICATION_XML).
                        content(xmlRequest)).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value())).
                andExpect(MockMvcResultMatchers.xpath("//message/text()").string("No information found for sts='59 ут 123456'"));

    }


    @Test
    void getNotNullJsonLegalPersonResponseTest() throws Exception {

        UUID id = UUID.randomUUID();
        legalPersonResponse.setId(id);

        mockServer.expect(once(), requestTo(URL_SAVE_LEGAL_PERSON_REQUEST)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(legalPersonRequest)));

        mockServer.expect(once(), requestTo(URL_GET_LEGAL_PERSON_RESPONSE)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(legalPersonResponse)));

        mockServer.expect(once(), requestTo(URL_DELETE_LEGAL_PERSON_RESPONSE + id)).
                andExpect(method(HttpMethod.DELETE)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(legalPersonRequest))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value())).
                andReturn();

        String resultContext = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        LegalPersonResponse resultResponse = objectMapper.readValue(resultContext, LegalPersonResponse.class);

        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals(1234567890L, resultResponse.getInn());
        Assertions.assertEquals(new BigDecimal(44), resultResponse.getAmountOfAccrual());
        Assertions.assertEquals(321521, resultResponse.getNumberOfResolution());
        Assertions.assertEquals(new BigDecimal(44), resultResponse.getAmountOfPaid());
        Assertions.assertEquals("21.7", resultResponse.getArticleOfKoap());

    }


    @Test
    void getNotNullXmlLegalPersonResponseTest() throws Exception {

        UUID id = UUID.randomUUID();
        legalPersonResponse.setId(id);

        JAXBContext context = JAXBContext.newInstance(LegalPersonRequest.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter sw = new StringWriter();
        mar.marshal(legalPersonRequest, sw);
        String xmlRequest = sw.toString();

        mockServer.expect(once(), requestTo(URL_SAVE_LEGAL_PERSON_REQUEST)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_XML)
                        .body(xmlRequest));


        JAXBContext jaxbContext = JAXBContext.newInstance(LegalPersonResponse.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter swr = new StringWriter();
        marshaller.marshal(legalPersonResponse, swr);
        String xmlResponse = swr.toString();

        mockServer.expect(once(), requestTo(URL_GET_LEGAL_PERSON_RESPONSE)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK).
                        contentType(MediaType.APPLICATION_XML)
                        .body(xmlResponse));

        mockServer.expect(once(), requestTo(URL_DELETE_LEGAL_PERSON_RESPONSE + id)).
                andExpect(method(HttpMethod.DELETE)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_XML).
                        accept(MediaType.APPLICATION_XML).
                        content(xmlRequest)).
                andExpect(status().is(HttpStatus.OK.value())).
                andExpect(MockMvcResultMatchers.xpath("//inn/text()").string(String.valueOf(1234567890L))).
                andExpect(MockMvcResultMatchers.xpath("//amountOfAccrual/text()").string("44")).
                andExpect(MockMvcResultMatchers.xpath("//amountOfPaid/text()").string("44")).
                andExpect(MockMvcResultMatchers.xpath("//numberOfResolution/text()").string(String.valueOf(321521))).
                andExpect(MockMvcResultMatchers.xpath("//articleOfKoap/text()").string("21.7"));

    }


    @Test
    void getNullJsonLegalPersonResponseTest() throws Exception {

        mockServer.expect(once(), requestTo(URL_SAVE_LEGAL_PERSON_REQUEST)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(legalPersonRequest)));

        mockServer.expect(times(3), requestTo(URL_GET_LEGAL_PERSON_RESPONSE)).
                andExpect(method(HttpMethod.POST)).
                andRespond((response) -> {
                    throw new ResponseNullException("No information found for inn='" + legalPersonRequest.getInn() + "'");
                });

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        accept(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(legalPersonRequest))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value())).
                andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No information found for inn='1234567890'"));

    }


    @Test
    void getNullXmlLegalPersonResponseTest() throws Exception {

        JAXBContext context = JAXBContext.newInstance(LegalPersonRequest.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter sw = new StringWriter();
        mar.marshal(legalPersonRequest, sw);
        String xmlRequest = sw.toString();

        mockServer.expect(once(), requestTo(URL_SAVE_LEGAL_PERSON_REQUEST)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_XML)
                        .body(xmlRequest));

        mockServer.expect(times(3), requestTo(URL_GET_LEGAL_PERSON_RESPONSE)).
                andExpect(method(HttpMethod.POST)).
                andRespond((response) -> {
                    throw new ResponseNullException("No information found for inn='" + legalPersonRequest.getInn() + "'");
                });

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_XML).
                        accept(MediaType.APPLICATION_XML).
                        content(xmlRequest)).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value())).
                andExpect(MockMvcResultMatchers.xpath("//message/text()").string("No information found for inn='1234567890'"));

    }


}
