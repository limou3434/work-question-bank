/**
 * src/access/menuAccess.ts
 * 定义检查当前用户权限的显示控制器
 */

import { menus } from "../../config/menus";
import checkAccess from "@/access/checkAccess";

const getAccessibleMenus = (loginUser: API.LoginUserVO, menuItems = menus) => {
  // 其实就是根据用户权限和权限校验器来过滤菜单数组
  return menuItems.filter((item) => {
    if (!checkAccess(loginUser, item.access)) {
      return false;
    }

    if (item.children) {
      item.children = getAccessibleMenus(loginUser, item.children);
    }

    return true;
  });
};

export default getAccessibleMenus;
