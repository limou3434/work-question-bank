/**
 * src/components/QuestionTable/index.tsx
 * 支持服务端渲染的题目表格组件
 */

/* 渲染 */
"use client"; // 注释本行则默认服务端渲染

/* 样式 */
import "./index.css";

/* 引入 */
import React, { useRef } from "react";
import { ActionType, ProColumns, ProTable } from "@ant-design/pro-components";
import {
  listQuestionVoByPageUsingPost,
  searchQuestionVoByPageUsingPost,
} from "@/api/questionController";
import TagList from "@/components/TagList";
import { TablePaginationConfig } from "antd";
import Link from "next/link";

/* 属性 */
interface Props {
  defaultQuestionList?: API.QuestionVO[];
  defaultTotal?: number;
  defaultSearchParams?: API.QuestionQueryRequest;
}

/* 定义 */
const QuestionsTable: React.FC = (props: Props) => {
  // 引入属性
  const { defaultQuestionList, defaultTotal, defaultSearchParams = {} } = props;

  // 以下两个默认值用于支持服务端渲染(本组件可以被强行转化为"受控组件")
  const [questionList, setQuestionList] = React.useState<API.QuestionVO[]>(
    defaultQuestionList || [],
  );
  const [total, setTotal] = React.useState<number>(defaultTotal || 0);

  // 判断是否首次加载
  const [init, setInit] = React.useState(true);

  // 引用表格
  const actionRef = useRef<ActionType>();

  // 列数据配置(内含修改操作和删除操作)
  const columns: ProColumns<API.QuestionVO>[] = [
    {
      title: "搜索",
      dataIndex: "searchText",
      valueType: "text",
      hideInTable: true, // 隐藏在表格内, 因为不需要显示到页面中
    },
    {
      title: "标题",
      dataIndex: "title",
      valueType: "text",
      hideInSearch: true, // 隐藏在表单内, 因为用前面的搜索替代查询
      render: (_, record) => {
        return <Link href={`/question/${record.id}`}>{record.title}</Link>;
      },
    },
    {
      title: "标签",
      dataIndex: "tagList",
      valueType: "select",
      fieldProps: {
        mode: "tags",
      },
      // 重写: 将朴素的 JSON 标签转化为 TagList 组件
      render: (_, record) => {
        return <TagList tagList={record.tagList} />;
      },
    },
    {
      title: "创建时间",
      sorter: true,
      dataIndex: "createTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: "更新时间",
      sorter: true,
      dataIndex: "updateTime",
      valueType: "dateTime",
      hideInSearch: true,
      hideInForm: true,
    },
  ];

  // 组织组件
  return (
    <div className="question-table">
      {/* 表格组件 */}
      <ProTable<API.QuestionVO>
        rowKey="id"
        actionRef={actionRef}
        search={{
          labelWidth: "auto",
        }}
        form={{
          initialValues: defaultSearchParams,
        }}
        dataSource={questionList}
        pagination={
          {
            pageSize: 12,
            showSizeChanger: false,
            total,
          } as TablePaginationConfig
        }
        // @ts-ignore
        request={async (params, sort, filter) => {
          // 判断是否首次加载
          if (init) {
            setInit(false);
            // 并且也传递了默认值
            if (defaultQuestionList && defaultTotal) {
              return; // 直接跳出无需客户端渲染
            }
          }

          // TODO: 通过 request() 的 sort 参数得到排序条件, 让后端支持多列排序
          const sortField = Object.keys(sort)?.[0] || "createTime";
          const sortOrder = sort?.[sortField] || "descend";
          // 获取表格内的题目数据
          // @ts-ignore
          // const { data, code } = await listQuestionVoByPageUsingPost({
          const { data, code } = await searchQuestionVoByPageUsingPost({
            ...params,
            sortOrder,
            // sortField: "_score", // TODO: 可以添加了 ES 后的份数排序, 不过给用户选项最好
            ...filter,
          } as API.QuestionQueryRequest);

          // 预期要被渲染的最终数据
          // @ts-ignore
          const newData = data?.records || [];
          // @ts-ignore
          const newTotal = data?.total || 0;

          // 更新状态
          setQuestionList(newData);
          setTotal(newTotal);

          return {
            success: code === 0,
            data: newData,
            total: newTotal,
          };
        }}
        columns={columns}
      />
    </div>
  );
};

export default QuestionsTable;

// TODO: 分词插件可以参考 IK 分词插件的官方文档：https://github.com/infinilabs/analysis-ik/tree/v7.17.18?tab=readme-ov-file#dictionary-configuration
// TODO: 使用 ES 查询时, 关联获取题目的动态数据(点赞数、评论数也考虑进去的情况), 其实就是先查 ES，再从 DB 查询其他的数据, 然后拼起来再返回(ES 本来就不能存放动态数据)
// TODO: 使用 ES 查询时, 支持 ES 降级为 DB 查询, 提高可用性
// TODO: 当在项目具有分布式 ES 时, 需要使用分布式锁注解来避免重复执行定时任务
