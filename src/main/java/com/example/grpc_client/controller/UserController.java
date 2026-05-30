package com.example.grpc_client.controller;

import com.example.enterprise_api.grpc.AddressResponse;
import com.example.enterprise_api.grpc.UserResponse;
import com.example.grpc_client.model.UserDtos;
import com.example.grpc_client.service.UserGrpcService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserGrpcService grpcService;

    public UserController(UserGrpcService grpcService) {
        this.grpcService = grpcService;
    }

    // POST /api/users
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createUser(
            @RequestBody UserDtos.CreateUserRequest req) {
        return grpcService.createUser(req)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(userToMap(r)));
    }

    // GET /api/users
    @GetMapping
    public Flux<Map<String, Object>> listUsers() {
        return grpcService.listUsers().map(this::protoUserToMap);
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public Mono<Map<String, Object>> getUser(@PathVariable String id) {
        return grpcService.getUser(id).map(this::userToMap);
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public Mono<Map<String, Object>> updateUser(
            @PathVariable String id,
            @RequestBody UserDtos.UpdateUserRequest req) {
        return grpcService.updateUser(id, req).map(this::userToMap);
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable String id) {
        return grpcService.deleteUser(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    // GET /api/users/{userId}/address
    @GetMapping("/{userId}/address")
    public Mono<Map<String, Object>> getAddress(@PathVariable String userId) {
        return grpcService.getAddress(userId).map(this::addressToMap);
    }

    // PUT /api/users/{userId}/address
    @PutMapping("/{userId}/address")
    public Mono<Map<String, Object>> updateAddress(
            @PathVariable String userId,
            @RequestBody UserDtos.UpdateAddressRequest req) {
        return grpcService.updateAddress(userId, req).map(this::addressToMap);
    }

    // ── Mapping helpers: proto → plain Map so Jackson never sees proto internals ──

    private Map<String, Object> userToMap(UserResponse response) {
        return protoUserToMap(response.getUser());
    }

    private Map<String, Object> protoUserToMap(com.example.enterprise_api.grpc.User user) {
        // LinkedHashMap instead of Map.of() — Map.of() throws NullPointerException
        // if any proto field is empty/default, which is common for optional fields
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",        user.getId());
        map.put("firstName", user.getFirstName());
        map.put("lastName",  user.getLastName());
        map.put("address",   protoAddressToMap(user.getAddress()));
        return map;
    }

    private Map<String, Object> addressToMap(AddressResponse response) {
        return protoAddressToMap(response.getAddress());
    }

    private Map<String, Object> protoAddressToMap(com.example.enterprise_api.grpc.Address address) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",           address.getId());
        map.put("addressLine1", address.getAddressLine1());
        map.put("addressLine2", address.getAddressLine2());
        map.put("country",      address.getCountry());
        map.put("zip",          address.getZip());
        return map;
    }
}