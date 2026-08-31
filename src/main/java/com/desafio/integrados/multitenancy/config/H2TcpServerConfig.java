package com.desafio.integrados.multitenancy.config;

import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.sql.SQLException;

@Configuration
@ConditionalOnClass(Server.class)
public class H2TcpServerConfig {

    /**
     * Inicia o servidor TCP do H2 na porta 9092 apontando o baseDir para a pasta ./data
     * permitindo que o DBeaver conecte passando apenas 'integrados_db' como Database name.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        File dataDir = new File("./data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        return Server.createTcpServer(
                "-tcp",
                "-tcpAllowOthers",
                "-tcpPort", "9092",
                "-baseDir", dataDir.getAbsolutePath(),
                "-ifNotExists"
        );
    }
}
