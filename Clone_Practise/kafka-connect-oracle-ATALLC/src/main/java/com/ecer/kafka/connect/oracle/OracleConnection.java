package com.ecer.kafka.connect.oracle;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  
 * @author Erdem Cer (erdemcer@gmail.com)
 */

public class OracleConnection{
    static final Logger log = LoggerFactory.getLogger(OracleConnection.class);
    private static HikariDataSource ds;
    private static OracleSourceConnectorConfig oracleConfig;

    public Connection connect(OracleSourceConnectorConfig oracleConfig) throws SQLException{
        if (ds == null || !OracleConnection.oracleConfig.equals(oracleConfig)) {
            HikariConfig hikariConfig = new HikariConfig();

            hikariConfig.setJdbcUrl("jdbc:oracle:thin:@"+oracleConfig.getDbHostName()+":"+oracleConfig.getDbPort()+"/"+oracleConfig.getDbName());
            hikariConfig.setUsername(oracleConfig.getDbUser());
            hikariConfig.setPassword(oracleConfig.getDbUserPassword());
            hikariConfig.setMaximumPoolSize(3);

            OracleConnection.ds = new HikariDataSource(hikariConfig);
            OracleConnection.oracleConfig = oracleConfig;
        }
        return OracleConnection.ds.getConnection();
    }

    public static void close() {
        log.info("Trying to close Hikari data source");
        try {
            ds.close();
        }
        catch (Exception e) {
            log.info("Got an error while trying to close data source");
        }
    }
}