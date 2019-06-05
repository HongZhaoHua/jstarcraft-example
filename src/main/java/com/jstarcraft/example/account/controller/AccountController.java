package com.jstarcraft.example.account.controller;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jstarcraft.example.account.exception.AccountException;
import com.jstarcraft.example.account.service.Account;
import com.jstarcraft.example.account.service.AccountService;
import com.jstarcraft.example.common.output.NormalOutput;
import com.jstarcraft.example.common.utility.HttpUtility;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    /**
     * 注册账号
     * 
     * @param request
     * @param accountName
     * @return
     */
    @ApiOperation(value = "注册账号", notes = "注册账号")
    @PostMapping("/registerAccount")
    public NormalOutput<Long> registerAccount(HttpServletRequest request, @RequestParam String accountName) {
        int sectionIndex = accountService.getSection(accountName);
        Account account = accountService.registerAccount(accountName, sectionIndex, Instant.now(), HttpUtility.getIp(request));
        return new NormalOutput<>(account.getId());
    }

    /**
     * 登入帐号
     * 
     * @param accountName
     * @return
     */
    @ApiOperation(value = "登入帐号", notes = "登入帐号")
    @PostMapping("/loginAccount")
    public NormalOutput<Long> loginAccount(@RequestParam String accountName) {
        Account account = accountService.getAccount(accountName);
        if (account == null) {
            throw AccountException.ACCOUNT_NONEXISTENT;
        }
        return new NormalOutput<>(account.getId());
    }

    /**
     * 登出账号
     * 
     * @param accountId
     * @return
     */
    @ApiOperation(value = "登出账号", notes = "登出账号")
    @PostMapping("/logoutAccount")
    public NormalOutput<Long> logoutAccount(@RequestHeader long accountId) {
        Account account = accountService.getAccount(accountId);
        accountService.logoutAccount(account);
        return new NormalOutput<>(account.getId());
    }

}
