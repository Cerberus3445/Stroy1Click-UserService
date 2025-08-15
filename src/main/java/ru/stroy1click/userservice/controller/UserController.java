package ru.stroy1click.userservice.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.stroy1click.userservice.dto.UserDto;
import ru.stroy1click.userservice.exception.ValidationException;
import ru.stroy1click.userservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Interaction with user")
@RateLimiter(name = "userLimiter")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "Get user")
    public UserDto get(@PathVariable("id") Long id){
        return this.userService.get(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<String> update(@PathVariable("id") Long id, @RequestBody @Valid UserDto userDto, BindingResult bindingResult){
        if(bindingResult.hasFieldErrors()) throw new ValidationException(collectErrorsToString(bindingResult.getFieldErrors()));

        this.userService.update(id, userDto);
        return ResponseEntity.status(HttpStatus.OK).body("User has been updated.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        this.userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("User has been deleted.");
    }

    private String collectErrorsToString(List<FieldError> fieldErrors){
        return fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList().toString().replace("[", "").replace("]", "");
    }
}
