package com.example.bankcards.mapper;

import com.example.bankcards.entity.RoleType;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring"
)
@Named("RoleMapper")
public interface RoleMapper {
    @Named("toRoles")
    default Set<RoleType> getRoles(Set<RoleType> roles) {
        return Optional.ofNullable(roles).orElse(Collections.emptySet());
    }
}
