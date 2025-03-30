package com.alianza.clients.repository.specification;

import com.alianza.clients.model.Client;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ClientSpecification {

    public static Specification<Client> hasName(String name) {
        return (root, query, criteriaBuilder) ->
            name == null ? null : criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + name.toLowerCase() + "%"
            );
    }

    public static Specification<Client> hasPhone(String phone) {
        return (root, query, criteriaBuilder) ->
            phone == null ? null : criteriaBuilder.like(
                root.get("phone"),
                "%" + phone + "%"
            );
    }

    public static Specification<Client> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
            email == null ? null : criteriaBuilder.like(
                criteriaBuilder.lower(root.get("email")),
                "%" + email.toLowerCase() + "%"
            );
    }

    public static Specification<Client> createdBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate == null) return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
            if (endDate == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
        };
    }
}