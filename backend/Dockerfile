# Copyright (c) 上海如静知华信息科技有限公司 | https://www.zhuatech.cn/
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2,sharing=locked sh -c 'for attempt in 1 2 3; do mvn -B -q -Dmaven.wagon.http.retryHandler.count=5 dependency:go-offline && exit 0; sleep $((attempt * 5)); done; exit 1'
COPY src src
RUN --mount=type=cache,target=/root/.m2,sharing=locked sh -c 'for attempt in 1 2 3; do mvn -B -q -Dmaven.wagon.http.retryHandler.count=5 clean package && exit 0; sleep $((attempt * 5)); done; exit 1'
FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 zhuatech
WORKDIR /app
COPY --from=build /workspace/target/zhuatech-aieval.jar app.jar
USER zhuatech
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
