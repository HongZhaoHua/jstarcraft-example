package com.jstarcraft.example.account.exception;

import com.jstarcraft.example.common.exception.StatusException;

public class AccountException extends StatusException {

	private static final long serialVersionUID = -1525854130102137298L;

	/** 签名非法 */
	public static final AccountException SIGNATURE_ILLEGAL = new AccountException(-1, "签名非法");

	/** 签名过期 */
	public static final AccountException SIGNATURE_EXPIRED = new AccountException(-2, "签名过期");

	/** 非法服务器 */
	public static final AccountException SERVER_ILLEGAL = new AccountException(-3, "非法服务器");

	/** 账号已存在 */
	public static final AccountException ACCOUNT_EXISTENT = new AccountException(-4, "账号已存在");

	/** 账号不存在 */
	public static final AccountException ACCOUNT_NONEXISTENT = new AccountException(-5, "账号不存在");

	/** 非法Token */
	public static final AccountException TOKEN_ILLEGAL = new AccountException(-5, "非法Token");

	protected AccountException(int status, String message) {
		super(status, message);
	}

}
