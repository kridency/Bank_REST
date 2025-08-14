package com.example.bankcards.util.specification;

import com.example.bankcards.entity.Card;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public record CardSpecification(Map<String, ? extends Comparable<?>> criteria) implements Specification<Card> {
    private static final BiFunction<String, Path<?>, Path<?>> getPath = new BiFunction<>() {
        @Override
        public Path<?> apply(String s, Path<?> path) {
            var ind = s.indexOf('.');
            return ind != -1
                    ? getPath.apply(s.substring(ind + 1), path.get(s.substring(0, ind)))
                    : path.get(s);
        }
    };

    @Override
    public Predicate toPredicate(@Nullable Root<Card> root,
                                 @Nullable CriteriaQuery<?> query,
                                 CriteriaBuilder builder) {
        return criteria.entrySet().stream()
                .filter(entry -> Optional.ofNullable(entry.getValue()).isPresent())
                .map(entry ->
                        builder.equal(Optional.ofNullable(root)
                                        .map(x -> getPath.apply(entry.getKey(), x)).orElse(null),
                                entry.getValue()))
                .reduce(builder::and).orElse(null);
    }
}
