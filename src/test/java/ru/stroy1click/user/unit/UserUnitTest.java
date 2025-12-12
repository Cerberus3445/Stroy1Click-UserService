package ru.stroy1click.user.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.stroy1click.user.cache.CacheClear;
import ru.stroy1click.user.dto.UserDto;
import ru.stroy1click.user.exception.NotFoundException;
import ru.stroy1click.user.mapper.UserMapper;
import ru.stroy1click.user.model.Role;
import ru.stroy1click.user.entity.User;
import ru.stroy1click.user.repository.UserRepository;
import ru.stroy1click.user.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CacheClear cacheClear;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        this.testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .emailConfirmed(true)
                .role(Role.ROLE_USER)
                .build();

        this.testUserDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .emailConfirmed(true)
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    public void get_WithValidId_ReturnsUserDto() {
        when(this.userRepository.findById(1L)).thenReturn(Optional.of(this.testUser));
        when(this.userMapper.toDto(this.testUser)).thenReturn(this.testUserDto);

        UserDto result = this.userService.get(1L);

        assertNotNull(result);
        assertEquals(this.testUserDto, result);
        verify(this.userRepository).findById(1L);
        verify(this.userMapper).toDto(this.testUser);
    }

    @Test
    public void get_WithInvalidId_ThrowsNotFoundException() {
        when(this.userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.get(999L));
        verify(this.userRepository).findById(999L);
    }

    @Test
    public void update_WithValidIdAndUserDto_UpdatesUser() {
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("john.doe@example.com")
                .password("newPassword")
                .emailConfirmed(false)
                .role(Role.ROLE_USER)
                .build();

        when(this.userRepository.findById(1L)).thenReturn(Optional.of(this.testUser));

        this.userService.update(1L, updatedUserDto);

        verify(this.userRepository).findById(1L);
        verify(this.userRepository).save(any(User.class));
        verify(this.cacheClear).clearEmail("john.doe@example.com");
    }

    @Test
    public void update_WithInvalidId_ThrowsNotFoundException() {
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("john.doe@example.com")
                .password("newPassword")
                .emailConfirmed(false)
                .role(Role.ROLE_USER)
                .build();

        when(this.userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.update(999L, updatedUserDto));
        verify(this.userRepository).findById(999L);
        verify(this.userRepository, never()).save(any(User.class));
    }

    @Test
    public void delete_WithValidId_DeletesUser() {
        when(this.userRepository.findById(1L)).thenReturn(Optional.of(this.testUser));

        this.userService.delete(1L);

        verify(this.userRepository).findById(1L);
        verify(this.userRepository).deleteById(1L);
        verify(this.cacheClear).clearEmail("john.doe@example.com");
    }

    @Test
    public void delete_WithInvalidId_ThrowsNotFoundException() {
        when(this.userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.delete(999L));
        verify(this.userRepository).findById(999L);
        verify(this.userRepository, never()).deleteById(anyLong());
    }

    @Test
    public void getByEmail_WithValidEmail_ReturnsUserDto() {
        when(this.userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(this.testUser));
        when(this.userMapper.toDto(this.testUser)).thenReturn(this.testUserDto);

        UserDto result = this.userService.getByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals(this.testUserDto, result);
        verify(this.userRepository).findByEmail("john.doe@example.com");
        verify(this.userMapper).toDto(this.testUser);
    }

    @Test
    public void getByEmail_WithInvalidEmail_ThrowsNotFoundException() {
        when(this.userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.getByEmail("invalid@example.com"));
        verify(this.userRepository).findByEmail("invalid@example.com");
    }

    @Test
    public void updateEmailConfirmedStatus_WithValidEmail_UpdatesStatus() {
        when(this.userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(this.testUser));

        this.userService.updateEmailConfirmedStatus("john.doe@example.com");

        assertTrue(this.testUser.getEmailConfirmed());
        verify(this.userRepository).findByEmail("john.doe@example.com");
        verify(this.cacheClear).clearUserById(1L);
        verify(this.cacheClear).clearEmail("john.doe@example.com");
    }

    @Test
    public void updateEmailConfirmedStatus_WithInvalidEmail_ThrowsNotFoundException() {
        when(this.userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.updateEmailConfirmedStatus("invalid@example.com"));
        verify(this.userRepository).findByEmail("invalid@example.com");
    }

    @Test
    public void updatePassword_WithValidEmail_UpdatesPassword() {
        when(this.userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(this.testUser));
        when(this.passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        this.userService.updatePassword("john.doe@example.com", "newPassword");

        verify(this.userRepository).findByEmail("john.doe@example.com");
        verify(this.passwordEncoder).encode("newPassword");
        verify(this.cacheClear).clearUserById(1L);
        verify(this.cacheClear).clearEmail("john.doe@example.com");
    }

    @Test
    public void updatePassword_WithInvalidEmail_ThrowsNotFoundException() {
        when(this.userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> this.userService.updatePassword("invalid@example.com", "newPassword"));
        verify(this.userRepository).findByEmail("invalid@example.com");
    }
}