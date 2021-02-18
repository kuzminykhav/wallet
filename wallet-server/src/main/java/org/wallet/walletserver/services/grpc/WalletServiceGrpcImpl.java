package org.wallet.walletserver.services.grpc;

import com.google.protobuf.ByteString;
import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.proto.WalletOuterClass;
import org.wallet.walletserver.services.jpa.TransactionService;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class WalletServiceGrpcImpl extends org.wallet.walletdata.proto.WalletGrpc.WalletImplBase implements BindableService {

    private final TransactionService transactionService;

    @Override
    public void deposit(WalletOuterClass.DepositRequest request, StreamObserver<WalletOuterClass.DepositResponse> responseObserver) {
        transactionService.depositTransaction(request.getUserId(),
                new BigDecimal(new java.math.BigInteger(
                        request.getAmount().getIntVal().getValue().toByteArray()),
                        request.getAmount().getScale()),
                Currency.valueOf(request.getCurrency().name()));
        WalletOuterClass.DepositResponse response = WalletOuterClass.DepositResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WalletOuterClass.WithdrawRequest request, StreamObserver<WalletOuterClass.WithdrawResponse> responseObserver) {
        transactionService.withdrawTransaction(request.getUserId(),
                new BigDecimal(new java.math.BigInteger(
                        request.getAmount().getIntVal().getValue().toByteArray()),
                        request.getAmount().getScale()),
                Currency.valueOf(request.getCurrency().name()));
        WalletOuterClass.WithdrawResponse response = WalletOuterClass.WithdrawResponse.newBuilder()
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void balance(WalletOuterClass.BalanceRequest request, StreamObserver<WalletOuterClass.BalanceResponse> responseObserver) {

        WalletOuterClass.BalanceResponse response = WalletOuterClass.BalanceResponse.newBuilder()
                .putAllBalance(transactionService.getBalance(request.getUserId()).getBalance()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey().name(),
                                e -> WalletOuterClass.BDecimal.newBuilder()
                                        .setScale(e.getValue().scale())
                                        .setIntVal(
                                                WalletOuterClass.BInteger.newBuilder()
                                                        .setValue(ByteString.copyFrom(e.getValue().unscaledValue().toByteArray()))
                                                        .build()
                                        )
                                        .build())))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
