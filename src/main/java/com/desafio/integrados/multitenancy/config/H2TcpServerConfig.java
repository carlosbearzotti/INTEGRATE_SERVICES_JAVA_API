package com.desafio.integrados.multitenancy.config;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.sql.SQLException;

@Configuration
@ConditionalOnClass(Server.class)
@ConditionalOnProperty(name = "h2.tcp.enabled", havingValue = "true", matchIfMissing = false)
public class H2TcpServerConfig {

    private static final Logger log = LoggerFactory.getLogger(H2TcpServerConfig.class);

    @Bean(destroyMethod = "stop")
    public Server h2TcpServer() {
        try {
            File dataDir = new File("./data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            Server server = Server.createTcpServer(
                    "-tcp",
                    "-tcpAllowOthers",
                    "-tcpPort", "9092",
                    "-baseDir", dataDir.getAbsolutePath(),
                    "-ifNotExists"
            );
            return server.start();
        } catch (SQLException e) {
            log.warn("Servidor TCP do H2 não pôde ser iniciado na porta 9092: {}", e.getMessage());
            return null;
        }
    }
}


