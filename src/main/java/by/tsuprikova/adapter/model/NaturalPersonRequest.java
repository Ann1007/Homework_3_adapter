package by.tsuprikova.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@RequiredArgsConstructor
public class NaturalPersonRequest {

    @NotBlank(message = "поле стс не может быть пустое")
    private String sts;


}
