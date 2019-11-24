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

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;

/**
 * AWS IAM authentication for RDS database
 * @author lars
 */
public class IamAuthentication {
    private static final DefaultAWSCredentialsProviderChain CREDS = new DefaultAWSCredentialsProviderChain();
    private static final String AWS_ACCESS_KEY = CREDS.getCredentials().getAWSAccessKeyId();
    private static final String AWS_SECRET_KEY = CREDS.getCredentials().getAWSSecretKey();  
    
    
    /**
     * This method generates the IAM Auth Token.
     * An example IAM Auth Token would look like follows:
     * <pre>
     * {@code btusi123.cmz7kenwo2ye.rds.cn-north-1.amazonaws.com.cn:3306/?
     *    Action=connect
     *   &DBUser=iamtestuser
     *   &X-Amz-Algorithm=AWS4-HMAC-SHA256
     *   &X-Amz-Date=20171003T010726Z
     *   &X-Amz-SignedHeaders=host
     *   &X-Amz-Expires=899
     *   &X-Amz-Credential=AKIAPFXHGVDI5RNFO4AQ%2F20171003%2Fcn-north-1%2Frds-db%2Faws4_request
     *   &X-Amz-Signature=f9f45ef96c1f770cdad11a53e33ffa4c3730bc03fdee820cfdf1322eed15483b }
     * </pre>
     * (without line breaks)
     * @param regionName region where the db is located
     * @param endpoint database endpoint / hostname
     * @param port database port
     * @param username database username
     * 
     * @return
     */
    public static String generateAuthToken(String regionName, String endpoint, int port, String username) {

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);

        RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
                .credentials(new AWSStaticCredentialsProvider(awsCredentials)).region(regionName).build();
        
        return generator.getAuthToken(GetIamAuthTokenRequest.builder()
                .hostname(endpoint)
                .port(port)
                .userName(username)
                .build());
    }

}
