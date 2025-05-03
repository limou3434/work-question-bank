"use client";

import "./page.css";
import { useEffect, useState } from "react";
import { getQuestionVoById } from "@/api/questionController";
import QuestionCard from "@/components/QuestionCard";

export default function QuestionPage({ params }) {
    const { questionId } = params;
    const [question, setQuestion] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        async function fetchQuestion() {
            try {
                const res = await getQuestionVoById({ id: questionId });
                setQuestion(res.data);
            } catch (e) {
                console.error("获取题目详情失败:", e);
                setError("题目加载失败，请检查是否已登录。");
            }
        }

        fetchQuestion();
    }, [questionId]);

    if (error) {
        return <div>{error}</div>;
    }

    if (!question) {
        return <div>加载中...</div>;
    }

    return (
        <div id="firstQuestionPage" className="max-width-content">
            <QuestionCard question={question} />
        </div>
    );
}


// /**
//  * src/app/question/[questionId]/page.tsx
//  * 题目详情页面
//  */
//
// /* 渲染 */
// "use client"; // 注释本行则默认服务端渲染
//
// /* 样式 */
// import "./page.css";
//
// /* 引入 */
// import {getQuestionVoById} from "@/api/questionController";
// import QuestionCard from "@/components/QuestionCard";
//
// /* 定义 */
//
// export default function QuestionPage({params}) {
//     const {questionId} = params;
//
//     // 获取首题
//     let question = undefined;
//
//     try {
//         const res = await getQuestionVoById({
//             id: questionId,
//         });
//
//         console.log(res.data);
//
//         question = res.data;
//     } catch (e) {
//         console.error("获取题目详情失败: " + e.message);
//     }
//
//     // 如果题目不存在
//     if (!question) {
//         // TODO: 可以做错误边缘
//         return <div>题目不存在...</div>;
//     }
//
//     return (
//         <div id="firstQuestionPage" className="max-width-content">
//             {/* @ts-ignore */}
//             <QuestionCard question={question}/>
//         </div>
//     );
// }
