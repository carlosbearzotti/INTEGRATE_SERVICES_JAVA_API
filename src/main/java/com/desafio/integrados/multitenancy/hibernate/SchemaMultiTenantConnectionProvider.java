package com.desafio.integrados.multitenancy.hibernate;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SchemaMultiTenantConnectionProvider implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;
    private final Set<String> verifiedSchemas = ConcurrentHashMap.newKeySet();

    public SchemaMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = getAnyConnection();
        ensureSchemaExists(connection, tenantIdentifier);
        connection.setSchema(tenantIdentifier);
        return connection;
    }

    private void ensureSchemaExists(Connection connection, String schemaName) {
        if (!"public".equalsIgnoreCase(schemaName) && verifiedSchemas.add(schemaName)) {
            try (Statement st = connection.createStatement()) {
                st.execute("CREATE SCHEMA IF NOT EXISTS \"" + schemaName + "\"");
            } catch (SQLException ignored) {
            }
        }
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            connection.setSchema("public");
        } catch (SQLException ignored) {
        }
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}
