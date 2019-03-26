package org.wallet.walletserver;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;
import org.wallet.walletserver.services.grpc.WalletServiceGrpcImpl;

import java.util.Optional;

@Component
public class GrpcServerRunner implements CommandLineRunner, DisposableBean {

    private Logger logger = LoggerFactory.getLogger(GrpcServerRunner.class);

    @Autowired
    private AbstractApplicationContext applicationContext;

    private Server server;

    @Override
    public void destroy() throws Exception {
        logger.info("Shutting down gRPC server ...");
        Optional.ofNullable(server).ifPresent(Server::shutdown);
        logger.info("gRPC server stopped.");

    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting gRPC Server ...");

        BindableService srv = applicationContext.getBeanFactory()
                    .getBean(WalletServiceGrpcImpl.class);

        server = ServerBuilder.forPort(8089)
                .addService(srv)
                .build()
                .start();

        logger.info("'{}' service has been registered.", srv.getClass().getName());

        logger.info("gRPC Server started, listening on port {}.", server.getPort());
        startDaemonAwaitThread();

    }

    private void startDaemonAwaitThread() {
        Thread awaitThread = new Thread(()->{
            try {
                GrpcServerRunner.this.server.awaitTermination();
            } catch (InterruptedException e) {
                logger.error("gRPC server stopped.", e);
            }
        });
        awaitThread.setDaemon(false);
        awaitThread.start();
    }
}
