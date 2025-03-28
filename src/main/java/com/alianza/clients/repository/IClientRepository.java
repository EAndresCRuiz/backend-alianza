package com.alianza.clients.repository;

import com.alianza.clients.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para operaciones de acceso a datos relacionadas con la entidad Cliente.
 * Extiende JpaRepository para heredar operaciones CRUD básicas.
 */
@Repository
public interface IClientRepository extends JpaRepository<Client, String> {
}