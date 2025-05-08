/**
 * src/app/page.tsx
 * 主页页面组件
 */

/* 渲染 */
"use server";

/* 样式 */
import "./page.css";

/* 导入 */
import Title from "antd/es/typography/Title";
import {Alert, Divider, Flex} from "antd";
import Link from "next/link";
import {listQuestionBankVoByPage} from "@/api/questionBankController";
import {listQuestionVoByPage} from "@/api/questionController";
import QuestionBankList from "@/components/QuestionBankList";
import QuestionList from "@/components/QuestionList";
import Marquee from "react-fast-marquee";

/* 实现 */
export default async function HomePage() {

    // 存放最新的题库列表
    let questionBankList = [];
    try {
        const res = await listQuestionBankVoByPage({
            pageSize: 12,
            sortField: "createTime",
            sortOrder: "desc",
        });
        questionBankList = res.data.records ?? [];
    } catch (e) {
    }

    // 获取最新的题目列表
    let questionList = [];
    try {
        const res = await listQuestionVoByPage({
            pageSize: 12,
            sortField: "createTime",
            sortOrder: "desc",
        });
        questionList = res.data.records ?? [];
    } catch (e) {
    }

    // 组织页面
    return (
        <div id="homePage" className="max-width-content">

            {/* 网站公告 */}
            <Alert
                type="success"
                banner
                message={
                    <Marquee pauseOnHover gradient={false}>
                        欢迎来到本项目
                    </Marquee>
                }
            />
            <Divider/>

            {/* 题库分类 */}
            <Flex vertical={false} justify="space-between" align="center">
                <Title level={3}>题库分类</Title>
                <Link href="/banks">查看更多</Link>
            </Flex>
            <div></div>
            <Divider/>

            {/* 题库列表 */}
            <Flex vertical={false} justify="space-between" align="center">
                <Title level={3}>最新题库</Title>
                <Link href="/banks">查看更多</Link>
            </Flex>
            <QuestionBankList questionBankList={questionBankList}/>
            <Divider/>

            {/* 最新题目 */}
            <Flex justify="space-between" align="center">
                <Title level={3}>最新题目</Title>
                <Link href="/questions">查看更多</Link>
            </Flex>
            <div></div>
            <QuestionList questionList={questionList}/>
            <Divider/>
        </div>
    );
}
