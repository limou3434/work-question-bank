/** @type {import('next').NextConfig} */
const nextConfig = {
  output: "standalone", // 使用 standalone 模式(独立运行模式)部署, 这样上传到服务器的时候可以不用上传 node_modules 目录
};
export default nextConfig;
