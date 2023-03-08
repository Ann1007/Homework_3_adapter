package by.tsuprikova.adapter.integrationTest;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

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


    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void createMock() {

        mockServer = MockRestServiceServer.createServer(restTemplate);

    }

    @AfterEach
    public void verifyMock() {
        mockServer.verify();
    }


    @Test
    void getNaturalPersonResponseTest() throws Exception {

        NaturalPersonRequest request = new NaturalPersonRequest();
        String sts = "59 ут 123456";
        request.setSts(sts);

        NaturalPersonResponse response = new NaturalPersonResponse();
        UUID id = UUID.randomUUID();
        response.setSts(sts);
        response.setAmountOfAccrual(new BigDecimal(28));
        response.setArticleOfKoap("21.3");
        response.setAmountOfPaid(new BigDecimal(28));
        response.setNumberOfResolution(321521);
        response.setId(id);

        String urlSaveRequest = "http://localhost:9000/api/v1/smv/natural_person/request";
        String urlGetResponse = "http://localhost:9000/api/v1/smv/natural_person/response";
        String urlDeleteResponse = "http://localhost:9000/api/v1/smv/natural_person/response/" + id;

        mockServer.expect(once(), requestTo(urlSaveRequest)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(request)));

        mockServer.expect(once(), requestTo(urlGetResponse)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(response)));

        mockServer.expect(once(), requestTo(urlDeleteResponse)).
                andExpect(method(HttpMethod.DELETE)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/natural_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(request))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value())).
                andReturn();

        String resultContext = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        NaturalPersonResponse resultResponse = objectMapper.readValue(resultContext, NaturalPersonResponse.class);

        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals(sts, resultResponse.getSts());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfAccrual());
        Assertions.assertEquals(321521, resultResponse.getNumberOfResolution());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfPaid());
        Assertions.assertEquals("21.3", resultResponse.getArticleOfKoap());


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
        legalPersonResponse.setId(id);
        legalPersonResponse.setAmountOfAccrual(amountOfAccrual);
        legalPersonResponse.setArticleOfKoap(articleOfKoap);
        legalPersonResponse.setAmountOfPaid(amountOfPaid);
        legalPersonResponse.setNumberOfResolution(numberOfResolution);

        String urlSaveRequest = "http://localhost:9000/api/v1/smv/legal_person/request";
        String urlGetResponse = "http://localhost:9000/api/v1/smv/legal_person/response";
        String urlDeleteResponse = "http://localhost:9000/api/v1/smv/legal_person/response/" + id;

        mockServer.expect(once(), requestTo(urlSaveRequest)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.ACCEPTED).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(request)));

        mockServer.expect(once(), requestTo(urlGetResponse)).
                andExpect(method(HttpMethod.POST)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK).
                        contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(legalPersonResponse)));

        mockServer.expect(once(), requestTo(urlDeleteResponse)).
                andExpect(method(HttpMethod.DELETE)).
                andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(request))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value())).
                andReturn();

        String resultContext = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        LegalPersonResponse resultResponse = objectMapper.readValue(resultContext, LegalPersonResponse.class);

        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals(inn, resultResponse.getInn());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfAccrual());
        Assertions.assertEquals(321521, resultResponse.getNumberOfResolution());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfPaid());
        Assertions.assertEquals("21.3", resultResponse.getArticleOfKoap());


    }


}
