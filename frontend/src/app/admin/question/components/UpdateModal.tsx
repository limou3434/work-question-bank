/**
 * src/app/admin/question/components/UpdateModal.tsx
 * 修改弹窗组件
 */

/* 导入 */
import { ProColumns, ProTable } from "@ant-design/pro-components";
import React from "react";
import { updateQuestionUsingPost } from "@/api/questionController";
import { message, Modal } from "antd";

/* 属性 */
interface Props {
  oldData?: API.Question;
  visible: boolean;
  columns: ProColumns<API.Question>[];
  onSubmit: (values: API.QuestionAddRequest) => void;
  onCancel: () => void;
}

/* 定义 */
// 更新节点
const handleUpdate = async (fields: API.QuestionUpdateRequest) => {
  const hide = message.loading("正在更新");
  try {
    await updateQuestionUsingPost(fields);
    hide();
    message.success("更新成功");
    return true;
  } catch (error: any) {
    hide();
    message.error("更新失败: " + error.message);
    return false;
  }
};

// 更新弹窗
const UpdateModal: React.FC<Props> = (props) => {
  const { oldData, visible, columns, onSubmit, onCancel } = props;

  if (!oldData?.id) {
    return <></>;
  }

  // 表单转换(使得后端返回的字符可以被渲染为标签)
  const initValues = { ...oldData };
  if (oldData.tags) {
    // @ts-ignore
    initValues.tags = JSON.parse(oldData.tags) || [];
  }

  return (
    <Modal
      destroyOnClose
      title={"更新"}
      open={visible}
      footer={null}
      onCancel={() => {
        onCancel?.();
      }}
    >
      <ProTable
        type="form"
        columns={columns}
        form={{
          initialValues: initValues,
        }}
        onSubmit={async (values: API.QuestionAddRequest) => {
          const success = await handleUpdate({
            ...values,
            id: oldData?.id as any,
          });
          if (success) {
            onSubmit?.(values);
          }
        }}
      />
    </Modal>
  );
};

/* 导出 */
export default UpdateModal;
