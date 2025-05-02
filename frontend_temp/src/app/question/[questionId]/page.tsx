/**
 * src/app/question/[questionId]/page.tsx
 * 题目详情页面
 */

/* 渲染 */
"use server"; // 注释本行则默认服务端渲染

/* 样式 */
import "./page.css";

/* 引入 */
import { message } from "antd";
import { getQuestionVoByIdUsingGet } from "@/api/questionController";
import QuestionCard from "@/components/QuestionCard";

/* 定义 */
// @ts-ignore
export default async function QuestionPage({ params }) {
  const { questionId } = params;

  // 获取首题
  let question = undefined;
  try {
    const res = await getQuestionVoByIdUsingGet({
      id: questionId,
    });
    question = res.data;
  } catch (e) {
    // @ts-ignore
    console.error("获取题目详情失败: " + e.message);
  }

  // 如果题目不存在
  if (!question) {
    // TODO: 可以做错误边缘
    return <div>题目不存在...</div>;
  }

  return (
    <div id="firstQuestionPage" className="max-width-content">
      {/* @ts-ignore */}
      <QuestionCard question={question} />
    </div>
  );
}
