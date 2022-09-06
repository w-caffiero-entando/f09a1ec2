<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>
    
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <!-- PARENT -->
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <parent>
        <groupId>org.entando</groupId>
        <artifactId>entando-core-parent</artifactId>
        <relativePath>../pom.xml</relativePath>
        <version>7.2.0-SNAPSHOT</version>
    </parent>
    
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <!-- BASE PROJECT SETUP -->
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <groupId>org.entando.entando</groupId>
    <artifactId>entando-parent-pom-tester</artifactId>
    <packaging>jar</packaging>
    <version>7.2.0-SNAPSHOT</version>
    <name>Entando Parent Pom Tester</name>
    <description>Test project for the Entando Core</description>
    <url>http://www.entando.com/</url>
    <licenses>
        <license>
            <name>GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007</name>
            <url>https://www.gnu.org/licenses/lgpl-3.0.txt</url>
            <distribution>repo</distribution>
        </license>
    </licenses>
    <organization>
        <name>Entando Inc.</name>
        <url>https://www.entando.com/</url>
    </organization>
    
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <!-- REPOSITORIES -->
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <repositories>
        <repository>
            <id>nexus-jx</id>
            <url>https://nexus-jx.apps.serv.run/repository/maven-releases/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
    
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <!-- PROPERTIES -->
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <!-- BUILD -->
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <build>
        <finalName>${project.artifactId}-${project.version}</finalName>
        
        <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
        <!-- build/EXTENSIONS -->
        <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
        <extensions>
            <extension>
                <groupId>org.apache.maven.wagon</groupId>
                <artifactId>wagon-webdav-jackrabbit</artifactId>
                <version>1.0</version>
            </extension>
        </extensions>
        
        <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
        <!-- build/RESOURCES -->
        <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
        <resources>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                    <include>**/*.xsd</include>
                </includes>
            </resource>
            <resource>
                <directory>src/test/java</directory>
                <includes>
                    <include>**/*.sql</include>
                    <include>**/*.json</include>
                </includes>
            </resource>
            <resource>
                <directory>src/main/tld</directory>
                <targetPath>META-INF</targetPath>
                <includes>
                    <include>*.tld</include>
                </includes>
            </resource>
        </resources>
        
        <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
        <!-- build/PLUGINS -->
        <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
        <plugins>
            <!-- MAVEN RELEASE PLUGIN -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-release-plugin</artifactId>
                <version>2.3.2</version>
                <configuration>
                    <tagNameFormat>v@{project.version}</tagNameFormat>
                    <pushChanges>false</pushChanges>
                    <localCheckout>true</localCheckout>
                </configuration>
            </plugin>
            
            <!-- MAVEN JAR PLUGIN -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
            </plugin>
            
        </plugins>
    </build>
    
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <!--  DEPENDENCIES -->
    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
    <dependencies>
    </dependencies>
</project>
