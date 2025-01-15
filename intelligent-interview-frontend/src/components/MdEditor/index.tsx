/**
 * src/components/MdEditor/index.tsx
 * Markdown 编辑器组件
 *
 * 使用方法:
 * const [text, setText] = useState<string>("");
 * <MdEditor value={text} onChange={setText} />
 * <MdViewer value={text} />
 */

/* 样式 */
import "./index.css";

/* 引入 */
import { Editor } from "@bytemd/react";
import gfm from "@bytemd/plugin-gfm";
import highlight from "@bytemd/plugin-highlight";
import "bytemd/dist/index.css";
import "highlight.js/styles/vs.css";

/* 属性 */
interface Props {
  value?: string;
  onChange?: (v: string) => void;
  placeholder?: string;
}

/* 实现 */
const plugins = [gfm(), highlight()]; // 要导入的插件

const MdEditor = (props: Props) => {
  const { value = "", onChange, placeholder } = props;

  return (
    <div className="md-editor">
      <Editor
        value={value || ""}
        placeholder={placeholder}
        mode="split" // 一分为二的编辑器模式(浏览器|编辑器)
        plugins={plugins}
        onChange={onChange}
      />
    </div>
  );
};

export default MdEditor;
