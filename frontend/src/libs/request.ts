/**
 * 配置请求实例
 */
import axios from "axios";

// 请求地址
function getBaseUrl(model: string) {
    let BASE_URL = "";
    if (model === "develop") {
        console.log("当前要求部署模式", model);
        BASE_URL = "http://127.0.0.1:8000/work_question_bank_api/";
    }
    else if (model === "release") {
        console.log("当前要求部署模式", model);
        BASE_URL = "http://127.0.0.1:8005/work_question_bank_api/";
    }
    return BASE_URL;
}

// 创建实例
const myAxios = axios.create({
    baseURL: getBaseUrl("release"), // 请求后端(IP+PORT)
    timeout: 10000, // 响应时间(10s)
    withCredentials: true, // 凭证携带(开启)
});

// 创建请求拦截器
myAxios.interceptors.request.use(
    // 请求执行前执行
    function (config) {
        // console.debug("截取到请求成功", config);
        return config;
    },

    // 处理请求错误
    function (error) {
        // console.debug("截取到请求失败", error);
        return Promise.reject(error);
    },
);

// 创建响应拦截器
myAxios.interceptors.response.use(
    // 响应成功(为 2xx 响应触发)
    function (response) {
        // 解析响应数据
        const {data} = response;
        console.debug("截取到响应成功", data);
        handleLoginError(data, response.request.responseURL);
        return data;
    },

    // 响应失败(非 2xx 响应触发)
    function (error) {
        // 处理响应错误
        console.debug("截取到响应失败", error);
        return Promise.reject(error);
    },
);

// 处理登录错误
const handleLoginError = (data: any, preUrl: any) => {
    // 特殊处理
    if (data.code === 40100) {
        // 不是获取用户信息接口, 或者不是登录页面, 则重定向跳转到登录页面
        if (
            !preUrl.includes("user/get/login") && // 判断之前的请求中不包含获取登录接口, 这个接口本来就是为了获取状态的, 无需跳转
            !window.location.pathname.includes("/user/login") // 判断当前页面不是处于登录页面, 这个页面本来就是为了登录的, 无需跳转
        ) {
            window.location.href = `/user/login?redirect=${window.location.href}`; // 这一句代码的作用是将用户重定向到登录页面, 并在登录页面的 URL 中添加一个 redirect 参数, 方便用户登陆请求成功后切回原来的界面, 做到无缝体验， redirect 保存了当前页面的 URL
        }
    }
}

export default myAxios;
