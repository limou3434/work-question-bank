

import request from "@/libs/request";

/** 此处后端没有提供注释 POST /questionBank/add */
export async function addQuestionBank(
  body: API.QuestionBankAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>("/questionBank/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /questionBank/delete */
export async function deleteQuestionBank(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/questionBank/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /questionBank/edit */
export async function editQuestionBank(
  body: API.QuestionBankEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/questionBank/edit", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /questionBank/get/vo */
export async function getQuestionBankVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionBankVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseQuestionBankVO>("/questionBank/get/vo", {
    method: "GET",
    params: {
      ...params,
      questionBankQueryRequest: undefined,
      ...params["questionBankQueryRequest"],
    },
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /questionBank/list/page */
export async function listQuestionBankByPage(
  body: API.QuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionBank>("/questionBank/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 POST /questionBank/list/page/vo */
export async function listQuestionBankVoByPage(
  body: API.QuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionBankVO>(
    "/questionBank/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /questionBank/my/list/page/vo */
export async function listMyQuestionBankVoByPage(
  body: API.QuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionBankVO>(
    "/questionBank/my/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** 此处后端没有提供注释 POST /questionBank/update */
export async function updateQuestionBank(
  body: API.QuestionBankUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>("/questionBank/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
