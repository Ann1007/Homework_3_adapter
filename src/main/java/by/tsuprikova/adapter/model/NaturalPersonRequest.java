package by.tsuprikova.adapter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@RequiredArgsConstructor
public class NaturalPersonRequest {

    @Schema(description = "СТС(свидетельство транспортного средства)", example = "98 ут 253901")
    @NotBlank(message = "поле стс не может быть пустое")
    private String sts;


}
