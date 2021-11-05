# use multi-stage build to keep containenr as small as possible
#--------------
# 1. Stage:
# - Build Java Runtime using jlink
# - Download dumb-init (= handles PID 1 process responsibilities) to have it ready for next stage
# - Download and install required dependency not available via Maven Central (or other repos)
# - Build single .jar including
#--------------
# Build from eclipse temurin as this has all the necessary tools to build a small image for Java applications
FROM eclipse-temurin:16 as builder
ENV MAVEN_VER=3.8.3 \
    MAVEN_HOME=/opt/maven

# Maven coordinates for .jar to be built
ENV BUILD_GROUP_ID=de.othr \
    BUILD_ART_ID=DatabaseConnector \
    BUILD_VERSION=1.0-SNAPSHOT
ENV BUILD_JAR_NAME=${BUILD_ART_ID}-${BUILD_VERSION}-jar-with-dependencies.jar


# Create a custom Java runtime which only has stuff that is required to run the application
# java.base,java.xml,java.sql,java.prefs,java.desktop,javax.api
RUN $JAVA_HOME/bin/jlink \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime
RUN mkdir /project

# Install utils to get other required software
RUN apt update && \
    apt install -y  \
    ca-certificates \
    wget
# download and install Apache Maven
RUN wget https://dlcdn.apache.org/maven/maven-3/${MAVEN_VER}/binaries/apache-maven-${MAVEN_VER}-bin.tar.gz && \
    tar xvf apache-maven-${MAVEN_VER}-bin.tar.gz && \
    mv apache-maven-${MAVEN_VER} ${MAVEN_HOME} && \
    # download and install dumb-init using binaries (https://github.com/Yelp/dumb-init)
    wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.5/dumb-init_1.2.5_x86_64 && \
    chmod +x /usr/local/bin/dumb-init

# copy over all the project files which are not exluded by .dockerignore
COPY . /project
WORKDIR /project
# Build the project with maven in batch-mode (= non-interactive)
RUN ${MAVEN_HOME}/bin/mvn -B clean package -DskipTests

#--------------
# 2. Stage
# - Copy built Java runtime from builder
# - Copy dumb-init binaries from builder
# - Copy over built project from builder
# - Create special Java user with no login shell to run the application
# - Run the application and let dumb-init handle all PID 1 related tasks
#--------------
FROM debian:stretch-slim
# Set JAVA_HOME variable and put it on PATH
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
# Name of .jar that
ENV BUILD_GROUP_ID=de.othr \
    BUILD_ART_ID=DatabaseConnector \
    BUILD_VERSION=1.0-SNAPSHOT
ENV BUILD_JAR_NAME=${BUILD_ART_ID}-${BUILD_VERSION}-jar-with-dependencies.jar
# copy over custom JRE from first stage
COPY --from=builder /javaruntime $JAVA_HOME
# add dumb-init which will take care of all the PID 1 specific stuff
#RUN ["apk", "add", "dumb-init"]
COPY --from=builder /usr/local/bin/dumb-init /usr/local/bin/dumb-init
RUN ["mkdir", "/app"]
# create special javauser to run the application to prevent running the application as root (security!)
RUN addgroup --system javauser && \
    # no login shell; username: "javauser"; group: "javauser"
    useradd -s /bin/false -g javauser javauser
# copy over built .jar
COPY --from=builder /project/target/${BUILD_JAR_NAME} /app/java-application.jar
WORKDIR /app
# make newly created user owner of this directory
RUN chown -R javauser:javauser /app
# set user to javauser
USER javauser
# run dumb-init and the java application
CMD ["/usr/local/bin/dumb-init", "java", "-jar", "java-application.jar"]