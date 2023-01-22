package by.tsuprikova.adapter.model;


import lombok.Data;
import lombok.RequiredArgsConstructor;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
public class LegalPersonRequest {
    @NotBlank(message = "поле стс не может быть пустое")
    private String sts;
    @Min(value = 1_000_000_000L, message = "поле ИНН должно состоять минимум из 10 цифр")
    private Long INN;

}
