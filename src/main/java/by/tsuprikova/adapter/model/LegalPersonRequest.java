package by.tsuprikova.adapter.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;

@Data
@RequiredArgsConstructor
public class LegalPersonRequest {

    @Schema(description = "taxpayer identification number(ИНН - идентификационный номер налогоплательщика)")
    @Min(value = 1_000_000_000L, message = "the inn field must consist of at least 10 digits")
    private Long inn;

}
