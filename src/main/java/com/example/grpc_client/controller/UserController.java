package com.example.grpc_client.controller;
import com.example.enterprise_api.grpc.AddressResponse;
import com.example.enterprise_api.grpc.User;
import com.example.enterprise_api.grpc.UserResponse;
import com.example.grpc_client.model.UserDtos;
import com.example.grpc_client.service.UserGrpcService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserGrpcService grpcService;

    public UserController(UserGrpcService grpcService) {
        this.grpcService = grpcService;
    }

    @PostMapping
    public ResponseEntity<Mono<UserResponse>> createUser(@RequestBody UserDtos.CreateUserRequest req) {
        return ResponseEntity.ok(grpcService.createUser(req));
    }

    @GetMapping
    public ResponseEntity<Flux<User>> listUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(grpcService.listUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<UserResponse>> getUser(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(grpcService.getUser(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<UserResponse>> updateUser(@PathVariable String id,
                                                @RequestBody UserDtos.UpdateUserRequest req) {
        return ResponseEntity.status(HttpStatus.OK).body(grpcService.updateUser(id, req));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return grpcService.deleteUser(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    @GetMapping("/{userId}/address")
    public ResponseEntity<Mono<AddressResponse>> getAddress(@PathVariable String userId) {
        return ResponseEntity.ok(grpcService.getAddress(userId));
    }

    @PutMapping("/{userId}/address")
    public ResponseEntity<Mono<AddressResponse>> updateAddress(@PathVariable String userId,
                                                   @RequestBody UserDtos.UpdateAddressRequest req) {
        return ResponseEntity.ok(grpcService.updateAddress(userId, req));
    }
}