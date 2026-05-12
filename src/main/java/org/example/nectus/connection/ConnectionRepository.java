package org.example.nectus.connection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConnectionRepository extends JpaRepository<Connection, UUID> {

//    Find a connection between two users regardless of who sent it
    @Query("""
            SELECT c from Connection c
            WHERE (c.requester.id = :userId1 AND c.addressee.id = :userId2)
            OR (c.requester.id = :userId2 AND c.addressee.id = :userId1)

""")
    Optional<Connection> findConnectionBetween(
            @Param("userId1") UUID userId1,
            @Param("userId2") UUID userId2
    );

//    Get all accepted connections for a user
    @Query("""
            SELECT c FROM Connection c
            WHERE (c.requester.id = :userId or c.addressee.id = :userId)
            AND c.status = 'ACCEPTED'
""")
    List<Connection> findAcceptedConnections(@Param("userId") UUID userId);

    List<Connection> findByAddresseeIdAndStatus(UUID addresseeId, ConnectionStatus status);

    List<Connection> findByRequesterIdAndStatus(UUID requesterId, ConnectionStatus status);

}
