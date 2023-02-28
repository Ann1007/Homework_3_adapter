package by.tsuprikova.adapter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NaturalPersonResponse extends Response {
    @Schema(description = "vehicle certificate (СТС - свидетельство транспортного средства)", example = "98 ут 253901")
    private String sts;
}
