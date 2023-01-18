package by.tsuprikova.adapter.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class ResponseWithFine {

    private int id;
    private BigDecimal amountOfAccrual;
    private BigDecimal amountOfPaid;
    private int numberOfResolution;
    private String sts;
    private Date dateOfResolution;
    private String articleOfKOAP;


}
