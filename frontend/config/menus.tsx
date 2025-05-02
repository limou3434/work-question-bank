/**
 * config/menus.tsx
 * 这里定义了菜单列表的配置, 是以一个数组交给其他文件读取来实现的,
 * 因此在这个文件里的数组加字段不影响整个项目的使用, 可以方便拓展
 */

import { MenuDataItem } from "@umijs/route-utils";
import {
  HomeOutlined,
  EditOutlined,
  AppstoreOutlined,
  BulbOutlined,
  CrownOutlined,
} from "@ant-design/icons";
import ACCESS_ENUM from "@/access/accessEnum";

/* 导航栏菜单列表配置数组 */
export const menus = [
  {
    path: "/",
    name: "主页",
    icon: <HomeOutlined />,
    access: ACCESS_ENUM.NOT_LOGIN,
    // hideInMenu: true, // 直接控制菜单显隐
  },
  {
    path: "/questions",
    name: "题目",
    access: ACCESS_ENUM.NOT_LOGIN,
    icon: <EditOutlined />,
  },
  {
    path: "/banks",
    name: "题库",
    access: ACCESS_ENUM.NOT_LOGIN,
    icon: <AppstoreOutlined />,
  },
  {
    path: "/other",
    name: "其他",
    access: ACCESS_ENUM.NOT_LOGIN,
    icon: <BulbOutlined />,
  },
  {
    path: "/user/center",
    access: ACCESS_ENUM.USER,
    hideInMenu: true,
  },
  {
    path: "/admin",
    name: "管理",
    access: ACCESS_ENUM.ADMIN,
    icon: <CrownOutlined />,
    children: [
      {
        path: "/admin/user",
        name: "用户管理",
        access: ACCESS_ENUM.ADMIN,
      },
      {
        path: "/admin/bank",
        name: "题库管理",
        access: ACCESS_ENUM.ADMIN,
      },
      {
        path: "/admin/question",
        name: "题目管理",
        access: ACCESS_ENUM.ADMIN,
      },
      {
        path: "/admin/data",
        name: "数据管理",
        access: ACCESS_ENUM.ADMIN,
      },
    ],
  },
] as MenuDataItem[]; // 这个用于上述类型定义的智能提示

/* 根据全部路径查找菜单 */
export const findAllMenuItemByPath = (path: string): MenuDataItem | null => {
  return findMenuItemByPath(menus, path);
};

/* 根据单个路径查找菜单(递归) */
export const findMenuItemByPath = (
  menus: MenuDataItem[],
  path: string,
): MenuDataItem | null => {
  for (const menu of menus) {
    if (menu.path === path) {
      return menu;
    }
    if (menu.children) {
      const matchedMenuItem = findMenuItemByPath(menu.children, path);
      if (matchedMenuItem) {
        return matchedMenuItem;
      }
    }
  }
  return null;
};
