package by.tsuprikova.adapter.controller;

import by.tsuprikova.adapter.model.LegalPersonRequest;
import by.tsuprikova.adapter.model.LegalPersonResponse;
import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.service.LegalPersonRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

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


    @Test
    void GetResponseWithValidRequestTest() throws Exception {

        LegalPersonRequest request = new LegalPersonRequest();
        Long inn = 1234567890L;
        request.setInn(inn);

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
        Assertions.assertEquals(inn, resultResponse.getInn());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfAccrual());
        Assertions.assertEquals(321521, resultResponse.getNumberOfResolution());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfPaid());
        Assertions.assertEquals("21.3", resultResponse.getArticleOfKoap());


    }


    @Test
    void GetResponseWithInvalidRequestTest() throws Exception {

        LegalPersonRequest invalidRequest = new LegalPersonRequest();
        Long inn = 890L;
        invalidRequest.setInn(inn);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/adapter/legal_person/response").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(invalidRequest))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value())).
                andExpect(MockMvcResultMatchers.jsonPath("$.inn").value("the inn field must consist of at least 10 digits")).
                andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(MethodArgumentNotValidException.class));

    }
}
