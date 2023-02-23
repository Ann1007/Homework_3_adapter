package by.tsuprikova.adapter.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;

@Data
@RequiredArgsConstructor
public class LegalPersonRequest {

    @Schema(description = "идентификационный номер налогоплательщика")
    @Min(value = 1_000_000_000L, message = "поле ИНН должно состоять минимум из 10 цифр")
    private Long inn;

}
