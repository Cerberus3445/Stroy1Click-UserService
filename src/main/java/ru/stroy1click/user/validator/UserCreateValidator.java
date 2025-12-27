package ru.stroy1click.user.validator;

import ru.stroy1click.user.dto.UserDto;

public interface UserCreateValidator {

    void validate(String email);
}
