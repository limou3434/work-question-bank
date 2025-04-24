/**
 * src/components/TagList/index.tsx
 * 标签列表组件
 */

/* 样式 */
import "./index.css";

/* 引入 */
import { Tag } from "antd";

/* 属性 */
interface Props {
  tagList?: string[];
}

/* 实现 */
const TagList = (props: Props) => {
  const { tagList = [] } = props;

  return (
    <div className="tag-list">
      {tagList.map((tag) => {
        return <Tag key={tag}>{tag}</Tag>;
      })}
    </div>
  );
};

export default TagList;
