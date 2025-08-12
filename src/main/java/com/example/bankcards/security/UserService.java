package com.example.bankcards.security;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.RoleType;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Запускает обращение к базе данных пользователей для создания новой записи.
     * Основной метод для создания записи пользователя в базе данных.
     * @param request   объект описания аттрибутов создаваемой учетной записи пользователя
     *
     * @return  объект описания результата обращения к базе данных пользователей
     */
    public UserDto create(UserDto request) {
        String email = request.getEmail();
        try {
            find(email);
            throw new EntityExistsException("Email = " + email + " уже зарегистрирован!");
        } catch (EntityNotFoundException e) {
            User newUser = new User(null, request.getEmail(),
                    passwordEncoder.encode(request.getPassword()), request.getRoles(), new HashSet<>());
            Optional.ofNullable(newUser.getRoles()).filter(Set::isEmpty).map(value -> value.add(RoleType.ROLE_USER));
            return userMapper.userToUserDto(userRepository.save(newUser));
        }
    }

    /**
     * Запускает обращение к базе данных пользователей для обновления существующей записи.
     * Основной метод для обновления записи пользователя в базе данных.
     * @param request   объект описания аттрибутов создаваемой учетной записи пользователя
     *
     * @return  объект описания результата обращения к базе данных пользователей
     */
    public UserDto update(UserDto request) {
        String email = request.getEmail();
        User user = find(email), updateUser = new User(user.getId(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Optional.ofNullable(request.getRoles()).orElse(user.getRoles()),
                null);
        return userMapper.userToUserDto(userRepository.save(updateUser));
    }

    /**
     * Запускает обращение к базе данных пользователей для удаления существующей записи.
     * Основной метод для удаления записи пользователя в базе данных.
     * @param request  объект описания аттрибутов удаляемой учетной записи пользователя
     *
     * @return  объект описания результата обращения к базе данных пользователей
     */
    public int delete(UserDto request) {
        var email = request.getEmail();
        Optional.ofNullable(email).orElseThrow(() -> new BadRequestException("Не указан email пользователя."));
        return userRepository.deleteByEmail(email);
    }

    /**
     * Преобразует объект отображения записи базы данных в объект используемый spring security.
     * Перегрузка метода для получения объекта spring security.
     * @param username  адрес электронной почты пользователя, отправившего запрос на аутентификацию
     *
     * @return  объект описания пользователя используемый spring security
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Email is: " + username));
    }

    /**
     * Запускает обращение к базе данных пользователей для получения объекта записи согласно указанному адресу электронной почты.
     * Вспомогательный метод для получения записи пользователя из базы данных.
     * @param email  адрес электронной почты искомого пользователя
     *
     * @return  объект отображения записи базы данных пользователей
     */
    public User find(String email) {
        return userRepository.getByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с Email = " + email + " не найден."));
    }
}
