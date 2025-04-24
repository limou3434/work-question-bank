/**
 * 页面尾部组件
 */

/* 渲染方式 */
// "use client"; // 注释本行则默认服务端渲染

/* 引入样式 */
import "./index.css"; // TODO：引入的样式有点问题, 导致页面尾部有些偏左...

/* 引入组件 */
import React from "react";

/* 定义属性 */
// interface Props {
//   // ...
// }

/* 组织组件 */
export default function GlobalFooter() {
  const currentYear = new Date().getFullYear();
  return (
    <div
      className="global-footer"
      style={{
        textAlign: "center",
        paddingBlockStart: 12,
      }}
    >
      <div>© {currentYear} 智能面试平台</div>
      <div>
        <a href="" target="_blank">
          by limou 3434
        </a>
      </div>
    </div>
  );
}
