package org.wallet.walletserver;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;
import org.wallet.walletserver.services.grpc.WalletServiceGrpcImpl;

import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class GrpcServerRunner implements CommandLineRunner, DisposableBean {

    @Autowired
    private final AbstractApplicationContext applicationContext;

    private Server server;

    @Override
    public void destroy() throws Exception {
        log.info("Shutting down gRPC server ...");
        Optional.ofNullable(server).ifPresent(Server::shutdown);
        log.info("gRPC server stopped.");

    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting gRPC Server ...");

        BindableService srv = applicationContext.getBeanFactory()
                .getBean(WalletServiceGrpcImpl.class);

        server = ServerBuilder.forPort(8089)
                .addService(srv)
                .build()
                .start();

        log.info("'{}' service has been registered.", srv.getClass().getName());

        log.info("gRPC Server started, listening on port {}.", server.getPort());
        startDaemonAwaitThread();

    }

    private void startDaemonAwaitThread() {
        Thread awaitThread = new Thread(() -> {
            try {
                GrpcServerRunner.this.server.awaitTermination();
            } catch (InterruptedException e) {
                log.error("gRPC server stopped.", e);
            }
        });
        awaitThread.setDaemon(false);
        awaitThread.start();
    }
}
