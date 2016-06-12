package com.ecm.security.session;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-19
 * Time: 上午9:12
 * To change this template use File | Settings | File Templates.
 */
public class PasswordExpiredException extends Throwable {
    public PasswordExpiredException(String strMessage) {
        super("密码已过期：" + strMessage);
    }
}
