// src/constants/user.ts

import ACCESS_ENUM from "@/access/accessEnum";

/**
 * 默认用户常量
 */

// 用户默认状态
export const DEFAULT_USER: API.LoginUserVO = {
  userName: "未登录",
  userProfile: "暂无简介",
  userAvatar: "/assets/images/notLoginUser128x128.png",
  userRole: ACCESS_ENUM.NOT_LOGIN,
};
