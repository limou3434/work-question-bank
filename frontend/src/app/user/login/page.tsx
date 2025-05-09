// src/app/user/login/page.tsx

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
import { userLoginUsingPost } from "@/api/userController";
import { message } from "antd";
import { useDispatch } from "react-redux";
import { AppDispatch } from "@/stores";
import { setLoginUser } from "@/stores/loginUser";
import { ProForm } from "@ant-design/pro-form/lib";
import { useRouter } from "next/navigation";

/* 定义 */
const UserLoginPage: React.FC = () => {
  // 登入状态
  const dispatch = useDispatch<AppDispatch>();

  // 表单实例
  const [form] = ProForm.useForm();

  // 重定向页面
  const router = useRouter();

  // 登入接口
  const doSubmit = async (values: API.UserLoginRequest): Promise<void> => {
    // 提交登入表单
    try {
      const res = await userLoginUsingPost(values);
      if (res.data) {
        message.success("登入成功"); // 提示登入成功
        // @ts-ignore
        dispatch(setLoginUser(res.data)); // 保存用户登入状态
        router.replace("/"); // 跳转页面
        form.resetFields(); // 重置表单
      }
    } catch (e) {}
  };

  return (
    <div id="userLoginPage" className="max-width-content">
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
        title="智能在线面试 - 登入"
        subTitle="科教平台工作室面试考核系统"
        onFinish={doSubmit}
      >
        {/* 用户名称 */}
        <ProFormText
          name="userAccount"
          fieldProps={{
            size: "large",
            prefix: <UserOutlined />,
          }}
          placeholder={"请输入正确的帐号"}
          rules={[
            {
              required: true,
              message: "请输入正确的帐号!",
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
          placeholder={"请输入正确的密码"}
          rules={[
            {
              required: true,
              message: "请输入正确的密码！",
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
            还没有帐号？
            <Link href={"/user/register"}>注册帐号</Link>
          </div>
        </div>
      </LoginForm>
    </div>
  );
};

/* 导出 */
export default UserLoginPage;
