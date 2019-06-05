package com.jstarcraft.example.account.exception;

import com.jstarcraft.example.common.exception.StatusException;

public class AccountException extends StatusException {

    private static final long serialVersionUID = -1525854130102137298L;

    /** 账号已存在 */
    public static final AccountException ACCOUNT_EXISTENT = new AccountException(-4, "账号已存在");

    /** 账号不存在 */
    public static final AccountException ACCOUNT_NONEXISTENT = new AccountException(-5, "账号不存在");

    protected AccountException(int status, String message) {
        super(status, message);
    }

}
