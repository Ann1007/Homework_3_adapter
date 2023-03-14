package by.tsuprikova.adapter.controller;

import by.tsuprikova.adapter.exceptions.ResponseNullException;
import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LegalPersonControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LegalPersonRequestService requestService;

    private LegalPersonRequest request;

    LegalPersonResponse legalPersonResponse;

    @BeforeEach
    void init() {
        request = new LegalPersonRequest();
        Long inn = 1234567890L;
        request.setInn(inn);
        legalPersonResponse = new LegalPersonResponse();
        BigDecimal amountOfAccrual = new BigDecimal(28);
        BigDecimal amountOfPaid = new BigDecimal(28);
        int numberOfResolution = 321521;
        String articleOfKoap = "21.3";
        legalPersonResponse.setInn(inn);
        legalPersonResponse.setAmountOfAccrual(amountOfAccrual);
        legalPersonResponse.setArticleOfKoap(articleOfKoap);
        legalPersonResponse.setAmountOfPaid(amountOfPaid);
        legalPersonResponse.setNumberOfResolution(numberOfResolution);
    }


    @Test
    void checkBadRequestTest() throws Exception {

        LegalPersonRequest invalidRequest = new LegalPersonRequest();
        Long inn = 890L;
        invalidRequest.setInn(inn);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(invalidRequest))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value())).
                andExpect(MockMvcResultMatchers.jsonPath("$.inn").value("the inn field must consist of at least 10 digits"));

        JAXBContext context = JAXBContext.newInstance(LegalPersonRequest.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter sw = new StringWriter();
        marshaller.marshal(invalidRequest, sw);
        String xmlRequest = sw.toString();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_XML_VALUE).
                        accept(MediaType.APPLICATION_XML_VALUE).
                        content(xmlRequest)).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value())).
                andExpect(MockMvcResultMatchers.xpath("//inn/text()").string("the inn field must consist of at least 10 digits"));
    }


    @Test
    void getResponseWithValidJsonRequestTest() throws Exception {

        ResponseEntity<LegalPersonResponse> response = new ResponseEntity<>(legalPersonResponse, HttpStatus.OK);

        Mockito.when(requestService.getResponseWithFineFromSMV(any(LegalPersonRequest.class))).thenReturn(response);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(request))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value())).
                andReturn();

        String resultContext = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        LegalPersonResponse resultResponse = objectMapper.readValue(resultContext, LegalPersonResponse.class);

        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals(1234567890L, resultResponse.getInn());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfAccrual());
        Assertions.assertEquals(321521, resultResponse.getNumberOfResolution());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfPaid());
        Assertions.assertEquals("21.3", resultResponse.getArticleOfKoap());

    }


    @Test
    void getResponseWithValidXmlRequestTest() throws Exception {

        ResponseEntity<LegalPersonResponse> response = new ResponseEntity<>(legalPersonResponse, HttpStatus.OK);

        Mockito.when(requestService.getResponseWithFineFromSMV(any(LegalPersonRequest.class))).thenReturn(response);

        JAXBContext context = JAXBContext.newInstance(LegalPersonRequest.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter sw = new StringWriter();
        marshaller.marshal(request, sw);
        String xmlRequest = sw.toString();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_XML_VALUE).
                        accept(MediaType.APPLICATION_XML_VALUE).
                        content(xmlRequest)).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value())).
                andReturn();

        String resultContext = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        JAXBContext jaxbContext = JAXBContext.newInstance(LegalPersonResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        @Cleanup StringReader stringReader = new StringReader(resultContext);
        LegalPersonResponse resultResponse = (LegalPersonResponse) unmarshaller.unmarshal(stringReader);

        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals(1234567890L, resultResponse.getInn());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfAccrual());
        Assertions.assertEquals(321521, resultResponse.getNumberOfResolution());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfPaid());
        Assertions.assertEquals("21.3", resultResponse.getArticleOfKoap());

    }


    @Test
    void getNullResponseTest() throws Exception {

        Mockito.when(requestService.getResponseWithFineFromSMV(any(LegalPersonRequest.class))).thenThrow(ResponseNullException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(request))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()));

        JAXBContext jaxbContext = JAXBContext.newInstance(LegalPersonRequest.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        @Cleanup StringWriter sw = new StringWriter();
        marshaller.marshal(request, sw);
        String xmlRequest = sw.toString();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_XML_VALUE).
                        accept(MediaType.APPLICATION_XML_VALUE).
                        content(xmlRequest)).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()));

    }

}
