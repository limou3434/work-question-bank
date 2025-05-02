/**
 * src/app/admin/user/page.tsx
 * 用户管理页面组件
 */

/* 渲染 */
"use client"; // 注释本行则默认服务端渲染

/* 样式 */
import "./page.css";

/* 引入 */
import CreateModal from "@/app/admin/user/components/CreateModal";
import UpdateModal from "@/app/admin/user/components/UpdateModal";
import { PlusOutlined } from "@ant-design/icons";
import type { ActionType, ProColumns } from "@ant-design/pro-components";
import { PageContainer, ProTable } from "@ant-design/pro-components";
import { Button, message, Space, Typography } from "antd";
import React, { useRef, useState } from "react";
import {
  deleteUser,
  listUserByPage,
} from "@/api/userController";

/* 定义 */
// 用户管理页面
const UserAdminPage: React.FC = () => {
  // 是否显示新建窗口
  const [createModalVisible, setCreateModalVisible] = useState<boolean>(false);

  // 是否显示更新窗口
  const [updateModalVisible, setUpdateModalVisible] = useState<boolean>(false);

  // 引用表格
  const actionRef = useRef<ActionType>(null);

  // 当前用户的数据
  const [currentRow, setCurrentRow] = useState<API.User>();

  // 行删除逻辑
  const handleDelete = async (row: API.User) => {
    const hide = message.loading("正在删除");
    if (!row) return true;
    try {
      await deleteUser({
        id: row.id as any,
      });
      hide();
      message.success("删除成功");
      actionRef?.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      // message.error("删除失败: " + error.message);
      return false;
    }
  };

  // 列数据配置(内含修改操作和删除操作)
  const columns: ProColumns<API.User>[] = [
    {
      title: "id",
      dataIndex: "id",
      valueType: "text",
      hideInForm: true, // 隐藏表单(主要用于后面复用在新建弹窗和修改弹窗中不显示某个表单字段)
    },
    {
      title: "账号",
      dataIndex: "userAccount",
      valueType: "text",
    },
    {
      title: "用户名",
      dataIndex: "userName",
      valueType: "text",
    },
    {
      title: "头像",
      dataIndex: "userAvatar",
      valueType: "image",
      fieldProps: {
        width: 64,
      },
      hideInSearch: true,
    },
    {
      title: "简介",
      dataIndex: "userProfile",
      valueType: "textarea",
    },
    {
      title: "权限",
      dataIndex: "userRole",
      valueEnum: {
        user: {
          text: "用户",
        },
        admin: {
          text: "管理员",
        },
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
    {
      title: "操作",
      dataIndex: "option",
      valueType: "option",
      render: (_, record) => (
        <Space size="middle">
          {/* 修改 */}
          <Typography.Link
            onClick={() => {
              setCurrentRow(record);
              setUpdateModalVisible(true); // 打开弹窗
            }}
          >
            修改
          </Typography.Link>

          {/* 删除 */}
          <Typography.Link type="danger" onClick={() => handleDelete(record)}>
            {/* TODO: 加强删除逻辑减少误删操作, 可以考虑使用 Popconfirm 组件 */}
            删除
          </Typography.Link>
        </Space>
      ),
    },
  ];

  // 组织页面
  return (
    <div id="adminUserPage" className="max-width-content">
      <PageContainer>
        {/* 表格组件 */}
        <ProTable<API.User>
          headerTitle={"查询表格"}
          actionRef={actionRef}
          rowKey="key"
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
            // 获取表格内的用户数据
            // @ts-ignore
            const { data, code } = await listUserByPage({
              ...params,
              sortField,
              sortOrder,
              ...filter,
            } as API.UserQueryRequest);

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
      </PageContainer>
    </div>
  );
};

export default UserAdminPage;
