package com.lyl.study.cloud.wxclient.web.vip.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lyl.study.cloud.base.CommonErrorCode;
import com.lyl.study.cloud.base.ImageVerification;
import com.lyl.study.cloud.base.dto.Result;
import com.lyl.study.cloud.vip.api.dto.request.MemberSaveForm;
import com.lyl.study.cloud.vip.api.dto.response.MemberDTO;
import com.lyl.study.cloud.vip.api.facade.MemberFacade;
import com.lyl.study.cloud.wxclient.security.CurrentSessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/session")
public class SessionController {
    @Reference
    private MemberFacade memberFacade;

    private ImageVerification imageVerification = new ImageVerification();

    @Value("${cloud.security.verifyCodeSessionName}")
    private String verifyCodeSessionName;

    /**
     * 获取当前会话信息
     */
    @GetMapping("/currentUser")
    @ResponseBody
    public Result<MemberDTO> getCurrentUser() {
        MemberDTO currentUser = CurrentSessionHolder.getCurrentUser();
        if (currentUser != null) {
            return new Result<>(CommonErrorCode.OK, "查询成功", currentUser);
        } else {
            return new Result<>(CommonErrorCode.FORBIDDEN, "尚未登录", null);
        }
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    @ResponseBody
    public Result<Long> register(@Validated @RequestBody MemberSaveForm form) {
        long id = memberFacade.save(form);
        return new Result<>(CommonErrorCode.OK, "注册成功", id);
    }

    /**
     * 图形验证码
     */
    @GetMapping("/verifyCode")
    public void getVerifyCode(HttpSession httpSession, HttpServletResponse response) throws IOException {
        String code = imageVerification.createCode(4);
        httpSession.setAttribute(verifyCodeSessionName, code);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        imageVerification.write(response.getOutputStream(), code);
    }
}
