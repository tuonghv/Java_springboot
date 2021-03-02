package com.ecer.kafka.connect.oracle;

import com.ecer.kafka.connect.oracle.models.Data;
import com.ecer.kafka.connect.oracle.models.DataSchemaStruct;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static com.ecer.kafka.connect.oracle.OracleConnectorSchema.*;

/**
 * @author Erdem Cer (erdemcer@gmail.com)
 */

public class OracleSourceTask extends SourceTask {
    static final Logger log = LoggerFactory.getLogger(OracleSourceTask.class);
    private String dbName;
    private Long streamOffsetScn;
    private Long streamOffsetCommitScn;
    private String streamOffsetRowId;
    private Long streamOffsetCtrl;
    private String topic = null;
    public OracleSourceConnectorConfig config;
    private OracleSourceConnectorUtils utils;
    private static Connection dbConn;
    private static Connection previousDbConn;
    String logMinerOptions = OracleConnectorSQL.LOGMINER_START_OPTIONS;
    String logMinerStartScr = OracleConnectorSQL.START_LOGMINER_CMD;
    static CallableStatement logMinerStartStmt = null;
    CallableStatement logMinerStopStmt = null;
    String logMinerSelectSql;
    static PreparedStatement logMinerSelect;
    PreparedStatement currentSCNStmt;
    ResultSet logMinerData;
    ResultSet currentScnResultSet;
    private boolean closed = false;
    Boolean parseDmlData;
    static int ix = 0;
    boolean skipRecord = true;
    private DataSchemaStruct dataSchemaStruct;
    private ConnectorSQL sql;
    static private boolean dbConnInvalidated = false;

    public OracleSourceTask() throws IOException {
        this.sql = new ConnectorSQL();
    }

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    public static Connection getThreadConnection() {
        return dbConn;
    }

    private static void closePreviousDbConn() {
        log.info("trying to close dbConn");
        try {
//            dbConn.prepareCall("DBMS_LOGMNR.END_LOGMNR;").execute();
////            logMinerSelect.cancel();
////            log.info("Completed select cancel");
////            logMinerStartStmt.cancel();
////            log.info("Completed statement cancel");
            dbConn.close();
            log.info("Closed dbConn successfully");
        } catch (SQLException e) {
            log.error("Failed to close connection: {}", e.getMessage());
        }
        log.info("Finished closing dbConn");
    }

    public static void closeConnectionPool() {
        OracleConnection.close();
    }

    public static void invalidateDbConn() {
        previousDbConn = dbConn;
        dbConnInvalidated = true;
    }

    @Override
    public void start(Map<String, String> map) {
        boolean succeeded = false;

        while (!succeeded) {
            //TODO: Do things here that are required to start your task. This could be open a connection to a database, etc.
            config = new OracleSourceConnectorConfig(map);
            topic = config.getTopic();
            dbName = config.getDbNameAlias();
            parseDmlData = config.getParseDmlData();
            String startSCN = config.getStartScn();
            log.info("Oracle Kafka Connector is starting on {}", config.getDbNameAlias());
            try {
                dbConn = new OracleConnection().connect(config);
                utils = new OracleSourceConnectorUtils(config, sql);
                log.info("Connecting to database version {}", utils.getDbVersion(dbConn));
                logMinerSelectSql = utils.getLogMinerSelectSql();

                log.info("Starting LogMiner Session");
                logMinerStartScr = OracleConnectorSQL.START_LOGMINER_CMD + logMinerOptions + ") \n; end;";
                log.debug("logMinerStartScr: " + logMinerStartScr);
                Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(LOG_MINER_OFFSET_FIELD, dbName));
                log.debug("offset: " + offset);
                streamOffsetScn = 0L;
                streamOffsetCommitScn = 0L;
                streamOffsetRowId = "";
                if (offset != null) {
                    Object lastRecordedOffset = offset.get(POSITION_FIELD);
                    Object commitScnPositionObject = offset.get(COMMITSCN_POSITION_FIELD);
                    Object rowIdPositionObject = offset.get(ROWID_POSITION_FIELD);
                    streamOffsetScn = (lastRecordedOffset != null) ? Long.parseLong(String.valueOf(lastRecordedOffset)) : 0L;
                    streamOffsetCommitScn = (commitScnPositionObject != null) ? Long.parseLong(String.valueOf(commitScnPositionObject)) : 0L;
                    streamOffsetRowId = (rowIdPositionObject != null) ? (String) offset.get(ROWID_POSITION_FIELD) : "";
                }

                if (streamOffsetScn != 0L) {
                    streamOffsetCtrl = streamOffsetScn;
                    log.debug("lastscn_startpos: " + OracleConnectorSQL.LASTSCN_STARTPOS);
                    PreparedStatement lastScnFirstPosPs = dbConn.prepareCall(OracleConnectorSQL.LASTSCN_STARTPOS);
                    lastScnFirstPosPs.setLong(1, streamOffsetScn);
                    lastScnFirstPosPs.setLong(2, streamOffsetScn);
                    ResultSet lastScnFirstPosRSet = lastScnFirstPosPs.executeQuery();
                    while (lastScnFirstPosRSet.next()) {
                        streamOffsetScn = lastScnFirstPosRSet.getLong("FIRST_CHANGE#");
                    }
                    lastScnFirstPosRSet.close();
                    lastScnFirstPosPs.close();

                    //streamOffsetScn=lastScnFirstPos-1;
                    //streamOffsetScn=lastScnFirstPos;
                    log.info("Captured last SCN has first position:{}", streamOffsetScn);
                }

                if (!startSCN.equals("")) {
                    log.info("Resetting offset with specified start SCN:{}", startSCN);
                    streamOffsetScn = Long.parseLong(startSCN);
                    //streamOffsetScn-=1;
                    skipRecord = false;
                }

                if (config.getResetOffset()) {
                    log.info("Resetting offset with new SCN");
                    streamOffsetScn = 0L;
                    streamOffsetCommitScn = 0L;
                    streamOffsetRowId = "";
                }

                if (streamOffsetScn == 0L) {
                    skipRecord = false;
                    log.debug("current_db_scn_sql: " + OracleConnectorSQL.CURRENT_DB_SCN_SQL);
                    currentSCNStmt = dbConn.prepareCall(OracleConnectorSQL.CURRENT_DB_SCN_SQL);
                    currentScnResultSet = currentSCNStmt.executeQuery();
                    while (currentScnResultSet.next()) {
                        streamOffsetScn = currentScnResultSet.getLong("CURRENT_SCN");
                    }
                    currentScnResultSet.close();
                    currentSCNStmt.close();
                    log.info("Getting current scn from database {}", streamOffsetScn);
                }
                //streamOffsetScn+=1;
                log.debug("Commit SCN : " + streamOffsetCommitScn);
                log.info(String.format("Log Miner will start at new position SCN : %s with fetch size : %s", streamOffsetScn, config.getDbFetchSize()));
                logMinerData = createLogminerDataResultSet(streamOffsetScn, dbConn);
                log.info("Logminer started successfully");
                succeeded = true;
            } catch (SQLException e) {
                log.error("Error at database tier, Please check: " + e.toString());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private ResultSet createLogminerDataResultSet(Long streamOffsetScn, Connection connection) throws SQLException {
        logMinerStartStmt = connection.prepareCall(logMinerStartScr);
        logMinerStartStmt.setLong(1, streamOffsetScn);
        logMinerStartStmt.execute();
        log.debug("logMinerSelectSql: " + logMinerSelectSql);
        logMinerSelect = connection.prepareCall(logMinerSelectSql);
        logMinerSelect.setFetchSize(config.getDbFetchSize());
        logMinerSelect.setLong(1, streamOffsetCommitScn);
        return logMinerSelect.executeQuery();
    }

    private ResultSet createLogminerDataResultSetOnPoll(Connection dbConn) throws SQLException {
        Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(LOG_MINER_OFFSET_FIELD, dbName));
        Object lastRecordedOffset = offset.get(POSITION_FIELD);
        Long offsetSCN = (lastRecordedOffset != null) ? Long.parseLong(String.valueOf(lastRecordedOffset)) : 0L;
        return createLogminerDataResultSet(offsetSCN, dbConn);
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        //TODO: Create SourceRecord objects that will be sent the kafka cluster.
        String sqlX = "";
        try {
            if (dbConnInvalidated) {
                closePreviousDbConn();
                createNewConnection();
                dbConnInvalidated = false;
            }
            ArrayList<SourceRecord> records = new ArrayList<>();
            if (logMinerData != null) {
                // && logMinerData != null
                while (!this.closed && logMinerData.next()) {
                    if (log.isDebugEnabled()) {
                        logRawMinerData();
                    }
                    Long scn = logMinerData.getLong(SCN_FIELD);
                    Long commitScn = logMinerData.getLong(COMMIT_SCN_FIELD);
                    String rowId = logMinerData.getString(ROW_ID_FIELD);
                    boolean contSF = logMinerData.getBoolean(CSF_FIELD);
                    if (skipRecord) {
                        if ((scn.equals(streamOffsetCtrl)) && (commitScn.equals(streamOffsetCommitScn)) && (rowId.equals(streamOffsetRowId)) && (!contSF)) {
                            skipRecord = false;
                        }
                        log.info("Skipping data with scn :{} Commit Scn :{} Rowid :{}", scn, commitScn, rowId);
                        continue;
                    }
                    //log.info("Data :"+scn+" Commit Scn :"+commitScn);

                    ix++;

                    //String containerId = logMinerData.getString(SRC_CON_ID_FIELD);
                    //log.info("logminer event from container {}", containerId);
                    String segOwner = logMinerData.getString(SEG_OWNER_FIELD);
                    String segName = logMinerData.getString(TABLE_NAME_FIELD);
                    String sqlRedo = logMinerData.getString(SQL_REDO_FIELD);
                    if (sqlRedo.contains(TEMPORARY_TABLE)) continue;

                    while (contSF) {
                        logMinerData.next();
                        sqlRedo += logMinerData.getString(SQL_REDO_FIELD);
                        contSF = logMinerData.getBoolean(CSF_FIELD);
                    }
                    sqlX = sqlRedo;
                    Timestamp timeStamp = logMinerData.getTimestamp(TIMESTAMP_FIELD);
                    String operation = logMinerData.getString(OPERATION_FIELD);
                    Data row = new Data(scn, segOwner, segName, sqlRedo, timeStamp, operation);
                    topic = config.getTopic().equals("") ? (config.getDbNameAlias() + DOT + row.getSegOwner() + DOT + row.getSegName()).toUpperCase() : topic;
                    //log.info(String.format("Fetched %s rows from database %s ",ix,config.getDbNameAlias())+" "+row.getTimeStamp()+" "+row.getSegName()+" "+row.getScn()+" "+commitScn);
                    if (ix % 100 == 0)
                        log.info(String.format("Fetched %s rows from database %s ", ix, config.getDbNameAlias()) + " " + row.getTimeStamp());
                    dataSchemaStruct = utils.createDataSchema(segOwner, segName, sqlRedo, operation, dbConn);
                    records.add(new SourceRecord(sourcePartition(), sourceOffset(scn, commitScn, rowId), topic, dataSchemaStruct.getDmlRowSchema(), setValueV2(row, dataSchemaStruct)));
                    streamOffsetScn = scn;
                    return records;
                }
                log.info("Logminer stoppped successfully");
            } else {
                log.error("logMinerData was null");
                handlePollFailure();
            }

        } catch (SQLException e) {
            log.error("SQL error during poll", e);
            handlePollFailure();
        } catch (JSQLParserException e) {
            log.error("SQL parser error during poll ", e);
            handlePollFailure();
        } catch (Exception e) {
            log.error("Error during poll on topic {} SQL :{}", topic, sqlX, e);
            handlePollFailure();
        }
        return null;

    }

    private void handlePollFailure() throws InterruptedException {
        closePreviousDbConn();
        createNewConnection();
        Thread.sleep(5000);
    }

    private void createNewConnection() {
        try {
            dbConn = new OracleConnection().connect(config);
            logMinerData = createLogminerDataResultSetOnPoll(dbConn);
        } catch (SQLException e) {
            log.error("Failed to recreate logminer resultSet: {}", e.getMessage());
        }
    }

    @Override
    public void stop() {
        log.info("Stop called for logminer");
        this.closed = true;
        try {
            log.info("Logminer session cancel");
            logMinerSelect.cancel();
            if (dbConn != null) {
                log.info("Closing database connection.Last SCN : {}", streamOffsetScn);
                logMinerSelect.close();
                logMinerStartStmt.close();
                dbConn.close();
            }
        } catch (SQLException e) {
        }

    }

    private Struct setValueV2(Data row, DataSchemaStruct dataSchemaStruct) {
        Struct valueStruct = new Struct(dataSchemaStruct.getDmlRowSchema())
                .put(SCN_FIELD, row.getScn())
                .put(SEG_OWNER_FIELD, row.getSegOwner())
                .put(TABLE_NAME_FIELD, row.getSegName())
                .put(TIMESTAMP_FIELD, row.getTimeStamp())
                .put(SQL_REDO_FIELD, row.getSqlRedo())
                .put(OPERATION_FIELD, row.getOperation())
                .put(DATA_ROW_FIELD, dataSchemaStruct.getDataStruct())
                .put(BEFORE_DATA_ROW_FIELD, dataSchemaStruct.getBeforeDataStruct());
        return valueStruct;

    }

    private Map<String, String> sourcePartition() {
        return Collections.singletonMap(LOG_MINER_OFFSET_FIELD, dbName);
    }

    private Map<String, String> sourceOffset(Long scnPosition, Long commitScnPosition, String rowId) {
        //return Collections.singletonMap(POSITION_FIELD, scnPosition);
        Map<String, String> offSet = new HashMap<String, String>();
        offSet.put(POSITION_FIELD, scnPosition.toString());
        offSet.put(COMMITSCN_POSITION_FIELD, commitScnPosition.toString());
        offSet.put(ROWID_POSITION_FIELD, rowId);
        return offSet;
    }

    private void logRawMinerData() throws SQLException {
        if (log.isDebugEnabled()) {
            StringBuffer b = new StringBuffer();
            for (int i = 1; i < logMinerData.getMetaData().getColumnCount(); i++) {
                String columnName = logMinerData.getMetaData().getColumnName(i);
                Object columnValue = logMinerData.getObject(i);
                b.append("[" + columnName + "=" + (columnValue == null ? "NULL" : columnValue.toString()) + "]");
            }
            log.debug(b.toString());
        }
    }
}

