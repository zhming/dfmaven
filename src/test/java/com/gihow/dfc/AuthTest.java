package com.gihow.dfc;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.gihow.security.session.Auth;
import com.gihow.security.session.PasswordExpiredException;
import org.joda.time.DateTime;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-18
 * Time: 下午3:26
 * To change this template use File | Settings | File Templates.
 */
public class AuthTest {
    @Test
    public void authDfcTest(){
        String username = "Administrator";
        String password = "gihow1qaz";
        for(int i = 0; i < 200; i++){
            try {
                assertEquals(true,Auth.authDfc(username, password));
            } catch (PasswordExpiredException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }



    }

    @Test
    public void authenticateTest(){
        IDfClientX clientX = new DfClientX();
        IDfClient client = null;
        IDfSessionManager sMgr = null;
        for(int i = 0; i < 10; i++){
            try {
                client = clientX.getLocalClient();
                IDfLoginInfo loginInfo = clientX.getLoginInfo();
                loginInfo.setUser("Administrator");
                loginInfo.setPassword("gihow1qaz");
                sMgr = client.newSessionManager();
                sMgr.setIdentity("GihowR", loginInfo);
                sMgr.authenticate("GihowR");
                System.out.println("身份验证成功");

            } catch (Exception e) {
                if(e instanceof DfIdentityException){
                    System.out.println("身份验证错误");
                }else if(e instanceof DfAuthenticationException){
                    System.out.println("用户认证信息错误");
                }else if(e instanceof DfPrincipalException){
                    System.out.println("Session问题");
                }else if(e instanceof DfServiceException){
                    System.out.println("释放session错误");
                }else{
                    throw new RuntimeException(e.getMessage());
                }

            }
        }

    }
}
