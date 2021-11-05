package de.othr.database_connector.services;
/*
Copyright 2021 Thomas Pilz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

import de.othr.database_connector.kpi.KpiMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.concurrent.*;


/**
 * Database service to connect to a PostgreSQL database and insert KPI data.
 * @author Thomas Pilz
 */
public class DatabaseService {

    public static final String URL = "#";
    private static DatabaseService instance;
    /**
     * SLF4J logger using Log4j 2
     */
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    public Connection dbCon;

    private DatabaseService() {

        logger.info("Created database service");
    }

    public static DatabaseService getInstance() {
        if (instance == null) instance = new DatabaseService();
        return instance;
    }


    private void connect(String url, String username, String password) throws ExecutionException, InterruptedException {
        var execSrv = Executors.newSingleThreadScheduledExecutor();
        final int maxReconnectionDelay = 180;
        long reconnectionDelay = 1;
        Callable<Boolean> connect = () ->{
            try {
                dbCon = DriverManager.getConnection(url, username, password);
                dbCon.setAutoCommit(false);
                logger.info("Successfully connected to database {}", url);
                return true;
            } catch (SQLException e) {
                logger.warn("Could not connect to database {} with username {}", url, username, e);
                return false;
            }
        };
        var isConnected = execSrv.schedule(connect, reconnectionDelay, TimeUnit.SECONDS);
        while(!isConnected.get()){
            var newReconnectionDelay = reconnectionDelay * 2;
            if(newReconnectionDelay > maxReconnectionDelay) reconnectionDelay = maxReconnectionDelay;
            else reconnectionDelay = newReconnectionDelay;
            logger.warn("Trying to reconnect in {} seconds", 2);
            isConnected = execSrv.schedule(connect, reconnectionDelay, TimeUnit.SECONDS);
        }
    }


    public void connect2db(String url, String username, String password){
        try {
            connect(url, username, password);
        } catch (ExecutionException e) {
            logger.warn("An exception occurred when trying to connect to database {}", url, e.getCause());
        } catch (InterruptedException e) {
            logger.error("Thread responsible for connecting to the database was interrupted!");
        }
    }

    public boolean isConnected(){
        try {
            if (dbCon.isClosed()) return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public void insertKpis(KpiMsg msg) {
        if (dbCon == null) {
            logger.error("You must open a database connection first!");
            return;
        }
        try {
            if (dbCon.isClosed()) {
                logger.error("No valid database connection open! Please open a database connection first!");
                return;
            }
        } catch (SQLException e) {
            logger.error("Cannot connect to database!", e);
            return;
        }
        long count = 0;
        var batchSize = 100;

        var kpis = msg.getKpis();
        try {
            var prepStmnt = dbCon.prepareStatement("INSERT INTO kpis (k_equipment,k_timestamp,k_name,k_unit,k_value) VALUES (?, ?, ?, ?, ?)");
            for (var kpi : kpis) {
                count++;

                prepStmnt.setString(1, msg.getClientId());
                prepStmnt.setTimestamp(2, new Timestamp(msg.getUnixTimestamp()));
                prepStmnt.setString(3, kpi.getName());
                prepStmnt.setInt(4, kpi.getUnitId());
                prepStmnt.setBigDecimal(5, kpi.getValue());
                prepStmnt.addBatch();
                if (count % batchSize == 0 || count == kpis.size()) {
                    prepStmnt.executeBatch();
                    dbCon.commit();
                }
            }
        } catch (BatchUpdateException e){
            logger.warn("Could not insert all KPIs!", e);
        } catch (SQLException e) {
            logger.warn("Could not insert values into database!", e);
            e.printStackTrace();
        }
    }
}
