package cn.com.edtechhub.workquestionbank.request.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户注册请求体
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 * @from <a href="https://datalearnhub.com">大数据工作室</a>
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
