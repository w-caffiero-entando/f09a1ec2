[![Build Status](https://img.shields.io/endpoint?url=https%3A%2F%2Fstatusbadge-jx.apps.serv.run%2Fentando%2Fentando-plugin-jpcontentscheduler)](https://github.com/entando/devops-results/tree/logs/jenkins-x/logs/entando/entando-plugin-jpcontentscheduler/master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpcontentscheduler&metric=alert_status)](https://sonarcloud.io/dashboard?id=entando_entando-plugin-jpcontentscheduler)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpcontentscheduler&metric=coverage)](https://entando.github.io/devops-results/entando-plugin-jpcontentscheduler/master/jacoco/index.html)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpcontentscheduler&metric=vulnerabilities)](https://entando.github.io/devops-results/entando-plugin-jpcontentscheduler/master/dependency-check-report.html)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpcontentscheduler&metric=code_smells)](https://sonarcloud.io/dashboard?id=entando_entando-plugin-jpcontentscheduler)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpcontentscheduler&metric=security_rating)](https://sonarcloud.io/dashboard?id=entando_entando-plugin-jpcontentscheduler)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=entando_entando-plugin-jpcontentscheduler&metric=sqale_index)](https://sonarcloud.io/dashboard?id=entando_entando-plugin-jpcontentscheduler)

entando-plugin-jpcontentscheduler
============

## Content scheduler

**Code**: ```jpcontentscheduler```

## Description

This plugin is used to manage contents temporal windows based on the content's dates.

It is possible to set up rules that:

 - will move an expired content to a public archive
 - will unpublish the expired content
 - will automatically publish the content
 - will send email notifications to a list of users

### Installation

Open the pom.xml of your project: locate the tag toward the end of the file, after the tag; if the dependencies tag does not exist just create a new one just after the closure of the build tag.

Add the following snippet inside the dependencies:

```  
<dependency>
    <groupId>org.entando.entando.plugins</groupId>
    <artifactId>entando-plugin-jpcontentscheduler</artifactId>
    <version>${entando.version}</version>
    <type>war</type>
</dependency>
``` 

### General Settings:

- `Active`
 boolean used to enable or disable the plugin

- `SiteCode`
 configuration parameter related to the `clustering` plugin 

- `Global category`
 mandatory category code that each content will be assigned to when suspended or moved to a generic archive not belonging to a specific category 

- `Content replacement`
 content identifier that will replace the archived/unpublished content

- `Content model replacement`
 model identifier that will replace the archived/unpublished content

### Email Settings:

These settings are used to manage how email notifications are being sent


### Users Settings:

Used to define bindings between users and content types.
It is possible to specify in a very granular way which user should receive emails for each type of content.

### Content Types Settings:

- `start date attribute`
 this mandatory parameter defines the mapping of the field used for automatic publishing.

- `end date attribute` 
 this mandatory parameter defines the mapping of the field used to determine whether or not the content has expired.

- `Content replace id`
 this optional parameter overrides the `General Settings` `Content replacement` setting.
 
- `Content replace model id`
 this optional parameter overrides the `General Settings` `Content model replacement` setting.

- `Suspend`
 mandatory boolean to define the behaviour of an expired content: `true` unpublishes the content while `false` archives it. 
 