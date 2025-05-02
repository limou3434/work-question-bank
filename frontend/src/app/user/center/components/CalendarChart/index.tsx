/* 渲染 */
"use client";

/* 样式 */
import "./index.css";

/* 引入 */
import ReactECharts from "echarts-for-react";
import React, { useEffect, useState } from "react";
import dayjs from "dayjs";
import {
  getUserById,
  getUserSignIn,
} from "@/api/userController";
import { message } from "antd";

/* 实现 */
const CalendarChart = () => {
  const [dataList, setDataList] = useState<number[]>([]);

  const fetchDataList = async () => {
    try {
      const res = await getUserSignIn({
        year,
      });
      // @ts-ignore
      setDataList(res.data);
    } catch (e) {
      
      // message.error("获取刷题记录失败: ", e.message);
    }
  };

  useEffect(() => {
    fetchDataList();
  }, []);

  const year = new Date().getFullYear();

  const optionsData = dataList.map((dayOfYear) => {
    const dateStr = dayjs(`${year}-01-01`)
      .add(dayOfYear - 1, "day") // 根据偏移量得到第几天
      .format("YYYY-MM-DD");
    return [dateStr, 1];
  });

  const options = {
    visualMap: {
      show: false,
      min: 0,
      max: 1,
      inRange: {
        color: ["#f2f3f9", "#6acf5d"],
      },
    },
    calendar: {
      range: year,
      left: 20,
      cellSize: ["auto", 16],
      yearLabel: {
        position: "top",
        formatter: `${year} 年刷题记录`,
      },
    },
    series: {
      type: "heatmap",
      coordinateSystem: "calendar",
      data: optionsData,
    },
  };

  return <ReactECharts className="calendar-chart" option={options} />;
};

export default CalendarChart;
