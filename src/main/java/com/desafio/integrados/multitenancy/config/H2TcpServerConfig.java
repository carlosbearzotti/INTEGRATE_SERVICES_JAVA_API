package com.desafio.integrados.multitenancy.config;

import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
@ConditionalOnClass(Server.class)
public class H2TcpServerConfig {

    /**
     * Inicia o servidor TCP do H2 na porta 9092 com permissão de conexões externas
     * e flag -ifNotExists para compatibilidade total com o DBeaver.
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists");
    }
}
