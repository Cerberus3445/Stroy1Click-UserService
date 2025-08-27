package ru.stroy1click.user.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.stroy1click.user.cache.CacheClear;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.user.exception.NotFoundException;
import ru.stroy1click.user.mapper.UserMapper;
import ru.stroy1click.user.model.User;
import ru.stroy1click.user.repository.UserRepository;
import ru.stroy1click.user.service.UserService;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final CacheClear cacheClear;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserDto get(Long id) {
        log.info("get {}", id);
        return this.userMapper.toDto(this.userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(id))
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "user", key = "#id")
    public void update(Long id, UserDto userDto) {
        log.info("update {}, {}", id, userDto);
        this.userRepository.findById(id).ifPresentOrElse(user -> {
            User newUser = User.builder()
                    .id(user.getId())
                    .firstName(userDto.getFirstName())
                    .lastName(userDto.getLastName())
                    .password(userDto.getPassword())
                    .emailConfirmed(user.getEmailConfirmed())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
            this.userRepository.save(newUser);
        }, () -> {
            throw new NotFoundException(id);
        });

        this.cacheClear.clearEmail(userDto.getEmail());
    }

    @Override
    @Transactional
    @CacheEvict(value = "user", key = "#id")
    public void delete(Long id) {
        log.info("delete {}", id);
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(id)
        );

        this.userRepository.deleteById(id);
        this.cacheClear.clearEmail(user.getEmail());
    }

    @Override
    @Cacheable(value = "email", key = "#email")
    public UserDto getByEmail(String email) {
        log.info("getByEmail {}", email);
        return this.userMapper.toDto(this.userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(email)
        ));
    }

    @Override
    @Transactional
    public void updateEmailConfirmedStatus(String email) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(email));
        user.setEmailConfirmed(true);

        this.cacheClear.clearUserById(user.getId());
        this.cacheClear.clearEmail(email);
    }

    @Override
    @Transactional
    public void updatePassword(String email, String newPassword) {
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(email));
        user.setPassword(this.passwordEncoder.encode(newPassword));

        this.cacheClear.clearUserById(user.getId());
        this.cacheClear.clearEmail(email);
    }
}

