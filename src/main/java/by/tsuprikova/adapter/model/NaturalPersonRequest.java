package by.tsuprikova.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class NaturalPersonRequest {

    @NotBlank(message = "поле не может быть пустое")
    private String sts;

}
