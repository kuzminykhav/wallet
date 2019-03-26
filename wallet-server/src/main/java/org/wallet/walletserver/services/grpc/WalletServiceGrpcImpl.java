package org.wallet.walletserver.services.grpc;

import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wallet.walletdata.model.Currency;
import org.wallet.walletdata.proto.WalletOuterClass;
import org.wallet.walletserver.services.jpa.TransactionService;

import java.math.BigDecimal;

@Service
public class WalletServiceGrpcImpl extends org.wallet.walletdata.proto.WalletGrpc.WalletImplBase implements BindableService {

    private Logger logger = LoggerFactory.getLogger(WalletServiceGrpcImpl.class);

    @Autowired
    private TransactionService transactionService;

    @Override
    public void deposit(org.wallet.walletdata.proto.WalletOuterClass.DepositRequest request, StreamObserver<WalletOuterClass.DepositResponse> responseObserver) {
        logger.debug("Request {}",request);
        transactionService.depositTransaction(request.getUserId(),BigDecimal.valueOf(1L), Currency.valueOf(request.getCurrency().name()));

        logger.debug("Response {1}",responseObserver);
        super.deposit(request, responseObserver);
    }

    @Override
    public void withdraw(org.wallet.walletdata.proto.WalletOuterClass.WithdrawRequest request, StreamObserver<WalletOuterClass.WithdrawResponse> responseObserver) {
        logger.debug("Request {}",request);


        logger.debug("Response {}",responseObserver);
        super.withdraw(request, responseObserver);
    }

    @Override
    public void balance(org.wallet.walletdata.proto.WalletOuterClass.BalanceRequest request, StreamObserver<WalletOuterClass.BalanceResponse> responseObserver) {
        logger.debug("Request {}",request);


        logger.debug("Response {}",responseObserver);
        super.balance(request, responseObserver);
    }

}
