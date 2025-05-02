/*
 * src/app/questions/page.tsx
 * 题目大全页面
 */

/* 渲染 */
"use server"; // 注释本行则默认服务端渲染

/* 样式 */
import "./page.css";

/* 引入 */
import React from "react";
import {
  listQuestionVoByPage,
  searchQuestionVoByPage,
} from "@/api/questionController";
import { message } from "antd";
import QuestionTable from "@/components/QuestionTable";
import Title from "antd/es/typography/Title";

/* 定义 */

export default async function QuestionsPage({ searchParams }) {
  // 获取到 url 中的查询参数
  const { q: searchText } = searchParams;

  // 先在服务端提前获取 12 条题目(其他题目后续客户端再来慢慢渲染)
  let questionList = [];
  let total = 0;
  try {
    // const res = await listQuestionVoByPage({
    const res = await searchQuestionVoByPage({
      title: searchText,
      pageSize: 12,
      // sortField: "createTime",
      sortField: "_score", // 引入了 ES 后修改为这个
      sortOrder: "desc",
    });
    
    questionList = res.data.records ?? [];
    
    total = res.data.total ?? 0;
  } catch (e) {
    
    // message.error("获取题目列表失败: " + e.message);
  }

  // 组织页面
  return (
    <div id="questionsPage" className="max-width-content">
      <Title level={3}>题目大全</Title>
      <QuestionTable
        // @ts-ignore
        defaultQuestionList={questionList}
        defaultTotal={total}
        defaultSearchParams={{ title: searchText }}
      />
    </div>
  );
}
