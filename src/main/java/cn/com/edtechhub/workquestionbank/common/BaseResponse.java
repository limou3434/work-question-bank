package cn.com.edtechhub.workquestionbank.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 通用返回类
 *
 * @param <T>
 * @author <a href="https://github.com/limou3434">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@Data
public class BaseResponse<T> implements Serializable { // Serializable 是 Java 中的一个标记接口, 属于 java.io 包, 它并没有定义任何方法, 主要用于标识一个类的实例可以被序列化

    /**
     * 响应状态
     */
    private int code;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应消息
     */
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

}
