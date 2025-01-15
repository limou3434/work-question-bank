/**
 * src/stores/loginUser.ts
 * 用户状态
 *
 * 这里定义了一个全局状态, 用户存储登录用户信息, 并且对外开放修改接口
 */

import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { DEFAULT_USER } from "@/constants/user";

// 定义登录用户信息全局状态
export const loginUserSlice = createSlice({
  name: "loginUser",
  initialState: DEFAULT_USER, // 设置用户状态默认值
  reducers: {
    // 定义修改状态的方法
    setLoginUser: (state, action: PayloadAction<API.LoginUserVO>) => {
      return {
        ...action.payload, // 一般只返回新的对象
      };
    },
  },
});

export const { setLoginUser } = loginUserSlice.actions; // 修改登陆状态
export default loginUserSlice.reducer;
