package com.lyl.study.cloud.base;

public class CommonErrorCode {
    /**
     * 成功
     */
    public static final int OK = 0;
    /**
     * 参数错误
     */
    public static final int BAD_REQUEST = 400;
    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;
    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;
    /**
     * 找不到资源
     */
    public static final int NOT_FOUND = 404;
    /**
     * 内部错误
     */
    public static final int INTERNAL_ERROR = 500;
    /**
     * 会话超时
     */
    public static final int EXPIRED_SESSION = 601;
    /**
     * 无效JWT
     */
    public static final int INVALD_JWT = 602;
    /**
     * 找不到用户名
     */
    public static int USERNAME_NOT_FOUND = 603;
    /**
     * 非法凭证
     */
    public static int BAD_CREDENTIALS = 604;
    /**
     * 账号被冻结
     */
    public static int ACCOUNT_DISABLED = 605;
}
