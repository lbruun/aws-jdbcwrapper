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



/**
 * For MySQL with Connector/J driver <i>prior</i> to version 8.
 * 
 * @author lars
 */
public class AWSWrappedJDBCDriverMySQLpre8 extends AWSWrappedJDBCDriver {

    static {
        AWSWrappedJDBCDriver.register(new AWSWrappedJDBCDriverMySQLpre8());
    }
    
    @Override
    public String getWrappedDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public String getDbDisplayName() {
        return "MySQL";
    }
}
