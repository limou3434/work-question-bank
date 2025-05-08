/**
 * 应用全局布局组件, 无论访问哪一个页面都需要访问这个全局布局页面, 可以把我们自己的全局布局文件包含到这里面来
 */
"use client";

import { AntdRegistry } from "@ant-design/nextjs-registry";
import BasicLayout from "@/layout/BasicLayout";
import React, { useCallback, useEffect } from "react";
import "./globals.css";
import { Provider, useDispatch } from "react-redux";
import store, { AppDispatch } from "@/stores";
import { getLoginUser } from "@/api/userController";
import AccessLayout from "@/access/AccessLayout";
import { setLoginUser } from "@/stores/loginUser";

const InitLayout: React.FC<
  Readonly<{
    children: React.ReactNode;
  }>
> = ({ children }) => {
  const dispath = useDispatch<AppDispatch>();

  // 初始化测试
  const doInitTest = useCallback(() => {
    console.debug("初始化测试");
  }, []);

  // 初始化用户登录状态
  const doInitLoginUser = useCallback(async () => {
    console.debug("初始化用户登录状态");
    const res = await getLoginUser(); // 尝试获取到当前登陆用户的信息
    if (res.data) {
      console.debug("初始化页面时获取到了用户登陆信息", res.data);
      dispath(setLoginUser(res.data as API.LoginUserVO)); // 获取得到则说明用户短期内已经登陆了, 更新用户信息全局状态即可
    } else {
      // TODO: 处理出错操作, 直接跳转到登录页面要求用户登陆即可
    }
  }, [dispath]); // 这里使用可以缓存回调函数的 useCallback(), 只有在依赖项发生改变时才重新创建函数, 而不是重新挂载本组件时创建函数, 可以优化性能

  // 调用一次所有的初始化逻辑
  useEffect(() => {
    console.debug("调用一次所有的初始化逻辑");
    doInitTest();
    doInitLoginUser();
  }, []);

  return children;
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="zh">
      <body>
       {/* 引入组件框架 */}
        <AntdRegistry>
          {/* 引入状态管理 */}
          <Provider store={store}>
            {/* 引入初始逻辑  */}
            <InitLayout>
              {/* 引入基本布局 */}
              <BasicLayout>
                {/* 引入页面权限 */}
                <AccessLayout>
                  {children}
                </AccessLayout>
              </BasicLayout>
            </InitLayout>
          </Provider>
        </AntdRegistry>
      </body>
    </html>
  );
}
