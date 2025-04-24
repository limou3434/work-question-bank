/**
 * src/components/QuestionCard/index.tsx
 * 题库列表组件
 */

/* 渲染 */
"use client";

/* 导入 */
import { Card } from "antd";
import Title from "antd/es/typography/Title";
import TagList from "@/components/TagList";
import MdViewer from "@/components/MdViewer";
import useAddUserSignInRecord from "@/hooks/useAddUserSignInRecord";

/* 属性 */
interface Props {
  question: API.QuestionBankVO;
}

/* 实现 */
const QuestionCard = (props: Props) => {
  const { question } = props;

  useAddUserSignInRecord(); // 由于题目详情卡片需要签到的页面中的公共组件, 因此在这里触发钩子函数即可

  // TODO: 不过这样做的话会导致每次刷题都会发送请求, 因此可以做一些优化, 保存到 LocalStorage 缓存中

  return (
    <div className="question-card">
      <Card>
        <Title level={1} style={{ fontSize: 32 }}>
          {question.title}
        </Title>
        {/* @ts-ignore */}
        <TagList tagList={question.tagList} />
        <div style={{ marginBottom: 16 }} />
        {/* @ts-ignore */}
        <MdViewer value={question.content} />
      </Card>
      <div style={{ marginBottom: 16 }} />
      <Card title="参考答案">
        {/* @ts-ignore */}
        <MdViewer value={question.answer} />
      </Card>
    </div>
  );
};

export default QuestionCard;
