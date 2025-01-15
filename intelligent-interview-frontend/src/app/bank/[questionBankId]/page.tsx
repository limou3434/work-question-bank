/**
 * src/app/bank/[questionBankId]/page.tsx
 * 题库详情页面
 */

/* 渲染 */
"use server"; // 注释本行则默认服务端渲染

/* 样式 */
import "./page.css";
import { getQuestionBankVoByIdUsingGet } from "@/api/questionBankController";
import { Avatar, Button, Card, message } from "antd";
import Meta from "antd/es/card/Meta";
import Paragraph from "antd/es/typography/Paragraph";
import Title from "antd/es/typography/Title";
import QuestionList from "@/components/QuestionList";

/* 定义 */
// @ts-ignore
export default async function BankPage({ params }) {
  // 获取一个题库的信息
  const { questionBankId } = params;
  let bank = undefined;

  try {
    const res = await getQuestionBankVoByIdUsingGet({
      id: questionBankId,
      needQueryQuestionList: true,
      pageSize: 200, // TODO: 暂时全量获取
    });
    bank = res.data;
  } catch (e) {
    // @ts-ignore
    message.error("获取题库详情失败: " + e.message);
  }

  // 如果题库不存在
  if (!bank) {
    // TODO: 可以做错误边缘
    return <div>题库不存在...</div>;
  }

  // 获取题库中第一题的信息
  let firstQuestionId;
  // @ts-ignore
  if (bank.questionPage?.records && bank.questionPage?.records.length > 0) {
    // @ts-ignore
    firstQuestionId = bank.questionPage?.records[0].id;
  }

  return (
    <div id="bankPage" className="max-width-content">
      <Title level={3}>题库详情</Title>
      <Card>
        <Meta
          // @ts-ignore
          avatar={<Avatar src={bank.picture} size={72} />}
          title={
            <Title level={4} style={{ marginBottom: 0, marginLeft: 10 }}>
              {/* @ts-ignore */}
              {bank.title}
            </Title>
          }
          description={
            <>
              <Paragraph type="secondary" style={{ marginLeft: 10 }}>
                {/* @ts-ignore */}
                {bank.description}
              </Paragraph>
              <Button
                type="primary"
                shape="round"
                style={{ marginLeft: 10 }}
                href={`/bank/${questionBankId}/firstQuestion/${firstQuestionId}`}
                target="_blank"
                disabled={!firstQuestionId} // 没有第一道题目就需要禁用
              >
                开始刷题
              </Button>
              <div style={{ marginBottom: 0, marginLeft: 10 }} />
            </>
          }
        />
      </Card>
      <div style={{ marginBottom: 16 }} />
      {/* @ts-ignore */}
      <QuestionList
        // @ts-ignore
        questionList={bank.questionPage?.records ?? []}
        // @ts-ignore
        cardTitle={`题目列表（${bank.questionPage?.total || 0}）`}
      />
    </div>
  );
}
