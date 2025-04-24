# @author <a href="https://github.com/xiaogithubooo">limou3434</a>
# @from <a href="https://datalearnhub.com">大数据工作室</a>
FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

# Run the web service on container startup. # NOTE: 下面修改为 jar 包名即可
CMD ["java","-jar","/app/target/intelligent-interview-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]