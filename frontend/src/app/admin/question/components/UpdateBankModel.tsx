/**
 * src/app/admin/question/components/UpdateBankModel.tsx
 * 修改所属题库弹窗组件
 */

/* 渲染 */
"use client";

/* 导入 */
import { Form, message, Modal, Select } from "antd";
import {
  addQuestionBankQuestionUsingPost,
  listQuestionBankQuestionByPageUsingPost,
  listQuestionBankQuestionVoByPageUsingPost,
  removeQuestionBankQuestionUsingPost,
} from "@/api/questionBankQuestionController";
import React, { useEffect, useState } from "react";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";

/* 属性 */
interface Props {
  questionId?: number;
  visible: boolean;
  onCancel: () => void;
}

/* 定义 */
// 更新弹窗
const UpdateBankModal: React.FC<Props> = (props) => {
  const { questionId, visible, onCancel } = props;

  const [questionBankList, setQuestionBankList] = useState<
    API.QuestionBankVO[]
  >([]);

  const [form] = Form.useForm();

  // 获取当前题目的所属题库
  const getCurrentQuestionBankIdList = async () => {
    try {
      // TODO: 没懂这里为什么还需要判断
      const res = await listQuestionBankQuestionByPageUsingPost({
        questionId,
        pageSize: 20,
      });
      // @ts-ignore
      const list = (res.data?.records ?? []).map(
        // @ts-ignore
        (item) => item.questionBankId,
      );
      // @ts-ignore
      form.setFieldsValue({ questionBankIdList: list });
    } catch (e) {
      // @ts-ignore
      message.error("获取题目所属题库列表标识失败: " + e.message);
    }
  };

  useEffect(() => {
    if (questionId) {
      getCurrentQuestionBankIdList();
    }
  }, [questionId, form]); // 避免重复调用

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

  useEffect(() => {
    getQuestionBankList();
  }, []); // 只需调用一次

  return (
    <Modal
      destroyOnClose
      title={"更新所属题库"}
      open={visible}
      footer={null}
      onCancel={onCancel}
    >
      {/* @ts-ignore */}
      <Form form={form} sytle={{ marginTop: 16 }}>
        <Form.Item label="选择所属题库" name="questionBankIdList">
          {/* @ts-ignore */}
          <Select
            mode="multiple"
            // @ts-ignore
            sytle={{ width: "100%" }}
            options={questionBankList.map((questionBank) => {
              return {
                label: questionBank.title,
                value: questionBank.id,
              };
            })}
            onSelect={async (value) => {
              const hide = message.loading("正在更新");
              try {
                await addQuestionBankQuestionUsingPost({
                  questionId,
                  questionBankId: value,
                });
                hide();
                message.success("更新绑定题库成功");
              } catch (error: any) {
                hide();
                message.error("更新绑定题库失败: " + error.message);
              }
            }}
            onDeselect={async (value) => {
              const hide = message.loading("正在更新");
              try {
                await removeQuestionBankQuestionUsingPost({
                  questionId,
                  questionBankId: value,
                });
                hide();
                message.success("取消绑定题库成功");
              } catch (error: any) {
                hide();
                message.error("取消绑定题库失败: " + error.message);
              }
            }}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};

/* 导出 */
export default UpdateBankModal;
