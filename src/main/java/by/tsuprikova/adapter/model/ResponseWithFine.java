package by.tsuprikova.adapter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class ResponseWithFine {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    @Schema(description = "amount of accrual (сумма по начислению)")
    private BigDecimal amountOfAccrual;
    @Schema(description = "amount of paid(сумма к оплате)")
    private BigDecimal amountOfPaid;
    @Schema(description = "number of resolution(номер постановления)")
    private int numberOfResolution;
    @Schema(description = "date of resolution(дата постановления)")
    private Date dateOfResolution;
    @Schema(description = "article of Koap(статья КоАП)")
    private String articleOfKoap;


}
