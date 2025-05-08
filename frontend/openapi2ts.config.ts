/**
 * 这个文件用于配置 openapi2ts 插件, 用于把接口文档配置自动生成为 ts 类型文件, 参考官方文档 https://www.npmjs.com/package/@umijs/openapi
 */

// eslint-disable-next-line import/no-anonymous-default-export
export default [
  {
    requestLibPath: "import request from '@/libs/request'",
    schemaPath: "http://127.0.0.1:8000/work_question_bank_api/v3/api-docs",
    serversPath: "./src",
  },
  // 支持导入多个 openapi 配置, 不过需要注意的是, 由于官方文档没有写明如何配置, 需要先关闭文档的访问凭证才能获取到 json 数据
];
