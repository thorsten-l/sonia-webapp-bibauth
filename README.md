# sonia-webapp-bibauth

## Create sample configuration file

`./bibauth.jar --write-sample-configuration`

### Output

```text
plain security token:  '<plain random token>'
```

### sample `configuration.xml` file

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<configuration version="1.0">
    <description>bibauth configuration file</description>
    <liveConfiguration>false</liveConfiguration>
    <ldapConfig>
        <hostname>localhost</hostname>
        <port>3636</port>
        <sslEnabled>true</sslEnabled>
        <credentials>
            <bindDN>cn=SuperDuperAdmin</bindDN>
            <password>IS3hcQfr5eST9NbRfGzxuQ==</password>
        </credentials>
    </ldapConfig>
    <ldapBarcodeAttributeName>barcode</ldapBarcodeAttributeName>
    <clientAuthorizationToken>+ug+LqV38jVJc9hBnfFdkU34b0kLw4Lb0YMN6xe3xNtI9Irl/9FVFWAiitjdbnQs</clientAuthorizationToken>
    <organizations>
        <organization name="orgA">
            <baseDn>ou=people,o=org-a.de,dc=text,de=de</baseDn>
            <searchFilter>(&amp;(objectClass=person)(uid={0}))</searchFilter>
            <searchScope>ONE</searchScope>
        </organization>
        <organization name="orgB">
            <baseDn>o=org-b.de,dc=text,de=de</baseDn>
            <searchFilter>(&amp;(objectClass=eduperson)(cn={0}))</searchFilter>
            <searchScope>SUB</searchScope>
        </organization>
    </organizations>
</configuration>
```

## Show options

`./bibauth.jar -h`

```text
Usage: ./bibauth.jar [options]

 --check (-c) WERT            : Check given config file
 --encrypt (-e) WERT          : Encrypt given password
 --generate (-g) N            : Generate random password (Vorgabe: 0)
 --help (-h)                  : Displays this help (Vorgabe: true)
 --version (-v)               : Display programm version (Vorgabe: false)
 --write-sample-configuration : Write sample configuration file (Vorgabe: false)
```

## Run bibauth service

`./bibauth.jar`

## Very Important for a production environment!!!
  - Put an nginx or Apache HTTPd proxy in front of the bibauth service to provide the HTTPS protocol.

  - Configure your Firewall to restrict the client source ip-addess to the destination service ip-address
