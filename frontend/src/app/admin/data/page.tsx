// src/app/admin/data/page.tsx
/* 渲染方式 */
"use client"; // 注释本行则默认服务端渲染

/* 引入样式 */
import "./page.css";
import { PageContainer } from "@ant-design/pro-components";

/* 组织页面 */
export default function DataPage() {
  return (
    <div id="dataPage" className="max-width-content">
      <PageContainer>
        <h1>Data Page</h1>
        {/* TODO: 设计关于本网站的数据展示 */}
      </PageContainer>
    </div>
  );
}
