VERSION 0.8
ARG clean=true
FROM openjdk:17-jdk-alpine
RUN apk add --update --no-cache maven coreutils
ENV MAVEN_HOME /usr/share/maven
ENV PATH $MAVEN_HOME/bin:$PATH
ENV FIX_MVN_STDOUT "tail -f"
RUN mvn -B --version | $FIX_MVN_STDOUT
WORKDIR /app

build:
    FROM +install
    
    RUN mvn -B -T4 package $MVN_OPT | $FIX_MVN_STDOUT

    SAVE ARTIFACT target
    SAVE ARTIFACT webapp/target

test:
    FROM +build
    RUN mvn -B -T4 test \
        org.jacoco:jacoco-maven-plugin:prepare-agent \
        org.jacoco:jacoco-maven-plugin:report \ 
        -Ppre-deployment-verification \
        -Ppost-deployment-verification \
        $MVN_OPT | $FIX_MVN_STDOUT

docker-prepare:
    FROM +build
    RUN mkdir -p target/generated-resources/licenses && touch target/generated-resources/licenses.xml
    COPY ./Dockerfile.tomcat .
    SAVE ARTIFACT Dockerfile.tomcat
    SAVE ARTIFACT target
    SAVE ARTIFACT webapp

build-image:
    ARG IMAGE_NAME
    FROM DOCKERFILE -f +docker-prepare/Dockerfile.tomcat +docker-prepare/.
    RUN echo "XXXXXXXXXX $IMAGE_NAME"
    SAVE IMAGE $IMAGE_NAME

publish-image:
    ARG DOCKERHUB_ORG
    ARG IMAGE_NAME
    FROM +build-image
    SAVE IMAGE --push $DOCKERHUB_ORG/$IMAGE_NAME

install:
    CACHE ./.m2

    # SELECTIVE IMPORT
    COPY pom.xml .
    LET LIST=$(cat pom.xml | grep "<module>" | sed -E 's/<[/]?module>//g')
    FOR dir IN $LIST
        COPY --dir $dir ./
    END

    # INSTALL
    ENV MVN_OPT=-Dmaven.repo.local=./.m2 \
        -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn \
        -Dorg.slf4j.simpleLogger.showDateTime=true \
        -Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss

    IF $clean:
        RUN mvn -B clean install $MVN_OPT | $FIX_MVN_STDOUT
    ELSE
        RUN mvn -B install $MVN_OPT | $FIX_MVN_STDOUT
    END

    # SELECTIVE SAVE
    SAVE ARTIFACT target
    FOR dir IN $LIST
        SAVE ARTIFACT "$dir/target"
    END
