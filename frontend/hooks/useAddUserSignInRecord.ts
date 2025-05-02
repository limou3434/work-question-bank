/* 引入 */
import { useEffect, useState } from "react";
import { addUserSignIn } from "@/api/userController";
import { message } from "antd";

/* 实现 */
const useAddUserSignInRecord = () => {
  const [loading, setLoading] = useState<boolean>(true); // 用户需要通过 loading 知道操作是否正在进行

  const doFetch = async () => {
    setLoading(true);
    try {
      const res = await addUserSignIn({});
    } catch (e) {
      
      message.error("今日签到失败: ", e.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    doFetch();
  }, []);

  return { loading }; // 通过 loading 标志, 前端可以显示加载状态（如加载动画、按钮禁用等）, 让用户知道操作正在处理中, 避免重复触发
};

export default useAddUserSignInRecord;
