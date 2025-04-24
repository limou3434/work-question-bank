/**
 * src/app/layout.tsx
 * 应用全局布局组件
 *
 * 无论访问哪一个页面都需要访问这个全局布局页面,
 * 可以把我们自己的全局布局文件包含到这里面来
 */
"use client";
import { AntdRegistry } from "@ant-design/nextjs-registry";
import BasicLayout from "@/layout/BasicLayout";
import React, { useCallback, useEffect } from "react";
import "./globals.css";
import { Provider, useDispatch } from "react-redux";
import store, { AppDispatch } from "@/stores";
import { getLoginUserUsingGet } from "@/api/userController";
import AccessLayout from "@/access/AccessLayout";
import { setLoginUser } from "@/stores/loginUser";

/**
 * 全局初始化组件
 * @param children
 * @constructor
 *
 * 是一个全局初始化的, 不显示的组件
 * 全局初始化逻辑都可以统一集中在这里书写
 */
const InitLayout: React.FC<
  Readonly<{
    children: React.ReactNode;
  }>
> = ({ children }) => {
  const dispath = useDispatch<AppDispatch>();
  /* 全局初始化的逻辑都可以写到这个块内部(注意: 逻辑就是还为执行的代码块) */
  // 维护用户登录状态
  const doInitLoginUser = useCallback(async () => {
    const res = await getLoginUserUsingGet(); // 尝试获取到当前登陆用户的信息
    if (res.data) {
      // @ts-ignore
      dispath(setLoginUser(res.data)); // 获取得到则说明用户短期内已经登陆了, 更新用户信息全局状态即可
    } else {
      // TODO: 处理出错操作, 直接跳转到登录页面要求用户登陆即可
    }
  }, []); // 这里使用可以缓存回调函数的 useCallback(), 只有在依赖项发生改变时才重新创建函数, 而不是重新挂载本组件时创建函数, 可以优化性能

  /* 上述逻辑在下面调用中只会调用一次(下面的 eslint 注释是为了取消警告, 请勿删除) */
  useEffect(() => {
    doInitLoginUser();
    // eslint-disable-next-line
  }, []);

  return children;
};

/**
 * 全局布局
 * @param children
 * @constructor
 */
export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="zh">
      <body>
        <AntdRegistry>
          {/* 使用状态管理组件就可以管理全局的状态 */}
          <Provider store={store}>
            <InitLayout>
              {/* 在这里使用了全局基本布局 */}
              <BasicLayout>
                <AccessLayout>{children}</AccessLayout>
              </BasicLayout>{" "}
            </InitLayout>
          </Provider>
        </AntdRegistry>
      </body>
    </html>
  );
}
