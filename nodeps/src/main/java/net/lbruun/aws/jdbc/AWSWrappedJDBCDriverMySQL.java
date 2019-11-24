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
 * For MySQL with Connector/J driver from version 8 onwards.
 * 
 * @author lars
 */
public class AWSWrappedJDBCDriverMySQL extends AWSWrappedJDBCDriver {

    static {
        AWSWrappedJDBCDriver.register(new AWSWrappedJDBCDriverMySQL());
    }
    
    @Override
    public String getWrappedDriverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public String getDbDisplayName() {
        return "MySQL";
    }
    
}
