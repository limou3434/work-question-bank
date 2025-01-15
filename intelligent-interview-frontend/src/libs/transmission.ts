// libs/transmission.ts
import axios from "axios";

// 创建 Axios 实例
const myAxios = axios.create({
  baseURL: "http://localhost:8002", // 请求后端(IP+PORT)
  timeout: 10000, // 响应时间(10s)
  withCredentials: true, // 凭证携带(开启)
});

// 创建请求拦截器
myAxios.interceptors.request.use(
  // 请求执行前执行
  function (config) {
    // console.log(config);
    return config;
  },
  // 处理请求错误
  function (error) {
    // console.log(error);
    return Promise.reject(error);
  },
);

// 创建响应拦截器
myAxios.interceptors.response.use(
  // 响应成功(2xx 响应触发)
  function (response) {
    // 解析响应数据
    const { data } = response;
    // 尚未登录
    if (data.code === 40100) {
      // 不是获取用户信息接口, 或者不是登录页面, 则重定向跳转到登录页面)
      if (
        !response.request.responseURL.includes("user/get/login") && // 判断之前的请求中不包含获取登录接口
        !window.location.pathname.includes("/user/login") // 判断当前页面不是处于登录页面
      ) {
        window.location.href = `/user/login?redirect=${window.location.href}`; // 这一句代码的作用是将用户重定向到登录页面, 并在登录页面的 URL 中添加一个 redirect 参数, 方便用户登陆请求成功后切回原来的界面, 做到无缝体验， redirect 保存了当前页面的 URL
      }
    }
    // 其他错误
    else if (data.code !== 0) {
      throw new Error(data.message ?? "服务器错误");
    }
    return data;
  },

  // 响应失败(非 2xx 响应触发)
  function (error) {
    // 处理响应错误
    return Promise.reject(error);
  },
);

export default myAxios;
