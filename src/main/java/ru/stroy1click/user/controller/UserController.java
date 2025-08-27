package ru.stroy1click.user.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.user.exception.ValidationException;
import ru.stroy1click.user.model.ConfirmEmailRequest;
import ru.stroy1click.user.model.UserServiceUpdatePasswordRequest;
import ru.stroy1click.user.service.UserService;
import ru.stroy1click.user.util.ValidationErrorUtils;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Interaction with user")
@RateLimiter(name = "userLimiter")
public class UserController {

    private final UserService userService;

    private final MessageSource messageSource;

    @GetMapping("/{id}")
    @Operation(summary = "Получение пользователя.")
    public UserDto get(@PathVariable("id") Long id){
        return this.userService.get(id);
    }

    @GetMapping
    @Operation(summary = "Получение пользователя по электронной почте.")
    public UserDto get(@RequestParam("email") String email){
        return this.userService.getByEmail(email);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление пользователя.")
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody @Valid UserDto userDto, BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()) throw new ValidationException(ValidationErrorUtils.collectErrorsToString(
                bindingResult.getFieldErrors()
        ));

        this.userService.update(id, userDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                this.messageSource.getMessage(
                        "info.user.update",
                        null,
                        Locale.getDefault()
                )
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление пользователя.")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        this.userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                this.messageSource.getMessage(
                        "info.user.delete",
                        null,
                        Locale.getDefault()
                )
        );
    }

    @PatchMapping("/email-status")
    @Operation(summary = "Подтвердить email.")
    public ResponseEntity<String> updateEmailConfirmedStatus(@RequestBody @Valid ConfirmEmailRequest confirmEmailRequest,
                                                             BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()) throw new ValidationException(ValidationErrorUtils.collectErrorsToString(
                bindingResult.getFieldErrors()
        ));

        this.userService.updateEmailConfirmedStatus(confirmEmailRequest.getEmail());
        return ResponseEntity.ok(
                this.messageSource.getMessage(
                        "info.user.email_confirmed",
                        null,
                        Locale.getDefault()
                )
        );
    }

    @PatchMapping("/password")
    @Operation(summary = "Обновить пароль.")
    public ResponseEntity<String> updatePassword(@RequestBody @Valid UserServiceUpdatePasswordRequest updatePasswordRequest,
                                                 BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()) throw new ValidationException(ValidationErrorUtils.collectErrorsToString(
                bindingResult.getFieldErrors()
        ));

        this.userService.updatePassword(updatePasswordRequest.getEmail(),
                updatePasswordRequest.getNewPassword());
        return ResponseEntity.ok(
                this.messageSource.getMessage(
                        "info.user.password_updated",
                        null,
                        Locale.getDefault()
                )
        );
    }
}
