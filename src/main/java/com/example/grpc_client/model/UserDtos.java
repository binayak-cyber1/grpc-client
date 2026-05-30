package com.example.grpc_client.model;

public class UserDtos {

    public record AddressDto(
            String id,
            String addressLine1,
            String addressLine2,
            String country,
            String zip
    ) {}

    public record UserDto(
            String id,
            String firstName,
            String lastName,
            AddressDto address
    ) {}

    public record CreateUserRequest(
            String firstName,
            String lastName,
            AddressDto address
    ) {}

    public record UpdateUserRequest(
            String firstName,
            String lastName
    ) {}

    public record UpdateAddressRequest(
            AddressDto address
    ) {}
}