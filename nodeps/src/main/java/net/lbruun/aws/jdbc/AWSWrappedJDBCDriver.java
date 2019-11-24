/*
 * Copyright 2019 Lars Bruun-Hansen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.lbruun.aws.jdbc;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import net.lbruun.aws.jdbc.auth.IamAuthentication;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 *
 * @author lars
 */
public abstract class AWSWrappedJDBCDriver implements Driver {

    public final static String KEY_DB_ENDPOINT = "aws.jdbc.db.endpoint";
    public final static String KEY_DB_PORT = "aws.jdbc.db.port";
    public final static String KEY_DB_AUTHTYPE = "aws.jdbc.db.authtype";
    public final static String KEY_DB_USERNAME = "aws.jdbc.db.username";
    public final static String KEY_DB_REGION = "aws.jdbc.db.region";
    
    public final static String JDBC_URL_MIDSTR = "awswrapper-";
    private final static String JDBC_URL_PREFIX = "jdbc:" + JDBC_URL_MIDSTR;
    private final AtomicReference<Driver> wrappedDriver = new AtomicReference<>();
    
            

    public AWSWrappedJDBCDriver() {
        AWSWrappedJDBCDriver.register(this);
    }

    
    /** 
     * Fully qualified class name for the JDBC driver we are wrapping.
     * For example: {@code org.postgresql.Driver}
     * 
     * @return 
     */
    public abstract String getWrappedDriverClassName();
    
    public abstract String getDbDisplayName();
    
    
    /**
     * Gets the underlying Driver.
     * 
     * Note, that this must be lazily loaded since we don't know in which order
     * the JVM will load JDBC drivers. If we tried finding this value in the
     * constructor then we'll get the wrong result in the situations where this
     * class happens to be loaded before the class it wraps.
     * @return 
     */
    private Driver getWrappedDriver() throws SQLException {
        Driver drv = wrappedDriver.get();
        if (drv == null) {
            Driver newDrv = AWSWrappedJDBCDriver.lookupWrappedDriver(getWrappedDriverClassName());
            if (newDrv == null) {
                throw new RuntimeException(new ClassNotFoundException(
                        "Cannot find JDBC Driver for " + getDbDisplayName() 
                                + ". Make sure the driver is on the classpath."));
            }
            if (!wrappedDriver.compareAndSet(null, newDrv)) {
                return wrappedDriver.get();
            }
            return newDrv;
        }
        return drv;
    }

    private String getUnwrappedUrlStr(String wrappedUrlStr) {
        if (wrappedUrlStr == null) {
            return wrappedUrlStr;
        }
        if (wrappedUrlStr.length() <= JDBC_URL_PREFIX.length()) {
            return wrappedUrlStr;
        }
        if (wrappedUrlStr.startsWith(JDBC_URL_PREFIX)) {
           return  "jdbc:" + wrappedUrlStr.substring(JDBC_URL_PREFIX.length());
        }
        return wrappedUrlStr;
    }
    
    @Override
    public final boolean acceptsURL(String url) throws SQLException {
        return getWrappedDriver().acceptsURL(getUnwrappedUrlStr(url));
    }

    @Override
    public final Connection connect(String url, Properties info) throws SQLException {
        String realUrl = getUnwrappedUrlStr(url);
        
        String dbEndpoint = info.getProperty(KEY_DB_ENDPOINT);
        String dbPortStr = info.getProperty(KEY_DB_PORT);
        int dbPort = -1;
        try {
            dbPort = Integer.valueOf(dbPortStr);
        } catch (NumberFormatException ex) {
            throw new SQLException("The value \"" + dbPortStr + "\" is not a valid IP port number");
        }
        String dbUsername = info.getProperty("username");
        if (dbUsername == null) {
            dbUsername = info.getProperty("user");
        }
        if (dbUsername == null) {
            dbUsername = info.getProperty(KEY_DB_USERNAME);
        }
        Region currentRegion = Regions.getCurrentRegion();
        String dbRegion = null;
        if (currentRegion != null) {
            dbRegion = currentRegion.getName();
        }
        dbRegion = info.getProperty(KEY_DB_REGION, dbRegion);

        if (dbRegion == null) {
            throw new SQLException("Can't determine AWS region.");
        }
        
        if ((!info.containsKey("password")) 
                || (info.getProperty("password").isEmpty())
                || (dbUsername.startsWith("awswrapper:iam:"))) {
            String authToken = IamAuthentication.generateAuthToken(dbRegion, dbEndpoint, dbPort, dbUsername);
            info.setProperty("password", authToken);
        }
        
        return getWrappedDriver().connect(getUnwrappedUrlStr(url), info);
    }
    
    
    private static synchronized Driver lookupWrappedDriver(String className) throws SQLException {
        Enumeration<Driver> availableDrivers = DriverManager.getDrivers();
        while (availableDrivers.hasMoreElements()) {
            Driver driver = availableDrivers.nextElement();
            if (driver.getClass().getName().equals(className)) {
                return driver;
            }
        }
        
        try {
            Class<?> driverClass = Class.forName(className);
            if (Driver.class.isAssignableFrom(driverClass)) {
                return (Driver) driverClass.newInstance();
            } else {
                throw new SQLException("Class \"" + className + "\" was found and can be loaded but it does not implement " + Driver.class.getName());
            }
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new SQLException("Cannot instantiate class " + className, ex);
        }
        return null;
    }

    /**
     * Called when the driver is deregistered to cleanup resources.
     */
    protected void shutdown() {
    }
    

    /**
     * Registers a driver along with the <code>DriverAction</code>
     * implementation.
     *
     * @param driver The driver to register.
     *
     * @throws RuntimeException If the driver could not be registered.
     */
    protected static void register(AWSWrappedJDBCDriver driver) {
        try {
            DriverManager.registerDriver(driver, () -> {driver.shutdown();});
        } catch (SQLException e) {
            throw new RuntimeException("Driver could not be registered.", e);
        }
    }




    
    //  Pure pass-through methods
    
    @Override
    public final int getMajorVersion() {
        try {
            return getWrappedDriver().getMajorVersion();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public final int getMinorVersion() {
        try {
            return getWrappedDriver().getMinorVersion();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public final Logger getParentLogger() throws SQLFeatureNotSupportedException {
        try {
            return getWrappedDriver().getParentLogger();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return getWrappedDriver().getPropertyInfo(url, info);
    }

    @Override
    public boolean jdbcCompliant() {
        try {
            return getWrappedDriver().jdbcCompliant();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    

}
