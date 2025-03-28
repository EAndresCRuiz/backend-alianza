package com.alianza.clients.repository;

import com.alianza.clients.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones de acceso a datos relacionadas con la entidad Cliente.
 * Extiende JpaRepository para heredar operaciones CRUD básicas.
 */
@Repository
public interface IClientRepository extends JpaRepository<Client, String> {

    /**
     * Busca clientes cuya sharedKey contenga la cadena especificada, ignorando mayúsculas y minúsculas.
     *
     * @param sharedKey Cadena a buscar dentro de la sharedKey
     * @return Lista de clientes que coinciden con el criterio de búsqueda
     */
    List<Client> findBySharedKeyContainingIgnoreCase(String sharedKey);

    /**
     * Verifica si existe un cliente con la sharedKey especificada.
     *
     * @param sharedKey sharedKey a verificar
     * @return true si existe un cliente con la sharedKey, false en caso contrario
     */
    boolean existsBySharedKey(String sharedKey);
}