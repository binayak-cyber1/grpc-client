package com.example.grpc_client.service;

import com.example.enterprise_api.grpc.*;
import com.example.grpc_client.model.UserDtos;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserGrpcService {

    private final ReactorUserServiceGrpc.ReactorUserServiceStub stub;

    public UserGrpcService(ReactorUserServiceGrpc.ReactorUserServiceStub stub) {
        this.stub = stub;
    }

    public Mono<UserResponse> createUser(UserDtos.CreateUserRequest dto) {
        return stub.createUser(
                CreateUserRequest.newBuilder()
                        .setFirstName(dto.firstName())
                        .setLastName(dto.lastName())
                        .setAddress(toProtoAddress(dto.address()))
                        .build()
        );
    }

    public Mono<UserResponse> getUser(String id) {
        return stub.getUser(GetUserRequest.newBuilder().setId(id).build());
    }

    public Flux<User> listUsers() {
        return stub.listUsers(Empty.newBuilder().build())
                .flatMapIterable(UserListResponse::getUsersList);
    }

    public Mono<UserResponse> updateUser(String id, UserDtos.UpdateUserRequest dto) {
        return stub.updateUser(
                UpdateUserRequest.newBuilder()
                        .setId(id)
                        .setFirstName(dto.firstName())
                        .setLastName(dto.lastName())
                        .build()
        );
    }

    public Mono<AddressResponse> updateAddress(String userId, UserDtos.UpdateAddressRequest dto) {
        return stub.updateAddress(
                UpdateAddressRequest.newBuilder()
                        .setUserId(userId)
                        .setAddress(toProtoAddress(dto.address()))
                        .build()
        );
    }

    public Mono<AddressResponse> getAddress(String userId) {
        return stub.getAddress(GetAddressRequest.newBuilder().setUserId(userId).build());
    }

    public Mono<Void> deleteUser(String id) {
        return stub.deleteUser(DeleteUserRequest.newBuilder().setId(id).build())
                .then();
    }

    private Address toProtoAddress(UserDtos.AddressDto dto) {
        if (dto == null) return Address.getDefaultInstance();
        Address.Builder builder = Address.newBuilder();
        if (dto.id() != null)           builder.setId(dto.id());
        if (dto.addressLine1() != null) builder.setAddressLine1(dto.addressLine1());
        if (dto.addressLine2() != null) builder.setAddressLine2(dto.addressLine2());
        if (dto.country() != null)      builder.setCountry(dto.country());
        if (dto.zip() != null)          builder.setZip(dto.zip());
        return builder.build();
    }
}
