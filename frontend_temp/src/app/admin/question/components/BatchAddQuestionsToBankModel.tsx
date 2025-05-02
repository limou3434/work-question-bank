/**
 * src/app/admin/question/components/BatchAddQuestionToBankModal.tsx
 * 批量绑定题目弹窗组件
 */

/* 渲染 */
"use client";

/* 导入 */
import { Button, Form, message, Modal, Select } from "antd";
import React, { useEffect, useState } from "react";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";
import { batchAddQuestionsToBankUsingPost } from "@/api/questionBankQuestionController";

/* 属性 */
interface Props {
  questionIdList?: number[];
  visible: boolean;
  onSubmit: () => void;
  onCancel: () => void;
}

/* 定义 */
// 更新弹窗
const BatchAddQuestionsToBankModal: React.FC<Props> = (props) => {
  const { questionIdList, visible, onSubmit, onCancel } = props;

  const [form] = Form.useForm();

  const [questionBankList = [], setQuestionBankList] = useState<
    API.QuestionBankVO[]
  >([]);

  // 获取题库列表
  const getQuestionBankList = async () => {
    const pageSize = 200;

    try {
      const res = await listQuestionBankVoByPageUsingPost({
        pageSize,
        sortField: "createTime",
        sortOrder: "descend",
      });
      // @ts-ignore
      setQuestionBankList(res.data?.records ?? []);
    } catch (e) {
      // @ts-ignore
      message.error("获取题库列表失败: " + e.message);
    }
  };

  // 提交表单
  const doSubmit = async (values: API.QuestionBankQuestionBatchAddRequest) => {
    const hide = message.loading("正在操作");

    const questionBankId = values.questionBankId;
    if (!questionBankId) {
      return;
    }

    try {
      await batchAddQuestionsToBankUsingPost({
        questionBankId,
        questionIdList,
      });
      hide();
      message.success("操作成功");
      onSubmit?.();
    } catch (e: any) {
      hide();
      message.success("操作失败: " + e.message);
    }
  };

  useEffect(() => {
    getQuestionBankList();
  }, []); // 只需调用一次

  return (
    <Modal
      destroyOnClose
      title={"批量绑定题目"}
      open={visible}
      footer={null}
      onCancel={() => onCancel?.()}
    >
      {/* @ts-ignore */}
      <Form form={form} sytle={{ marginTop: 16 }} onFinish={doSubmit}>
        <Form.Item label="选择所属题库" name="questionBankId">
          {/* @ts-ignore */}
          <Select
            // @ts-ignore
            sytle={{ width: "100%" }}
            options={questionBankList.map((questionBank) => {
              return {
                label: questionBank.title,
                value: questionBank.id,
              };
            })}
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            提交
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
};

/* 导出 */
export default BatchAddQuestionsToBankModal;
