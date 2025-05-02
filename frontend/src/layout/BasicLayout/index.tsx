/**
 * src/layout/BasicLayout/index.tsx
 * 基本布局组件(包含导航栏组件)
 *
 * 本文件定义了一个全局布局组件, 包含一个完整可用的导航栏
 * 其他组件可以成为本组件的子组件, 复用导航栏的界面
 */

/* 渲染 */
"use client"; // 注释本行则默认服务端渲染

/* 样式 */
import "./index.css";

/* 引入 */
import {GithubFilled, LogoutOutlined, QuestionCircleFilled, UserOutlined,} from "@ant-design/icons";
import {ProLayout} from "@ant-design/pro-components";
import {Dropdown} from "antd";
import React from "react";
import Image from "next/image";
import {usePathname, useRouter} from "next/navigation";
import Link from "next/link";
import Footer from "../../components/Footer";
import SearchInput from "./components/SearchInput";
import {menus} from "../../../config/menus";
import {useDispatch, useSelector} from "react-redux";
import {AppDispatch, RootState} from "@/stores";
import getAccessibleMenus from "@/access/menuAccess";
import {userLogout} from "@/api/userController";
import {setLoginUser} from "@/stores/loginUser";
import {DEFAULT_USER} from "@/constants/user";

/* 属性 */
interface Props {
    children: React.ReactNode; // 代表一个 React 节点
}

/* 实现 */
export default function BasicLayout({children}: Props) {
    const pathname = usePathname(); // 可以返回当前浏览器地址栏中的路径名(比直接 useStatus() 好一些, 避免水合错误)
    const loginUser = useSelector((state: RootState) => state.loginUser); // useSelector 接受一个选择器函数作为参数, 选择器函数接收整个 Redux 存储的状态, 并返回想要的部分, 这里返回的就是当前用户的状态(注意如果用户没有登陆就会使用默认状态)
    const dispatch = useDispatch<AppDispatch>(); // 登入状态
    const router = useRouter(); // 重定向页面

    // 用户登出
    const doUserLogout = async () => {
        try {
            await userLogout();
            dispatch(setLoginUser(DEFAULT_USER)); // 保存用户登入状态
            router.push("/user/login"); // 跳转页面
        } catch (e) {
        }
    };

    console.debug("这里是基本布局组件, 检查是否存储了登陆状态", loginUser)

    return (
        <div
            id="basic-layout"
            style={{
                height: "100vh",
                overflow: "auto",
            }}
        >
            {/* 下面的 ProLayout 就是核心组件, 是 Antd Pro 导航栏布局 */}
            <ProLayout
                title="智能面试平台"
                logo={
                    <Image
                        src="/assets/logo.png"
                        height={32}
                        width={32}
                        alt="智能面试 - limou3434"
                    />
                }
                // 设置导航栏布局方式
                layout="top"
                // 设置当前用户页面路径
                location={{
                    pathname, // 传入当前客户端浏览器页面以支持选中高亮
                }}
                // 设置用户信息道具
                avatarProps={{
                    // 这里直接获取全局状态中的数据, 如果用户没有登陆会使用默认状态的数据, 实在不行就会显示 logo 作为用户头像, 并且用户名为 "无默认状态"
                    src: loginUser.userAvatar || "/assets/logo.png",
                    size: "small",
                    title: loginUser.userName || "无默认状态",
                    render: (props, dom) => {
                        // 如果没有登陆就不用显示登出控件
                        if (!loginUser.id) {
                            return (
                                <div
                                    onClick={() => {
                                        router.push("/user/login");
                                    }}
                                >
                                    {dom}
                                </div>
                            );
                        }
                        return (
                            <Dropdown
                                menu={{
                                    items: [
                                        {
                                            key: "userCenter",
                                            icon: <UserOutlined/>,
                                            label: "个人中心",
                                        },
                                        {
                                            key: "logout",
                                            icon: <LogoutOutlined/>,
                                            label: "退出登录",
                                        },
                                    ],
                                    onClick: async (event: { key: React.Key }) => {
                                        const key = event.key;
                                        if (key === "logout") {
                                            await doUserLogout();
                                        } else if (key === "userCenter") {
                                            router.push("/user/center");
                                        }
                                    },
                                }}
                            >
                                {dom}
                            </Dropdown>
                        );
                    },
                }}
                // 设置页面活动区域
                actionsRender={(props) => {
                    if (props.isMobile) return []; // 移动端访问就会自动隐藏这个组件

                    if (pathname === "/questions") {
                        return [
                            <a
                                key="QuestionCircleFilled"
                                href="https://github.com/xiaogithubooo" // TODO: 待修改
                                target="_blank"
                            >
                                <QuestionCircleFilled/>
                            </a>,
                            <a
                                key="GithubFilled"
                                href="https://github.com/xiaogithubooo"
                                target="_blank"
                            >
                                <GithubFilled/>
                            </a>,
                        ];
                    }

                    return [
                        // 返回数组的话, 必须每个元素都有一个 key 属性
                        <SearchInput key="SearchInput"/>,
                        <a
                            key="QuestionCircleFilled"
                            href="https://github.com/xiaogithubooo" // TODO: 待修改
                            target="_blank"
                        >
                            <QuestionCircleFilled/>
                        </a>,
                        <a
                            key="GithubFilled"
                            href="https://github.com/xiaogithubooo"
                            target="_blank"
                        >
                            <GithubFilled/>
                        </a>,
                    ];
                }}
                // 设置页面头部
                headerTitleRender={(logo, title, _) => {
                    return (
                        <a>
                            {logo}
                            {title}
                        </a>
                    );
                }}
                // 设置页面尾部
                footerRender={() => {
                    return <Footer/>;
                }}
                // 设置页面头部菜单项
                menuDataRender={() => {
                    return getAccessibleMenus(loginUser, menus); // 这个函数会自动根据页面权限的设置来过滤菜单项, 以控制对应用户看到对应的菜单项
                }}
                // 设置渲染菜单项响应
                menuItemRender={(item, dom) => (
                    <Link
                        href={item.path || "/"} // 设置路径, 如果没有路径就设置为根路径
                        target={item.target}
                    >
                        {/* 这里定义为跳转到页面 */}
                        {dom}
                    </Link>
                )}
                // 设置导航栏点击事件(打印日志)
                // onMenuHeaderClick={(e) => console.log(e)}
            >
                {/* 填充外部 React 节点 */}
                {children}
            </ProLayout>
        </div>
    );
}
