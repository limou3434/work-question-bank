/**
 * src/app/banks/page.tsx
 * 题库大全页面
 */

/* 渲染方式 */
"use server";

/* 引入样式 */
import "./page.css";
import { message } from "antd";
import { listQuestionBankVoByPage } from "@/api/questionBankController";
import Title from "antd/es/typography/Title";
import QuestionBankList from "@/components/QuestionBankList";

/* 引入组件 */
// import ...

/* 定义属性 */
// interface ...

/* 组织页面 */
export default async function BanksPage() {
  let questionBankList = [];
  try {
    const res = await listQuestionBankVoByPage({
      pageSize: 200, // 注意后端被设置为最多获取 200 条(题库数量不多直接全量获取即可)
      sortField: "createTime",
      sortOrder: "desc",
    });
    
    questionBankList = res.data.records ?? [];
  } catch (e) {
    
    // message.error("获取题库列表失败: " + e.message);
  }

  return (
    <div id="banksPage" className="max-width-content">
      <Title level={3}>题库大全</Title>
      <QuestionBankList questionBankList={questionBankList} />
    </div>
  );
}
