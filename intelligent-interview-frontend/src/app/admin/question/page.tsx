/**
 * src/app/admin/question/page.tsx
 * 题目管理页面组件
 */

/* 渲染 */
"use client"; // 注释本行则默认服务端渲染

/* 样式 */
import "./page.css";

/* 引入 */
import CreateModal from "@/app/admin/question/components/CreateModal";
import UpdateModal from "@/app/admin/question/components/UpdateModal";
import { PlusOutlined } from "@ant-design/icons";
import type { ActionType, ProColumns } from "@ant-design/pro-components";
import { PageContainer, ProTable } from "@ant-design/pro-components";
import { Button, message, Space, Typography } from "antd";
import React, { useRef, useState } from "react";
import {
  deleteQuestionUsingPost,
  listQuestionByPageUsingPost,
} from "@/api/questionController";
import TagList from "@/components/TagList";
import MdEditor from "@/components/MdEditor";
import UpdateBankModal from "@/app/admin/question/components/UpdateBankModel";

/* 定义 */
// 题目管理页面
const QuestionAdminPage: React.FC = () => {
  // TODO: 使用实时搜索, 并且进行防抖操作

  // 是否显示新建窗口
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);

  // 是否显示更新窗口
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);

  // 是否显示所属题库更新窗口
  const [updateBankModalVisible, setUpdateBankModalVisible] =
    useState<boolean>(false);

  // 引用表格
  const actionRef = useRef<ActionType>();

  // 当前题目的数据
  const [currentRow, setCurrentRow] = useState<API.Question>();

  // 行删除逻辑
  const handleDelete = async (row: API.Question) => {
    const hide = message.loading("正在删除");
    if (!row) return true;
    try {
      await deleteQuestionUsingPost({
        id: row.id as any,
      });
      hide();
      message.success("删除成功");
      actionRef?.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error("删除失败: " + error.message);
      return false;
    }
  };

  // 列数据配置(内含修改操作和删除操作)
  const columns: ProColumns<API.Question>[] = [
    {
      title: "id",
      dataIndex: "id",
      valueType: "text",
      hideInForm: true,
    },
    {
      title: "所属题库", // 需要支持对这个的搜索
      dataIndex: "questionBankId",
      hideInTable: true, // 隐藏在表格中
      hideInForm: true, // 隐藏在表单中
    },
    {
      title: "标题",
      dataIndex: "title",
      valueType: "text",
    },
    {
      title: "内容",
      dataIndex: "content",
      valueType: "text",
      hideInSearch: true,
      width: 240,
      // 重写: 将朴素的文本框转化为 Markdown 组件
      // @ts-ignore
      renderFormItem: (item, { fieldProps }) => {
        // 编写要渲染的表单项
        return <MdEditor {...fieldProps} />;
      },
    },
    {
      title: "答案",
      dataIndex: "answer",
      valueType: "text",
      hideInSearch: true,
      width: 640,
      // 重写: 将朴素的文本框转化为 Markdown 组件
      // @ts-ignore
      renderFormItem: (item, { fieldProps }) => {
        // 编写要渲染的表单项
        return <MdEditor {...fieldProps} />;
      },
    },
    {
      title: "标签",
      dataIndex: "tags",
      valueType: "select",
      fieldProps: {
        mode: "tags",
      },
      // 重写: 将朴素的 JSON 标签转化为 TagList 组件
      render: (_, record) => {
        const tagList = JSON.parse(record.tags || "[]");
        return <TagList tagList={tagList} />;
      },
    },
    {
      title: "创建用户",
      dataIndex: "userId",
      valueType: "text",
      hideInForm: true,
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
      title: "编辑时间",
      sorter: true,
      dataIndex: "editTime",
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
    {
      title: "操作",
      dataIndex: "option",
      valueType: "option",
      render: (_, record) => (
        <Space size="middle">
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateModalVisible(true);
            }}
          >
            修改
          </Typography.Link>
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateBankModalVisible(true);
            }}
          >
            修改所属题库
          </Typography.Link>
          <Typography.Link type="danger" onClick={() => handleDelete(record)}>
            删除
          </Typography.Link>
        </Space>
      ),
    },
  ];

  // 组织页面
  return (
    <div id="adminQuestionPage" className="max-width-content">
      <PageContainer>
        {/* 表格组件 */}
        <ProTable<API.Question>
          headerTitle={"查询表格"}
          actionRef={actionRef}
          rowKey="key"
          scroll={{ x: true }}
          search={{
            labelWidth: "auto",
          }}
          toolBarRender={() => [
            <Button
              type="primary"
              key="primary"
              onClick={() => {
                setCreateModalVisible(true); // 点击新建后就打开新建弹窗
              }}
            >
              <PlusOutlined /> 新建
            </Button>,
          ]}
          request={async (params, sort, filter) => {
            // TODO: 通过 request() 的 sort 参数得到排序条件, 让后端支持多列排序
            const sortField = Object.keys(sort)?.[0];
            const sortOrder = sort?.[sortField] ?? undefined;
            // 获取表格内的题目数据
            // @ts-ignore
            const { data, code } = await listQuestionByPageUsingPost({
              ...params,
              sortField,
              sortOrder,
              ...filter,
            } as API.QuestionQueryRequest);

            return {
              success: code === 0,
              // @ts-ignore
              data: data?.records || [],
              // @ts-ignore
              total: Number(data?.total) || 0,
            };
          }}
          columns={columns}
        />

        {/* 创建弹窗 */}
        <CreateModal
          visible={createModalVisible}
          columns={columns}
          onSubmit={() => {
            setCreateModalVisible(false); // 弹窗关闭
            actionRef.current?.reload(); // 刷新表格, 并且重新触发 ProTable 的 request 方法
          }}
          onCancel={() => {
            setCreateModalVisible(false); // 弹窗关闭
          }}
        />

        {/* 修改弹窗 */}
        <UpdateModal
          visible={updateModalVisible}
          columns={columns}
          oldData={currentRow}
          onSubmit={() => {
            setUpdateModalVisible(false); // 弹窗关闭
            setCurrentRow(undefined); // 清空当前行数据
            actionRef.current?.reload(); // 刷新表格, 并且重新触发 ProTable 的 request 方法
          }}
          onCancel={() => {
            setUpdateModalVisible(false); // 弹窗关闭
          }}
        />

        {/* 修改所属题库弹窗 */}
        <UpdateBankModal
          visible={updateBankModalVisible}
          questionId={currentRow?.id}
          onCancel={() => {
            setUpdateBankModalVisible(false); // 弹窗关闭
          }}
        />
      </PageContainer>
    </div>
  );
};

export default QuestionAdminPage;
