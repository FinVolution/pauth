package com.ppdai.auth;


import com.ppdai.auth.config.LDAPConfiguration;
import com.ppdai.auth.service.LdapService;
import com.ppdai.auth.vo.UserVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LdapServiceTest {
    @Autowired
    private LdapService ldapService;

    @Test
    public void testLogin(){
        UserVO userVO=
                ldapService.login("lizhiming","123456789");
        System.out.println(userVO.toString());
    }
}
