// src/app/user/register/page.tsx

/* 渲染 */
"use client"; // 注释本行则默认服务端渲染

/* 样式 */
import "./page.css";

/* 引入 */
import { LockOutlined, UserOutlined } from "@ant-design/icons";
import { LoginForm, ProFormText } from "@ant-design/pro-components";
import React from "react";
import Image from "next/image";
import Link from "next/link";
import { userRegisterUsingPost } from "@/api/userController";
import { message } from "antd";
import { ProForm } from "@ant-design/pro-form/lib";
import { useRouter } from "next/navigation";

/* 定义 */
const UserRegisterPage: React.FC = () => {
  // 表单实例
  const [form] = ProForm.useForm();

  // 重定向页面
  const router = useRouter();

  // 登入接口
  const doSubmit = async (values: API.UserLoginRequest): Promise<void> => {
    // TODO: 拦截前端部分不正确的请求, 减轻后端压力
    // 提交登入表单
    try {
      const res = await userRegisterUsingPost(values);
      if (res.data) {
        message.success("注册成功"); // 提示登入成功
        router.replace("/user/login"); // 跳转页面
        form.resetFields(); // 重置表单
      }
    } catch (e) {
      // @ts-ignore
      message.error("注册失败: " + e.message); // 提示登入失败
    }
  };

  return (
    <div id="userRegisterPage" className="max-width-content">
      {/* 登入组件 */}
      <LoginForm
        form={form}
        logo={
          <Image
            src="/assets/logo.png"
            alt="智能在线面试"
            height={45}
            width={45}
          />
        }
        title="智能在线面试 - 注册"
        subTitle="科教平台工作室面试考核系统"
        submitter={{
          searchConfig: {
            submitText: "注册",
          },
        }}
        onFinish={doSubmit}
      >
        {/* 用户名称 */}
        <ProFormText
          name="userAccount"
          fieldProps={{
            size: "large",
            prefix: <UserOutlined />,
          }}
          placeholder={"请输入帐号"}
          rules={[
            {
              required: true,
              message: "请输入帐号!",
            },
          ]}
        />
        {/* 用户密码 */}
        <ProFormText.Password
          name="userPassword"
          fieldProps={{
            size: "large",
            prefix: <LockOutlined />,
          }}
          placeholder={"请输入密码"}
          rules={[
            {
              required: true,
              message: "请输入密码！",
            },
          ]}
        />
        {/* 确认密码 */}
        <ProFormText.Password
          name="checkPassword"
          fieldProps={{
            size: "large",
            prefix: <LockOutlined />,
          }}
          placeholder={"请重复输入确认的密码"}
          rules={[
            {
              required: true,
              message: "请重复输入确认的密码！",
            },
          ]}
        />
        {/* 注册帐号 */}
        <div
          style={{
            marginBlockEnd: 24,
          }}
        >
          <div
            style={{
              float: "right",
              marginBlockEnd: 24,
            }}
          >
            已经有帐号？
            <Link href={"/user/login"}>登入帐号</Link>
          </div>
        </div>
      </LoginForm>
    </div>
  );
};

/* 导出 */
export default UserRegisterPage;
