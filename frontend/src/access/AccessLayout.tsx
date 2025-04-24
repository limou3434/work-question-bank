/**
 * src/access/AccessLayout.tsx
 * 全局拦截器, 使用权限校验器检验权限来拦截页面
 */

import { usePathname } from "next/navigation";
import { useSelector } from "react-redux";
import { RootState } from "@/stores";
import { findAllMenuItemByPath } from "../../config/menus";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";
import Forbidden from "@/app/forbidden";

const AccessLayout: React.FC<
  Readonly<{
    children: React.ReactNode;
  }>
> = ({ children }) => {
  // 获取数据
  const pathname = usePathname(); // 获取当前页面路径
  const loginUser = useSelector((state: RootState) => state.loginUser); // 获取当前登录用户信息
  const menu = findAllMenuItemByPath(pathname); // 获取当前页面对应的菜单
  const needAccess = menu?.access ?? ACCESS_ENUM.NOT_LOGIN; // 获取页面需要的权限
  const canAccess = checkAccess(loginUser, needAccess); // 获取校验权限的结果

  // 非法访问
  if (!canAccess) {
    return <Forbidden />; // 不能访问就跳转到 403 页面
  }

  // 合法访问
  return children;
};

export default AccessLayout;
