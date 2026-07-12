# ========================================
# Spring Boot 后端 Dockerfile
# 构建前需先执行：mvn clean package -DskipTests
# ========================================

FROM openjdk:17

# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 拷贝 jar 包（Maven 构建产物在 target/ 目录下）
COPY target/*.jar /app.jar

# 入口
ENTRYPOINT ["java", "-jar", "/app.jar"]
