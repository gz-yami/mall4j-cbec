FROM ibm-semeru-runtimes:open-17.0.10_7-jdk as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM ibm-semeru-runtimes:open-17.0.10_7-jdk
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "-Xmx1024m", "-Xss256k", "-Xshareclasses", "-Xtune:virtualized", "-Dspring.profiles.active=docker", "org.springframework.boot.loader.launch.JarLauncher"]
