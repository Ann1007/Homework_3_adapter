package by.tsuprikova.adapter.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class LegalPersonRequest {

    @Schema(description = "СТС(свидетельство транспортного средства)", example = "98 са 253901")
    @NotBlank(message = "поле стс не может быть пустое")
    private String sts;
    @Schema(description = "идентификационный номер налогоплательщика")
    @Min(value = 1_000_000_000L, message = "поле ИНН должно состоять минимум из 10 цифр")
    private Long INN;

}
