package com.example.bankcards.service;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.RoleType;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.specification.GetSpecification;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, CRUDService<UserDto> {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Requests user database for a filtered list.
     * Main user database constrained sample list method.
     * @param criteria   set of sought values for filter attributes
     * @param pageable  user list pagination criteria object
     *
     * @return  set of user database record representation objects
     */
    public Slice<UserDto> get(Map<String, ? extends Comparable<?>> criteria, Pageable pageable) {
        List<UserDto> result = repository.findAll(new GetSpecification<>(criteria),
                pageable).stream().map(mapper::userToUserDto).toList();
        return new SliceImpl<>(result, pageable, result.iterator().hasNext());
    }

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
            loadUserByUsername(email);
            throw new EntityExistsException("Email = " + email + " already exists!");
        } catch (EntityNotFoundException e) {
            User newUser = new User(null, request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    Optional.ofNullable(request.getRoles()).orElse(Set.of(RoleType.ROLE_USER)),
                    new HashSet<>());
            return mapper.userToUserDto(repository.save(newUser));
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
        User user = loadUserByUsername(email), updateUser = new User(user.getId(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                Optional.ofNullable(request.getRoles()).orElse(user.getRoles()),
                user.getCards());
        return mapper.userToUserDto(repository.save(updateUser));
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
        return repository.deleteByEmail(email);
    }

    /**
     * Converts user account database record description object to spring security object.
     * Overloaded method for receiving spring security object.
     * @param username  email address of the user requested for authentication
     *
     * @return  spring security user account description object
     */
    @Override
    public User loadUserByUsername(String username) throws EntityNotFoundException {
        return repository.getByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found. Email is: " + username));
    }
}
