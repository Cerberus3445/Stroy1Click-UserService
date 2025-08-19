package ru.stroy1click.userservice.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmEmailRequest {

    @Email(message = "Электронная почта должна быть валидной")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;
}
