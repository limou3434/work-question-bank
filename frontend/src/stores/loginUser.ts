/**
 * 登录用户信息状态
 */

import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {DEFAULT_USER} from "@/constants/user";

// 定义状态
export const loginUserSlice = createSlice({
    name: "loginUser", // 设置状态名称
    initialState: DEFAULT_USER, // 设置状态缺省
    reducers: { // 设置状态方法
        setLoginUser: (state, action: PayloadAction<API.LoginUserVO>) => {
            return {
                ...action.payload, // 一般只返回新的对象
            };
        },
    },
});

// 导出状态
export default loginUserSlice.reducer;

// 暴露方法
export const {setLoginUser} = loginUserSlice.actions;

