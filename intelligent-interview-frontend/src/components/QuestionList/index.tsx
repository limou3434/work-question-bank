/**
 * src/components/QuestionList/index.tsx
 * 题目列表组件
 */

/* 渲染 */
"use client";

/* 样式 */
import "./index.css";

/* 导入 */
import { Card, List } from "antd";
import TagList from "@/components/TagList";
import Link from "next/link";

/* 属性 */
interface Props {
  questionList: API.QuestionVO[];
  cardTitle?: string;
}

/* 实现 */
const QuestionList = (props: Props) => {
  // 获取属性中题目列表
  const { questionList = [], cardTitle } = props;

  // 组织页面
  return (
    <div className="question-list">
      <Card title={cardTitle}>
        <List
          dataSource={questionList}
          renderItem={(item) => (
            // @ts-ignore
            <List.Item extra={<TagList tagList={item.tagList} />}>
              <List.Item.Meta
                // @ts-ignore
                title={<Link href={`/question/${item.id}`}>{item.title}</Link>}
              />
            </List.Item>
          )}
        />
      </Card>
    </div>
  );
};

export default QuestionList;
