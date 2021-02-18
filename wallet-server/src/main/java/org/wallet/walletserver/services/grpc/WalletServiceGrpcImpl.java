package org.wallet.walletserver.services.grpc;

import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.proto.WalletOuterClass;
import org.wallet.walletserver.services.jpa.TransactionService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class WalletServiceGrpcImpl extends org.wallet.walletdata.proto.WalletGrpc.WalletImplBase implements BindableService {

    private final TransactionService transactionService;

    @Override
    public void deposit(org.wallet.walletdata.proto.WalletOuterClass.DepositRequest request, StreamObserver<WalletOuterClass.DepositResponse> responseObserver) {
        log.debug("Request {}",request);
        transactionService.depositTransaction(request.getUserId(),BigDecimal.valueOf(1L), Currency.valueOf(request.getCurrency().name()));

        log.debug("Response {1}",responseObserver);
        super.deposit(request, responseObserver);
    }

    @Override
    public void withdraw(org.wallet.walletdata.proto.WalletOuterClass.WithdrawRequest request, StreamObserver<WalletOuterClass.WithdrawResponse> responseObserver) {
        log.debug("Request {}",request);


        log.debug("Response {}",responseObserver);
        super.withdraw(request, responseObserver);
    }

    @Override
    public void balance(org.wallet.walletdata.proto.WalletOuterClass.BalanceRequest request, StreamObserver<WalletOuterClass.BalanceResponse> responseObserver) {
        log.debug("Request {}",request);


        log.debug("Response {}",responseObserver);
        super.balance(request, responseObserver);
    }

}
