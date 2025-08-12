package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring",
        uses = { RoleMapper.class })
@Named("UserMapper")
public interface UserMapper {
    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "roles", target = "roles")
    })
    UserDto userToUserDto(User user);
}
