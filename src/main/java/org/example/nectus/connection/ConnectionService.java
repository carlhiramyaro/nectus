package org.example.nectus.connection;

import lombok.RequiredArgsConstructor;
import org.example.nectus.common.security.SecurityUtils;
import org.example.nectus.connection.dto.ConnectionRequest;
import org.example.nectus.connection.dto.ConnectionResponse;
import org.example.nectus.user.User;
import org.example.nectus.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Security;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ConnectionResponse sendRequest(ConnectionRequest request){
        System.out.println(">>> send request() called");
        User currentUser = SecurityUtils.getCurrentUser();
        System.out.println(">>> currentUser: " + currentUser.getId());
        UUID targetId = request.targetUserId();

        if(currentUser.getId().equals(targetId)){
            throw new RuntimeException("You cannot connect with yourself");
        }

        User targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        connectionRepository.findConnectionBetween(currentUser.getId(), targetId)
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "Coneection already exists with satus: " + existing.getStatus()
                    );
                });

        Connection connection = Connection.builder()
                .requester(currentUser)
                .addressee(targetUser)
                .status(ConnectionStatus.PENDING)
                .build();

        connectionRepository.save(connection);
        return mapToResponse(connection, currentUser.getId());
    }

    @Transactional
    public ConnectionResponse respondToRequest(UUID connectionId, ConnectionStatus newStatus){
        User currentUser = SecurityUtils.getCurrentUser();

        if(newStatus != ConnectionStatus.ACCEPTED && newStatus != ConnectionStatus.REJECTED){
            throw new RuntimeException("Invalid response status");
        }

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        if (!connection.getAddressee().getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not the receipt of this request");
        }

        if(connection.getStatus() != ConnectionStatus.PENDING){
            throw new RuntimeException("This request has already been responded to");
        }

        connection.setStatus(newStatus);
        connectionRepository.save(connection);
        return mapToResponse(connection, currentUser.getId());
    }

    public List<ConnectionResponse> getMyConnections(){
        User currentUser = SecurityUtils.getCurrentUser();
        return connectionRepository.findAcceptedConnections(currentUser.getId())
                .stream()
                .map(c -> mapToResponse(c, currentUser.getId()))
                .toList();
    }

    public List<ConnectionResponse> getPendingRequests(){
        User currentUser = SecurityUtils.getCurrentUser();
        return connectionRepository
                .findByAddresseeIdAndStatus(currentUser.getId(), ConnectionStatus.PENDING)
                .stream()
                .map(c -> mapToResponse(c, currentUser.getId()))
                .toList();
    }

    public List<ConnectionResponse> getSentRequests(){
        User currentUser = SecurityUtils.getCurrentUser();
        return connectionRepository
                .findByRequesterIdAndStatus(currentUser.getId(), ConnectionStatus.PENDING)
                .stream()
                .map(c -> mapToResponse(c, currentUser.getId()))
                .toList();
    }

    @Transactional
    public void removeConnection(UUID connectionId){
        User currentUser = SecurityUtils.getCurrentUser();

        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        boolean isInvolved =
                connection.getRequester().getId().equals(currentUser.getId()) ||
                        connection.getAddressee().getId().equals(currentUser.getId());

        if (!isInvolved){
            throw new RuntimeException("Not authorized to remove this connection");
        }

        connectionRepository.delete(connection);
    }

    private ConnectionResponse mapToResponse(Connection connection, UUID currentUserId){
        boolean iAmRequester = connection.getRequester().getId().equals(currentUserId);
        User otherUser = iAmRequester ? connection.getAddressee() : connection.getRequester();

        return new ConnectionResponse(
                connection.getId(),
                otherUser.getId(),
                otherUser.getFullName(),
                otherUser.getHeadline(),
                otherUser.getProfilePictureUrl(),
                connection.getStatus(),
                iAmRequester,
                connection.getCreatedAt()

        );

    }



}
