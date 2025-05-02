/**
 * 题库列表组件
 */

/* 渲染 */
"use client";

/* 样式 */
import "./index.css";

/* 导入 */
import {Avatar, Card, List, Typography} from "antd";
import Link from "next/link";

/* 属性 */
interface Props {
    questionBankList?: string[];
}

/* 实现 */
const QuestionBankList = (props: Props) => {
    // 获取属性中题库列表
    const {questionBankList} = props;

    // 单张卡片渲染
    const questionBankView = (questionBank: API.QuestionBankVO) => {
        return (
            <div>
                <Card>
                    <Link href={`/bank/${questionBank.id}`}>
                        <Card.Meta
                            avatar={<Avatar src={questionBank.picture}/>}
                            title={questionBank.title}
                            description={
                                // 使用高级排版组件缩略为一行
                                <Typography.Paragraph
                                    type="secondary"
                                    ellipsis={{rows: 1}}
                                    style={{marginBottom: 0}}
                                >
                                    {questionBank.description}
                                </Typography.Paragraph>
                            }
                        />
                    </Link>
                </Card>
            </div>
        );
    };

    // 组织页面
    return (
        <div className="question-bank-list">
            <List
                grid={{
                    gutter: 16,
                    column: 4,
                    xs: 1,
                    sm: 2,
                    md: 3,
                    lg: 3,
                }}
                dataSource={questionBankList}
                renderItem={(item) => <List.Item>{questionBankView(item as unknown as API.QuestionBankVO)}</List.Item>}
            />
        </div>
    );
};

export default QuestionBankList;
