[![Build Status](https://img.shields.io/endpoint?url=https%3A%2F%2Fstatusbadge-jx.apps.serv.run%2Fentando%2Fentando-plugin-jpmail)](https://github.com/entando/devops-results/tree/logs/jenkins-x/logs/entando/entando-plugin-jpmail/master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpmail&metric=alert_status)](https://sonarcloud.io/dashboard?id=entando_entando-plugin-jpmail)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpmail&metric=coverage)](https://entando.github.io/devops-results/entando-plugin-jpmail/master/jacoco/index.html)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpmail&metric=vulnerabilities)](https://entando.github.io/devops-results/entando-plugin-jpmail/master/dependency-check-report.html)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpmail&metric=code_smells)](https://sonarcloud.io/dashboard?id=entando_entando-plugin-jpmail)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpmail&metric=security_rating)](https://sonarcloud.io/dashboard?id=entando_entando-plugin-jpmail)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpmail&metric=sqale_index)](https://sonarcloud.io/dashboard?id=entando_entando-plugin-jpmail)

entando-plugin-jpmail
**Mail Plugin**

**Code**: ```jpmail```

**Description**

Mail Plugin is a component that let Entando users configure SMTP server and create new senders to send email messages.

**Installation**

In order to install the Mail Plugin, you must insert the following dependency in the pom.xml file of your project:

```
<dependency>
    <groupId>org.entando.entando.plugins</groupId>
    <artifactId>entando-plugin-jpmail</artifactId>
    <version>${entando.version}</version>
    <type>war</type>
</dependency>
````

**Configuration**

From Entando’s back office, you have to:

 1. create new **Sender**: you have to create a new Sender with a _Code_ and an _Email_. You can have a List of Senders; the Sender will be choosed on the base of its Code. 
 2. configure the **SMTP server**: you have to active the SMTP server, and then to set _Host_ (mandatory), _Port_, _Security Certification_, _Timeout_ parameter.
 
Please leave _Username_ and _Password_ blank if the SMTP does not require authentication.
 


# Developing against local versions of upstream projects (e.g. admin-console,  entando-engine).

Full instructions on how to develop against local versions of upstream projects are available in the
[entando-parent-bom](https://github.com/entando/entando-core-bom) project.     