package ru.stroy1click.userservice.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.stroy1click.userservice.dto.UserDto;
import ru.stroy1click.userservice.model.User;

@Component
@RequiredArgsConstructor
public class UserMapper implements Mappable<User, UserDto>{

    private final ModelMapper modelMapper;

    @Override
    public User toEntity(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }

    @Override
    public UserDto toDto(User userCredential) {
        return this.modelMapper.map(userCredential, UserDto.class);
    }
}
