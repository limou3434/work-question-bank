/*
 * 题目大全页面
 */

/* 渲染 */
"use client";

/* 样式 */
import "./page.css";

/* 引入 */
import React from "react";
import {Avatar, Card, Col, Row} from "antd";
import Title from "antd/es/typography/Title";
import {useSelector} from "react-redux";
import {RootState} from "@/stores";
import Paragraph from "antd/es/typography/Paragraph";
import CalendarChart from "@/app/user/center/components/CalendarChart";

/* 定义 */
export default function UserCenterPage() {
    const user = useSelector((state: RootState) => state.loginUser);
    const [activeTabKey, setActiveTabKey] = React.useState<string>("record");

    // 组织页面
    return (
        <div id="userCenterPage" className="max-width-content">
            <Title level={3}>用户中心</Title>
            <Row gutter={[16, 16]}>
                <Col xs={24} md={6}>
                    <Card style={{textAlign: "center"}}>
                        <Avatar src={user.userAvatar} size={72}/>
                        <div style={{marginBottom: 16}}/>
                        <Card.Meta
                            title={
                                <Title level={4} style={{marginBottom: 0}}>
                                    {user.userName}
                                </Title>
                            }
                            description={
                                <Paragraph type="secondary">{user.userProfile}</Paragraph>
                            }
                        />
                    </Card>
                </Col>
                <Col xs={24} md={18}>
                    <Card
                        tabList={[
                            {
                                key: "record",
                                label: "刷题记录",
                            },
                            {
                                key: "userInfo",
                                label: "个人信息",
                            },
                        ]}
                        activeTabKey={activeTabKey}
                        onTabChange={(key) => setActiveTabKey(key)}
                    >
                        {activeTabKey === "record" && <CalendarChart/>}
                        {activeTabKey === "userInfo" && <div>个人信息</div>}
                    </Card>
                </Col>
            </Row>
        </div>
    );
}
