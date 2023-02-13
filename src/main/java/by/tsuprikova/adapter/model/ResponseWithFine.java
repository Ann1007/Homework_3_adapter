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
    @Schema(description = "сумма по начислению")
    private BigDecimal amountOfAccrual;
    @Schema(description = "сумма к оплате")
    private BigDecimal amountOfPaid;
    @Schema(description = "номер постановления")
    private int numberOfResolution;
    @Schema(description = "СТС(свидетельство транспортного средства)", example = "98 ут 253901")
    private String sts;
    @Schema(description = "дата постановления")
    private Date dateOfResolution;
    @Schema(description = "статья КоАП")
    private String articleOfKoap;


}
