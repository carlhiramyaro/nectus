package org.example.nectus.connection;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.nectus.connection.dto.ConnectionRequest;
import org.example.nectus.connection.dto.ConnectionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/connection")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;

    @PostMapping
    public ResponseEntity<ConnectionResponse> sendRequest(
            @Valid @RequestBody ConnectionRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(connectionService.sendRequest(request));
    }

    @PatchMapping("/{connectionId}/accept")
    public ResponseEntity<ConnectionResponse> accept(@PathVariable UUID connectionId){
        return ResponseEntity.ok(
                connectionService.respondToRequest(connectionId, ConnectionStatus.ACCEPTED)
        );
    }

    @PatchMapping("/{connectionId}/reject")
    public ResponseEntity<ConnectionResponse> reject(@PathVariable UUID connectionId){
        return ResponseEntity.ok(
                connectionService.respondToRequest(connectionId, ConnectionStatus.REJECTED)
        );
    }

    @GetMapping
    public ResponseEntity<List<ConnectionResponse>> getMyConnections(){
        return ResponseEntity.ok(connectionService.getMyConnections());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ConnectionResponse>> getPendingRequest(){
        return ResponseEntity.ok(connectionService.getPendingRequests());
    }

    @GetMapping("/sent")
    public ResponseEntity<List<ConnectionResponse>> getSentRequests(){
        return ResponseEntity.ok(connectionService.getSentRequests());
    }

    @DeleteMapping("/{connectionId}")
    public ResponseEntity<Void> removeConnection(@PathVariable UUID connectionId){
        connectionService.removeConnection(connectionId);
        return ResponseEntity.noContent().build();
    }




}
