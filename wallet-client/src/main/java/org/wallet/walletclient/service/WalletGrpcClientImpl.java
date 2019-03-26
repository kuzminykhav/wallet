package org.wallet.walletclient.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.proto.WalletGrpc;
import org.wallet.walletdata.proto.WalletOuterClass;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Service
public class WalletGrpcClientImpl {

    private Logger logger = LoggerFactory.getLogger(WalletGrpcClient.class);

    private final ManagedChannel managedChannel;
    private final org.wallet.walletdata.proto.WalletGrpc.WalletBlockingStub blockingStub;

    private org.wallet.walletdata.proto.WalletGrpc.WalletBlockingStub walletServiceBlockingStub;


    public WalletGrpcClientImpl() {
        this(ManagedChannelBuilder.forAddress("localhost",8089)
                .usePlaintext()
                .build());
        deposit(1L,null,Currency.EUR);
    }

    /** Construct client for accessing HelloWorld server using the existing channel. */
    WalletGrpcClientImpl(ManagedChannel channel) {
        this.managedChannel = channel;
        blockingStub = WalletGrpc.newBlockingStub(channel);
    }

    /** Say hello to server. */
    public void deposit(Long userId, BigDecimal amount, Currency currency) {
        logger.info("Will try to deposit ...");
        WalletOuterClass.DepositRequest request = WalletOuterClass.DepositRequest.newBuilder()
                .setCurrency(WalletOuterClass.Currency.EUR).setUserId(1L).build();
        WalletOuterClass.DepositResponse response;
        try {
            response = blockingStub.deposit(request);
        } catch (StatusRuntimeException e) {
            logger.warn( "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Result success ");
    }

    public void shutdown() throws InterruptedException {
        managedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @PostConstruct
    private void initializeClient() {

        walletServiceBlockingStub = org.wallet.walletdata.proto.WalletGrpc.newBlockingStub(managedChannel);

    }

}
