package qqcommon;/**
 * @author 杨周
 * @data 2022/10/23
 * @time 14:27
 * 表示一个用户/客户的信息
 */

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;//用户的Id/用户名
    private String passwd;//用户密码
    public User(String userId,String passwd){
        this.passwd = passwd;
        this.userId = userId;
    }
    public User(){}
    public String getUserId() {
        return userId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
