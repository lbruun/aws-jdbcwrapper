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
package net.lbruun.aws.jdbc.auth;

import java.util.Locale;

/**
 * Database user authentication type
 * @author lars
 */
public enum DbAuthenticationType {
    
    /**
     * AWS IAM authentication.
     * 
     *<p>
     * This form of authentication is supported by:
     * <ul>
     *    <li>Amazon RDS PostgreSQL</li>
     *    <li>Amazon RDS MySQL</li>
     *    <li>Amazon Aurora - PostgreSQL</li>
     *    <li>Amazon Aurora - MySQL</li>
     *    <li>Amazon Redshift</li>
     * </ul>
     */
    IAM,
    
    /**
     * Credentials stored in AWS Secrets Manager.
     * 
     *<p>
     * This form of authentication is supported by any database. The username
     * and password is looked up in AWS Secrets Manager and when retrieved
     * it is passed to the database as in the {@link #STD} scenario.
     */
    SECRETSMANAGER,
    
    /**
     * Using traditional plain-text username/password credential.
     * 
     *<p>
     * This form of authentication is supported by any database. 
     */
    STD;
    
    
    public DbAuthenticationType getFromString(String name) {
        return DbAuthenticationType.valueOf(name.toUpperCase(Locale.US));
    }
}
