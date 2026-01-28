# Requirements

Postgesql DB: https://www.postgresql.org/download/

JDK: 17

# JVM params

-DclientId="{clientId}"

-DclientSecret="{clientSecret}"

-DpostgresUrl="jdbc:postgresql://{postgresUrl}"

-DpostgresUsername="{postgresUsername}"

-DpostgresPassword="{postgresPassword}"

-DspringProfile="dev" / "prod"

-DmailUser="{mailUser}"

-DmailPassword="{mailPassword}"

-DenableSwagger=true/false

-DurlPostgresLocal="localhost:5432/{databaseLocalName}"

-DusernamePostgresLocal="{userPostgresLocal}"

-DpasswordPostgresLocal="{passwordPostgresLocal}"

-DPagarMeSecretKey="{pagarMeSecretKey}"

-DsmsKey="{smsKey}"

-DsmsEnabled=true/false

# Postgres Steps

## 1 - Install and configure pgAdmin 4 (postgresql) - Remember the username and password that you create during the first configuration

## 2 - Create the database with name "radar-ecom"
![image](https://github.com/ezequiel-primaz/radar-ecom-ms/assets/82176711/f1a7d2ae-c822-4236-aacb-f4bfddfeb9e6)

## 3 - Use this values in VM params if the spring profile is "local"
![image](https://github.com/ezequiel-primaz/radar-ecom-ms/assets/82176711/584f8f24-b095-4464-b65a-8a75f9f97f63)

# Intellij Steps

## 1 - Open the project on intellij
![image](https://github.com/ezequiel-primaz/radar-ecom-ms/assets/68088581/d65686aa-9bb6-4cda-a161-c98f98eeca1d)


## 2 - Configure the JVM params
![image](https://github.com/ezequiel-primaz/radar-ecom-ms/assets/68088581/9198e64f-9c25-49a0-98c5-6f8b3cbf1044)

## 3 - Build and Run the appliation.
