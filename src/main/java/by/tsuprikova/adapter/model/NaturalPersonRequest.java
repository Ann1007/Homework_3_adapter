package by.tsuprikova.adapter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlRootElement;


@Data
@RequiredArgsConstructor
@XmlRootElement(name = "naturalPersonRequest")
public class NaturalPersonRequest {

    @Schema(description = "vehicle certificate (СТС - свидетельство транспортного средства)", example = "98 ут 253901")
    @NotBlank(message = "the sts field cannot be empty")
    private String sts;


}
