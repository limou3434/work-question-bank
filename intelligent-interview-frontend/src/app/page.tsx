/**
 * src/app/page.tsx
 * 主页页面组件
 */

/* 渲染 */
"use server";

/* 样式 */
import "./page.css";
import Title from "antd/es/typography/Title";
import { Divider, Flex, message } from "antd";
import Link from "next/link";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import QuestionBankList from "@/components/QuestionBankList";
import QuestionList from "@/components/QuestionList";

/* 实现 */
export default async function HomePage() {
  // 存放最新的题库列表
  let questionBankList = [];
  try {
    const res = await listQuestionBankVoByPageUsingPost({
      pageSize: 12,
      sortField: "createTime",
      sortOrder: "desc",
    });
    // @ts-ignore
    questionBankList = res.data.records ?? [];
  } catch (e) {
    // @ts-ignore
    message.error("获取最新题库失败: " + e.message);
  }

  // 获取最新的题目列表
  let questionList = [];
  try {
    const res = await listQuestionVoByPageUsingPost({
      pageSize: 12,
      sortField: "createTime",
      sortOrder: "desc",
    });
    // @ts-ignore
    questionList = res.data.records ?? [];
  } catch (e) {
    // @ts-ignore
    message.error("获取最新题目失败: " + e.message);
  }

  // 组织页面
  return (
    <div id="homePage" className="max-width-content">
      {/* 网站公告 */}
      {/* 题库说明 */}
      <Flex justify="space-between" align="center">
        <Title level={3}>最新题库</Title>
        <Link href="/banks">查看更多</Link>
      </Flex>
      {/* TODO: 题库分类 */}
      <div></div>
      {/* 题库列表 */}
      <div>
        <QuestionBankList questionBankList={questionBankList} />
      </div>
      {/* 内容分割 */}
      <Divider />
      {/* 题目说明 */}
      <Flex justify="space-between" align="center">
        <Title level={3}>最新题目</Title>
        <Link href="/questions">查看更多</Link>
      </Flex>
      {/* TODO: 题目分类 */}
      <div></div>
      {/* 题目列表 */}
      <div>
        <QuestionList questionList={questionList} />
      </div>
      {/* TODO: 页面侧边栏(热题榜、排行榜、工具栏(分享、社群...)) */}
      <div></div>
    </div>
  );
}
