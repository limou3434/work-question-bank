/**
 * src/components/MdViewer/index.tsx
 * Markdown 浏览器组件
 *
 * 使用方法:
 * const [text, setText] = useState<string>("");
 * <MdEditor value={text} onChange={setText} />
 * <MdViewer value={text} />
 */

/* 样式 */
import "./index.css";

/* 引入 */
import { Viewer } from "@bytemd/react";
import gfm from "@bytemd/plugin-gfm";
import highlight from "@bytemd/plugin-highlight";
import "bytemd/dist/index.css";
import "highlight.js/styles/vs.css";
import "github-markdown-css/github-markdown-light.css";

/* 属性 */
interface Props {
  value?: string;
}

/* 实现 */
const plugins = [gfm(), highlight()];

const MdViewer = (props: Props) => {
  const { value = "" } = props;

  return (
    <div className="md-viewer">
      <Viewer value={value} plugins={plugins} />
    </div>
  );
};

export default MdViewer;
