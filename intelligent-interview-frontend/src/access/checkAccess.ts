/**
 * src/access/checkAccess.ts
 * 定义检查当前用户权限的权限校验器
 */

import ACCESS_ENUM from "@/access/accessEnum";

const checkAccess = (
  loginUser: API.LoginUserVO,
  needAccess = ACCESS_ENUM.NOT_LOGIN,
) => {
  // 获取当前登陆用户具有的权限(无登陆则没有权限)
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;
  // loginUserAccess: 定义了一个变量, 用于存储用户访问权限
  // loginUser?.userRole: 使用了可选链操作符 ?. 来安全地访问 loginUser 对象的 userRole 属性, 如果 loginUser 是 null 或 undefined, loginUser?.userRole 的结果就是 undefined, 不会抛出错误
  // ?? ACCESS_ENUM.NOT_LOGIN: 空值合并操作符 ??，表示如果 loginUser?.userRole 的结果是 null 或 undefined, 则使用 ACCESS_ENUM.NOT_LOGIN 作为默认值

  // 如果无需登陆也能访问
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true;
  }

  // 如果需要登陆才能访问
  if (needAccess === ACCESS_ENUM.USER) {
    return loginUserAccess !== ACCESS_ENUM.NOT_LOGIN; // 只要不是尚未登录就可以通过校验
  }

  // 如果需要管理才能访问
  if (needAccess === ACCESS_ENUM.ADMIN) {
    return loginUserAccess === ACCESS_ENUM.ADMIN; // 只要确认管理用户就可以通过校验
  }

  return false; // 默认失败, 防止意外
};

export default checkAccess;
