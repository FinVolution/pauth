package com.ppdai.auth;

import com.ppdai.auth.common.identity.Identity;
import com.ppdai.auth.service.PauthLoginService;
import com.ppdai.auth.service.PauthTokenService;
import com.ppdai.auth.utils.PauthTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {
    @Autowired
    private PauthTokenService pauthTokenService;
    @Autowired
    private PauthLoginService pauthLoginService;
    @Autowired
    private PauthTokenUtil pauthTokenUtil;

    @RequestMapping("/")
    public String index(HttpServletRequest request, Model model) {
        model.addAllAttributes(setModelMap(request));
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        String loginUrl = pauthLoginService.fetchLoginUrl();
        if (loginUrl != null) {
            return "redirect:" + loginUrl;
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        pauthTokenService.revokeToken(request, response);
        return "redirect:/";
    }

    @RequestMapping("/fetchToken")
    public String fetchToken(HttpServletRequest request, HttpServletResponse response) {
        pauthTokenService.fetchToken(request, response);
        return "redirect:/";
    }

    @ResponseBody
    @RequestMapping("/refreshToken")
    public Boolean refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return pauthTokenService.refreshToken(request, response);
    }

    private Map setModelMap(HttpServletRequest request){
        Map modelMap = new HashMap();
        Identity identity = pauthTokenUtil.getTokenInfo(request);
        String userName = identity.getName();
        modelMap.put("userName", userName);
        return modelMap;
    }
}
