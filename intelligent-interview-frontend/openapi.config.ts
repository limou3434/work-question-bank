const { generateService } = require("@umijs/openapi");

generateService({
  requestLibPath: "import request from '@/libs/transmission'", // 请求库
  schemaPath: "http://localhost:8002/api/v2/api-docs", // 后端接口规范(Open API 以前称为 Swagger)
  serversPath: "./src", // 生成代码对应的父目录
});
