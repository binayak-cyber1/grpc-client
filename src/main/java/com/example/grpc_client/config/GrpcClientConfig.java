package com.example.grpc_client.config;

import com.example.enterprise_api.grpc.ReactorUserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.host}")
    private String grpcHost;

    @Value("${grpc.client.port}")
    private int grpcPort;

    private ManagedChannel channel;

    @Bean
    public ManagedChannel managedChannel() {
        channel = ManagedChannelBuilder
                .forTarget("dns:///" + grpcHost + ":" + grpcPort)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .enableRetry()
                .keepAliveTime(30, TimeUnit.SECONDS)        // ← keep connection alive
                .keepAliveTimeout(5, TimeUnit.SECONDS)      // ← timeout for keepalive ping
                .keepAliveWithoutCalls(true)                // ← keep alive even when idle
                .idleTimeout(5, TimeUnit.MINUTES)           // ← don't drop idle connections
                .build();
        return channel;
    }

    @Bean
    public ReactorUserServiceGrpc.ReactorUserServiceStub userServiceStub(ManagedChannel channel) {
        return ReactorUserServiceGrpc.newReactorStub(channel);
    }

    @PreDestroy
    public void shutdownChannel() throws InterruptedException {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}