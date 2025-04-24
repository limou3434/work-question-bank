/**
 * src/stores/index.ts
 * 全局状态管理器
 *
 * (1)说明: npm 安装好 @reduxjs/toolkit react-redux, 配置好本文件后,
 * 就可以在项目中使用 <Provider store={这里传入下面定义好的全局状态管理器}></Provider>
 * (2)用法: 在 "<Provider store={store}>" 组件的内部(包括子组件)可以使用, 其中 Provider 组件通常是在 React Context API 中定义的
 * a. 获取状态
 * "const loginUser = useSelector((state: RootState) => state.loginUser);"
 * "login.{JSON字段} // 直接获取 JSON 字段"
 * b. 修改状态
 * "const dispatch = useDispatch<AppDispatch>();"
 * "dispatch(setLoginUser({JSON对象})) // 修改方法定义在定义状态的文件中"
 * (3)注意: 只允许在客户端渲染时使用全局状态, 因为这些全局状态维护在浏览器中
 */

import { configureStore } from "@reduxjs/toolkit";
import loginUser from "@/stores/loginUser";

// 创建全局状态管理器
const store = configureStore({
  reducer: {
    // 在这里存放所有需要被管理的状态
    loginUser,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export default store;
