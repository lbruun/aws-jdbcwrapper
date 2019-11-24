# AWS JDBC Wrapper Library

The **AWS JDBC Wrapper Library** is a non-intrusive JDBC wrapper
which supports AWS-specific authentication methods when connecting
to database. Currently IAM authentication and plain username/password 
authentication is supported.


## Features

* Enables AWS IAM authentication. (more to come)
* Non-instrusive - add it to an existing application without changing 
  any source code, only configuration.
* Provides wrappers to common JDBC drivers.



## Maven dependency

The recommended way to use the library is to consume it from Maven Central. 

The library comes in two editions:

#### Edition with all required dependencies (artifactId : `aws-jdbc-wrapper`)

This edition comes with the needed transitive dependencies. All you need to do
is to add as a dependency to your project:

``` xml
<dependency>
    <groupId>net.lbruun.aws</groupId>
    <artifactId>aws-jdbc-wrapper</artifactId>  
    <version> .. latest .. </version>
</dependency>
```

#### Edition Without any dependencies (artifactId : `aws-jdbc-wrapper-nodeps`)

This edition comes without any transitive dependencies. Add it to your project
as follows:

``` xml
<dependency>
    <groupId>net.lbruun.aws</groupId>
    <artifactId>aws-jdbc-wrapper-nodeps</artifactId>  
    <version> .. latest .. </version>
</dependency>
```

but in addition the library assumes that that your project provides:

``` xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-core</artifactId>  
    <version> ... </version>    <!-- AWS SDK v1  -->
</dependency>

<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk-rds</artifactId>
    <version> ... </version>    <!-- AWS SDK v1 -->
</dependency>
```

You would use this edition of the library if you are concerned about classpath 
clashes between the version of the AWS SDK which your own project requires
and the one which this library would use.


## Usage

The library is meant to be non-instrusive.

In your JDBC configuration, do the following:

1. Change your driver class name into one of the values from the 
  table in the 'Supported JDBC Drivers' section of this document, 
  depending on which database you use.
1. Change your JDBC URL so that you insert the text `awswrapper-` right
  after the first colon. For example the url `jdbc:postgresql://myhost:5432/mydb`
  would become: `jdbc:awswrapper-postgresql://myhost:5432/mydb`
1. Change the database username value so that you prefix it with
  `awswrapper:<authtype>:` where `<authtype>` is a value from the table
  below. For example the username value "mydbuser" may become "awswrapper:iam:mydbuser"
  indicating that you wish to use AWS IAM authentication.
1. Add properties to your JDBC connection setup as per the table below.

#### Authentication types

| Auth type     |   Description                             |
|---------------|-------------------------------------------|
| `iam`         | AWS IAM authentication. If you set a password in the JDBC configuration, it will be ignored. |
| `std`         | Standard username/password authentication. You *must* supply a password. |


#### JDBC Wrapper properties



| Property Name          | Mandatory? | Description                    |
|------------------------|------------|--------------------------------|
| `aws.jdbc.db.endpoint` | Yes        | Hostname of the DB         |
| `aws.jdbc.db.port`     | Yes        | IP port of the DB              |
| `aws.jdbc.db.username` | No        | DB username to connect with. Normally not required to be set with a separate key like this. The library will look first for a property named 'user' or 'username'. |
| `aws.jdbc.db.region`   | No        | AWS Region where the DB instance is running, e.g. `eu-central-1`. If executing on an EC2 instance in the same region as the database, then you can leave this out as the library can figure out this value on its own. |

Some of this is indeed duplicate information because some of this is likely 
already defined in the JDBC URL. However, the library is not yet clever
enough to derive this information from the JDBC URL .. it therefore needs it
explicitly.


## Supported JDBC Drivers


| JDBC Driver                    | Driver classname                                    |
|--------------------------------|-----------------------------------------------------|
| PostgreSQL                     | net.lbruun.aws.jdbc.AWSWrappedJDBCDriverPostgreSQL  |
| MySQL Connector/j v8 onwards   | net.lbruun.aws.jdbc.AWSWrappedJDBCDriverMySQL       |
| MySQL Connector/j pre v8       | net.lbruun.aws.jdbc.AWSWrappedJDBCDriverMySQLpre8   |



## Credits

The library is inspired by Amazon's 
[AWS Secrets Manager JDBC Library](https://github.com/aws/aws-secretsmanager-jdbc)

## License

This library is licensed under the Apache 2.0 License.
