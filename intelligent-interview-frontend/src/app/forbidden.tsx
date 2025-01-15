/**
 * src/app/forbidden.tsx
 * 定义一个无权限访问时访问的页面
 */

import { Button, Result } from "antd";
const Forbidden = () => {
  return (
    <Result
      status={403}
      title="403"
      subTitle="对不起, 您无权访问该权限"
      extra={
        <Button type="primary" href="/">
          返回首页
        </Button>
      }
    />
  );
};

export default Forbidden;
