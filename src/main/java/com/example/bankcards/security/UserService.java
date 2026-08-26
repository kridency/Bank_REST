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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Requests user account database for new record creation.
     * Main user account database record creation.
     * @param request   user account database record description object
     *
     * @return  user account database record description object
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public UserDto create(UserDto request) {
        String email = request.getEmail();
        try {
            find(email);
            throw new EntityExistsException("Email = " + email + " already exists!");
        } catch (EntityNotFoundException e) {
            User newUser = new User(null, request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    Optional.ofNullable(request.getRoles()).orElse(Set.of(RoleType.ROLE_USER)),
                    new HashSet<>());
            return userMapper.userToUserDto(userRepository.save(newUser));
        }
    }

    /**
     * Requests user account database to update existing record.
     * Main user account database record update method.
     * @param request   new user account database record description object
     *
     * @return  user account database record description object
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public UserDto update(UserDto request) {
        String email = request.getEmail();
        User user = find(email), updateUser = new User(user.getId(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Optional.ofNullable(request.getRoles()).orElse(user.getRoles()),
                null);
        return userMapper.userToUserDto(userRepository.save(updateUser));
    }

    /**
     * Requests user account database to delete existing record.
     * Main user account database record delete method.
     * @param request  user account database record description object
     *
     * @return  number of deleted rows in the user account database
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public int delete(UserDto request) {
        var email = request.getEmail();
        Optional.ofNullable(email).orElseThrow(() -> new BadRequestException("User email not specified."));
        return userRepository.deleteByEmail(email);
    }

    /**
     * Converts user account database record description object to spring security object.
     * Overloaded method for receiving spring security object.
     * @param username  email address of the user requested for authentication
     *
     * @return  spring security user account description object
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found. Email is: " + username));
    }

    /**
     * Requests user account database for the record matching specified email address.
     * Supplementary user account database record receiving method.
     * @param email  sought user account email address
     *
     * @return  user account database record
     */
    public User find(String email) {
        return userRepository.getByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User = " + email + " not found."));
    }
}
