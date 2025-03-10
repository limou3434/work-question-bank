/**
 * src/app/bank/[questionBankId]/question/[questionId]/page.tsx
 * 开始刷题页面
 */

/* 渲染 */
"use server"; // 注释本行则默认服务端渲染

/* 样式 */
import "./page.css";
import { getQuestionBankVoByIdUsingGet } from "@/api/questionBankController";
import Title from "antd/es/typography/Title";
import { Flex, Menu, message } from "antd";
import { getQuestionVoByIdUsingGet } from "@/api/questionController";
import Sider from "antd/es/layout/Sider";
import { Content } from "antd/es/layout/layout";
import QuestionCard from "@/components/QuestionCard";
import Link from "next/link";

/* 定义 */
// @ts-ignore
export default async function FirstQuestionPage({ params }) {
  const { questionBankId, questionId } = params;

  // 获取题库
  let bank = undefined;
  try {
    const res = await getQuestionBankVoByIdUsingGet({
      id: questionBankId,
      needQueryQuestionList: true,
      pageSize: 200,
    });
    bank = res.data;
  } catch (e) {
    // @ts-ignore
    console.error("获取题库失败: " + e.message);
  }

  // 如果题库不存在
  if (!bank) {
    // TODO: 可以做错误边缘
    return <div>题库不存在...</div>;
  }

  // 获取首题
  let question = undefined;
  try {
    const res = await getQuestionVoByIdUsingGet({
      id: questionId,
    });
    question = res.data;
  } catch (e) {
    // @ts-ignore
    console.error("获取题目失败: " + e.message);
  }

  // 如果首题不存在
  if (!question) {
    // TODO: 可以做错误边缘
    return <div>首题不存在...</div>;
  }

  // 定义题目菜单列表
  // @ts-ignore
  const questionMenuItemList = (bank.questionPage?.records || []).map((q) => {
    return {
      key: q.id,
      label: (
        <Link href={`/bank/${questionBankId}/firstQuestion/${q.id}`}>
          {q.title}
        </Link>
      ),
    };
  });

  return (
    <div id="firstQuestionPage" className="max-width-content">
      <Flex gap={16}>
        {/* 侧边栏目 */}
        <Sider width={240} theme="light" style={{ padding: "24px 0" }}>
          {/* 题库标题 */}
          <Title level={4} style={{ padding: "0 20px" }}>
            {/* @ts-ignore */}
            {bank.title}
          </Title>
          {/* 菜单列表 */}
          {/* @ts-ignore */}
          <Menu items={questionMenuItemList} selectedKeys={[question.id]} />
        </Sider>
        {/* 内容区域 */}
        <Content>
          {/* @ts-ignore */}
          <QuestionCard question={question} />
        </Content>
      </Flex>
      {/* TODO: 支持默认隐藏答案 */}
      {/* TODO: 支持未登陆无法查看答案 */}
      {/* TODO: 支持下一题 */}
    </div>
  );
}
