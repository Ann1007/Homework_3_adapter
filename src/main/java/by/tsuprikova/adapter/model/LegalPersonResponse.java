package by.tsuprikova.adapter.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "LegalPersonResponse")
public class LegalPersonResponse extends Response {

    @Schema(description = "taxpayer identification number(ИНН - идентификационный номер налогоплательщика)")
    private Long inn;
}
