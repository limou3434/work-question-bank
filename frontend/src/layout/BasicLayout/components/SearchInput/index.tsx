/**
 * src/layout/BasicLayout/components/SearchInput/index.tsx
 * 搜索条组件
 */

/* 渲染 */
// "use client"; // 注释本行则默认服务端渲染

/* 样式 */
import "./index.css";

/* 引入 */
import React from "react";
import { Input } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import { useRouter } from "next/navigation";

/* 定义 */
const SearchInput = () => {
  const router = useRouter();

  return (
    <div
      className="search-input"
      key="SearchOutlined"
      aria-hidden
      style={{
        display: "flex",
        alignItems: "center",
        marginInlineEnd: 24,
      }}
    >
      <Input.Search
        style={{
          borderRadius: 4,
          marginInlineEnd: 12,
        }}
        prefix={<SearchOutlined />}
        placeholder="搜索题目"
        onSearch={(value) => {
          const encodedValue = encodeURIComponent(value); // 提前使用 URL 编码
          router.push(`/questions?q=${encodedValue}`);
        }}
      />
    </div>
  );
};

export default SearchInput;
