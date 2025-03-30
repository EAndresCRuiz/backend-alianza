package com.alianza.clients.repository.specification;

import com.alianza.clients.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientSpecificationTest {

    @Test
    void hasName_WithValidName_ShouldReturnSpecification() {
        // Arrange
        String name = "John";
        Root<Client> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        jakarta.persistence.criteria.Path<String> namePath = mock(jakarta.persistence.criteria.Path.class);
        when(root.<String>get("name")).thenReturn(namePath);
        when(criteriaBuilder.lower(namePath)).thenReturn(namePath);
        when(criteriaBuilder.like(namePath, "%john%")).thenReturn(mock(jakarta.persistence.criteria.Predicate.class));

        // Act
        Specification<Client> spec = ClientSpecification.hasName(name);
        spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).like(namePath, "%john%");
    }

    @Test
    void hasName_WithNullName_ShouldReturnNull() {
        // Arrange
        Root<Client> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        // Act
        Specification<Client> spec = ClientSpecification.hasName(null);
        var result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNull(result);
        verify(criteriaBuilder, never()).like(any(jakarta.persistence.criteria.Expression.class), anyString());
    }

    @Test
    void hasPhone_WithValidPhone_ShouldReturnSpecification() {
        // Arrange
        String phone = "123456789";
        Root<Client> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        jakarta.persistence.criteria.Path<String> phonePath = mock(jakarta.persistence.criteria.Path.class);
        when(root.<String>get("phone")).thenReturn(phonePath);
        when(criteriaBuilder.lower(phonePath)).thenReturn(phonePath);
        when(criteriaBuilder.like(phonePath, "%123456789%")).thenReturn(mock(jakarta.persistence.criteria.Predicate.class));

        // Act
        Specification<Client> spec = ClientSpecification.hasPhone(phone);
        spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).like(phonePath, "%123456789%");
    }

    @Test
    void hasEmail_WithValidEmail_ShouldReturnSpecification() {
        // Arrange
        String email = "test@test.com";
        Root<Client> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        jakarta.persistence.criteria.Path<String> emailPath = mock(jakarta.persistence.criteria.Path.class);
        when(root.<String>get("email")).thenReturn(emailPath);
        when(criteriaBuilder.lower(emailPath)).thenReturn(emailPath);
        when(criteriaBuilder.like(emailPath, "%test@test.com%")).thenReturn(mock(jakarta.persistence.criteria.Predicate.class));

        // Act
        Specification<Client> spec = ClientSpecification.hasEmail(email);
        spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).like(emailPath, "%test@test.com%");
    }

    @Test
    void createdBetween_WithBothDates_ShouldReturnBetweenPredicate() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Root<Client> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        jakarta.persistence.criteria.Path<LocalDate> createdAtPath = mock(jakarta.persistence.criteria.Path.class);
        when(root.<LocalDate>get("createdAt")).thenReturn(createdAtPath);
        when(criteriaBuilder.between(createdAtPath, startDate, endDate)).thenReturn(mock(jakarta.persistence.criteria.Predicate.class));

        // Act
        Specification<Client> spec = ClientSpecification.createdBetween(startDate, endDate);
        spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).between(createdAtPath, startDate, endDate);
    }

    @Test
    void createdBetween_WithStartDateOnly_ShouldReturnGreaterThanOrEqualPredicate() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        Root<Client> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        jakarta.persistence.criteria.Path<LocalDate> createdAtPath = mock(jakarta.persistence.criteria.Path.class);
        when(root.<LocalDate>get("createdAt")).thenReturn(createdAtPath);
        when(criteriaBuilder.greaterThanOrEqualTo(createdAtPath, startDate)).thenReturn(mock(jakarta.persistence.criteria.Predicate.class));

        // Act
        Specification<Client> spec = ClientSpecification.createdBetween(startDate, null);
        spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        verify(criteriaBuilder).greaterThanOrEqualTo(createdAtPath, startDate);
    }

    @Test
    void createdBetween_WithNullDates_ShouldReturnNull() {
        // Arrange
        Root<Client> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        // Act
        Specification<Client> spec = ClientSpecification.createdBetween(null, null);
        var result = spec.toPredicate(root, query, criteriaBuilder);

        // Assert
        assertNull(result);
        verify(criteriaBuilder, never()).between(
                any(jakarta.persistence.criteria.Expression.class),
                any(LocalDate.class),
                any(LocalDate.class));
    }
}