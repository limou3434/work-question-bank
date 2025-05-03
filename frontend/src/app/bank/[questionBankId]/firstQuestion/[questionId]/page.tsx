"use client";

import "./page.css";
import { getQuestionBankVoById } from "@/api/questionBankController";
import { getQuestionVoById } from "@/api/questionController";
import Title from "antd/es/typography/Title";
import { Flex, Menu, Spin } from "antd";
import Sider from "antd/es/layout/Sider";
import { Content } from "antd/es/layout/layout";
import QuestionCard from "@/components/QuestionCard";
import Link from "next/link";
import { useEffect, useState } from "react";

export default function FirstQuestionPage({ params }) {
    const { questionBankId, questionId } = params;

    const [bank, setBank] = useState(null);
    const [question, setQuestion] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function fetchData() {
            try {
                const bankRes = await getQuestionBankVoById({
                    id: questionBankId,
                    needQueryQuestionList: true,
                    pageSize: 200,
                });
                setBank(bankRes.data);
            } catch (e) {
                console.error("获取题库失败:", e.message);
            }

            try {
                const questionRes = await getQuestionVoById({
                    id: questionId,
                });
                setQuestion(questionRes.data);
            } catch (e) {
                console.error("获取题目失败:", e.message);
            }

            setLoading(false);
        }

        fetchData();
    }, [questionBankId, questionId]);

    if (loading) return <Spin tip="加载中..." />;
    if (!bank) return <div>题库不存在...</div>;
    if (!question) return <div>首题不存在...</div>;

    const questionMenuItemList = Array.isArray(bank?.questionPage?.records)
        ? bank.questionPage.records.map((q) => ({
            key: q.id,
            label: (
                <Link href={`/bank/${questionBankId}/firstQuestion/${q.id}`}>
                    {q.title}
                </Link>
            ),
        }))
        : [];

    return (
        <div id="firstQuestionPage" className="max-width-content">
            <Flex gap={16}>
                <Sider width={240} theme="light" style={{ padding: "24px 0" }}>
                    <Title level={4} style={{ padding: "0 20px" }}>{bank.title}</Title>
                    <Menu items={questionMenuItemList} selectedKeys={[question.id]} />
                </Sider>
                <Content>
                    <QuestionCard question={question} />
                </Content>
            </Flex>
        </div>
    );
}
