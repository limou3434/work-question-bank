/**
 * src/app/admin/user/components/UpdateModal.tsx
 * 修改弹窗组件组件
 */

/* 导入 */
import { ProColumns, ProTable } from "@ant-design/pro-components";
import React from "react";
import { updateUser } from "@/api/userController";
import { message, Modal } from "antd";

/* 属性 */
interface Props {
  oldData?: API.User;
  visible: boolean;
  columns: ProColumns<API.User>[];
  onSubmit: (values: API.UserAddRequest) => void;
  onCancel: () => void;
}

/* 定义 */
// 更新节点
const handleUpdate = async (fields: API.UserUpdateRequest) => {
  const hide = message.loading("正在更新");
  try {
    await updateUser(fields);
    hide();
    message.success("更新成功");
    return true;
  } catch (error: any) {
    hide();
    // message.error("更新失败: " + error.message);
    return false;
  }
};

// 更新弹窗
const UpdateModal: React.FC<Props> = (props) => {
  const { oldData, visible, columns, onSubmit, onCancel } = props;

  if (!oldData) {
    return <></>;
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
          initialValues: oldData,
        }}
        onSubmit={async (values: API.UserAddRequest) => {
          const success = await handleUpdate({
            ...values,
            id: oldData.id as any,
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
