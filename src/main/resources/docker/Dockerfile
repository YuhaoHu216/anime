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

# JVM 内存参数由 docker-compose 的 JAVA_OPTS 环境变量传入
ENV JAVA_OPTS=""
# 入口（exec 确保 java 进程 PID=1，能正确接收 SIGTERM）
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app.jar"]
