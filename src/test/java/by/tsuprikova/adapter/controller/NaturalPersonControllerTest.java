package by.tsuprikova.adapter.controller;

import by.tsuprikova.adapter.model.NaturalPersonRequest;
import by.tsuprikova.adapter.model.NaturalPersonResponse;
import by.tsuprikova.adapter.service.NaturalPersonRequestService;
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
public class NaturalPersonControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private NaturalPersonRequestService requestService;


    @Test
    void GetResponseWithValidRequestTest() throws Exception {

        NaturalPersonRequest request = new NaturalPersonRequest();
        String sts = "59 ут 123456";
        request.setSts(sts);

        NaturalPersonResponse naturalPersonResponse = new NaturalPersonResponse();
        BigDecimal amountOfAccrual = new BigDecimal(28);
        BigDecimal amountOfPaid = new BigDecimal(28);
        int numberOfResolution = 321521;
        String articleOfKoap = "21.3";
        naturalPersonResponse.setSts("59 ут 123456");
        naturalPersonResponse.setAmountOfAccrual(amountOfAccrual);
        naturalPersonResponse.setArticleOfKoap(articleOfKoap);
        naturalPersonResponse.setAmountOfPaid(amountOfPaid);
        naturalPersonResponse.setNumberOfResolution(numberOfResolution);
        ResponseEntity<NaturalPersonResponse> response = new ResponseEntity<>(naturalPersonResponse, HttpStatus.OK);

        Mockito.when(requestService.getResponseWithFineFromSMV(any(NaturalPersonRequest.class))).thenReturn(response);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/adapter/natural_person/get_response").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(request))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value())).
                andReturn();

        String resultContext = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        NaturalPersonResponse resultResponse = objectMapper.readValue(resultContext, NaturalPersonResponse.class);

        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals("59 ут 123456", resultResponse.getSts());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfAccrual());
        Assertions.assertEquals(321521, resultResponse.getNumberOfResolution());
        Assertions.assertEquals(new BigDecimal(28), resultResponse.getAmountOfPaid());
        Assertions.assertEquals("21.3", resultResponse.getArticleOfKoap());


    }


    @Test
    void GetResponseWithInvalidRequestTest() throws Exception {

        NaturalPersonRequest invalidRequest = new NaturalPersonRequest();
        invalidRequest.setSts("");

        mockMvc.perform(MockMvcRequestBuilders.post("/adapter/natural_person/get_response").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(invalidRequest))).
                andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value())).
                andExpect(MockMvcResultMatchers.jsonPath("$.sts").value("поле стс не может быть пустое")).
                andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(MethodArgumentNotValidException.class));

    }


}
