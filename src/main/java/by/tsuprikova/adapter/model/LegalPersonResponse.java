package by.tsuprikova.adapter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LegalPersonResponse extends Response {

    @Schema(description = "taxpayer identification number(ИНН - идентификационный номер налогоплательщика)")
    private Long inn;
}
