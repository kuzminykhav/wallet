package org.wallet.walletclient.service;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.proto.WalletGrpc;
import org.wallet.walletdata.proto.WalletOuterClass;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WalletGrpcClientImpl {

    private final ManagedChannel managedChannel;
    private final org.wallet.walletdata.proto.WalletGrpc.WalletBlockingStub blockingStub;

    private org.wallet.walletdata.proto.WalletGrpc.WalletBlockingStub walletServiceBlockingStub;


    public WalletGrpcClientImpl() throws InterruptedException {
        this(ManagedChannelBuilder.forAddress("localhost", 8089)
                .usePlaintext()
                .build());
        deposit(1L, BigDecimal.TEN, Currency.EUR);
        balance(1L);
        withdraw(1L, BigDecimal.ONE, Currency.EUR);
        balance(1L);
        withdraw(1L, BigDecimal.ONE, Currency.EUR);
        balance(1L);

        shutdown();
    }

    WalletGrpcClientImpl(ManagedChannel channel) {
        this.managedChannel = channel;
        blockingStub = WalletGrpc.newBlockingStub(channel);
    }

    public void deposit(Long userId, BigDecimal amount, Currency currency) {
        log.info("Will try to deposit ...");
        WalletOuterClass.DepositRequest request = WalletOuterClass.DepositRequest.newBuilder()
                .setCurrency(WalletOuterClass.Currency.valueOf(currency.name()))
                .setUserId(userId)
                .setAmount(WalletOuterClass.BDecimal.newBuilder()
                        .setScale(amount.scale())
                        .setIntVal(
                                WalletOuterClass.BInteger.newBuilder()
                                        .setValue(ByteString.copyFrom(amount.unscaledValue().toByteArray()))
                                        .build()
                        )
                        .build())
                .build();
        WalletOuterClass.DepositResponse response;
        try {
            response = blockingStub.deposit(request);
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
            return;
        }
        log.info("Result deposit is success ");
    }

    public void withdraw(Long userId, BigDecimal amount, Currency currency) {
        log.info("Will try to withdraw ...");
        WalletOuterClass.WithdrawRequest request = WalletOuterClass.WithdrawRequest.newBuilder()
                .setCurrency(WalletOuterClass.Currency.valueOf(currency.name()))
                .setUserId(userId)
                .setAmount(WalletOuterClass.BDecimal.newBuilder()
                        .setScale(amount.scale())
                        .setIntVal(
                                WalletOuterClass.BInteger.newBuilder()
                                        .setValue(ByteString.copyFrom(amount.unscaledValue().toByteArray()))
                                        .build()
                        )
                        .build())
                .build();
        WalletOuterClass.WithdrawResponse response;
        try {
            response = blockingStub.withdraw(request);
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
            return;
        }
        log.info("Result withdraw is success ");
    }

    public void balance(Long userId) {
        log.info("Will try to get balance ...");
        WalletOuterClass.BalanceRequest request = WalletOuterClass.BalanceRequest.newBuilder()
                .setUserId(userId)
                .build();
        WalletOuterClass.BalanceResponse response;
        try {
            response = blockingStub.balance(request);
            response.getBalanceMap()
                    .forEach((key, value) -> log.info("Balance currency {} amount {}", key,
                            new BigDecimal(new java.math.BigInteger(
                                    value.getIntVal().getValue().toByteArray()),
                                    value.getScale()).toString()
                    ));
        } catch (StatusRuntimeException e) {
            log.warn("RPC failed: {}", e.getStatus());
            return;
        }
        log.info("Result balance is success ");
    }

    public void shutdown() throws InterruptedException {
        managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @PostConstruct
    private void initializeClient() {

        walletServiceBlockingStub = org.wallet.walletdata.proto.WalletGrpc.newBlockingStub(managedChannel);

    }

}
