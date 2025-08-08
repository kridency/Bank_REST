package com.example.bankcards.mapper;

import com.example.bankcards.dto.RegistrationDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring",
        uses = { RoleMapper.class })
@Named("UserMapper")
public interface UserMapper {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "roles", target = "roles")
    })
    UserDto userToUserDto(User user);

    @Named("encodePassword")
    default String encodePassword(String plain) {
        return encoder.encode(plain);
    }
}
