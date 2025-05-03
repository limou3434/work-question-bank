# 基础镜像
FROM node:22.11

# 编译代码(请在本地直接使用 ./mvnw clean package -DskipTests 进行编译避免容器过大)
COPY ./.next/standalone ./standalone

# 运行端口
EXPOSE 3000

# 启动命令
CMD ["node", "standalone/server.js"]
