/**
 * 全局状态管理器
 * npm 安装好 @reduxjs/toolkit react-redux, 配置好本文件后, 在项目中使用 <Provider store={这里传入下面定义好的全局状态管理器}></Provider>
 * (1) 修改状态
 * "const dispatch = useDispatch<AppDispatch>();"
 * "dispatch(setLoginUser({JSON对象})); // 修改方法定义在定义状态的文件中"
 * (2) 获取状态
 * "const loginUser = useSelector((state: RootState) => state.loginUser);"
 * "login.{JSON字段}; // 直接获取 JSON 字段"
 * 只允许在客户端渲染时使用全局状态, 因为这些全局状态维护在浏览器中
 */

import {configureStore} from "@reduxjs/toolkit";
import loginUser from "@/stores/loginUser";

// 定义全局状态管理器
const store = configureStore({
    reducer: { // 存放所有需被管理状态
        loginUser,
    },
});


export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

// 导出全局状态管理器
export default store;

