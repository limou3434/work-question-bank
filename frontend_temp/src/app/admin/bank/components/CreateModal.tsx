/**
 * src/app/admin/bank/components/CreateModal.tsx
 * 创建弹窗组件组件
 */

/* 引入 */
import { ProColumns, ProTable } from "@ant-design/pro-components";
import { message, Modal } from "antd";
import React from "react";
import { addQuestionBankUsingPost } from "@/api/questionBankController";

/* 属性 */
interface Props {
  visible: boolean;
  columns: ProColumns<API.QuestionBank>[];
  onSubmit: (values: API.QuestionBankAddRequest) => void;
  onCancel: () => void;
}

/* 定义 */
// 添加节点
const handleAdd = async (fields: API.QuestionBankAddRequest) => {
  const hide = message.loading("正在添加");
  try {
    await addQuestionBankUsingPost(fields);
    hide();
    message.success("创建成功");
    return true;
  } catch (error: any) {
    hide();
    message.error("创建失败: " + error.message);
    return false;
  }
};

// 创建弹窗
const CreateModal: React.FC<Props> = (props) => {
  const { visible, columns, onSubmit, onCancel } = props;

  return (
    <Modal
      destroyOnClose
      title={"创建"}
      open={visible}
      footer={null}
      onCancel={() => {
        onCancel?.();
      }}
    >
      <ProTable
        type="form"
        columns={columns}
        onSubmit={async (values: API.QuestionBankAddRequest) => {
          const success = await handleAdd(values);
          if (success) {
            onSubmit?.(values);
          }
        }}
      />
    </Modal>
  );
};

/* 导出 */
export default CreateModal;
