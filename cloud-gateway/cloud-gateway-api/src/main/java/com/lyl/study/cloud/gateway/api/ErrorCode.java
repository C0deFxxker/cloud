package com.lyl.study.cloud.gateway.api;

import com.lyl.study.cloud.base.CommonErrorCode;

public class ErrorCode extends CommonErrorCode {
    public static int BAD_CREDENTIALS = 1001;
    public static int ACCOUNT_DISABLED = 1002;
    public static int EXPIRED_SESSION = 1003;
    public static int INVALD_JWT = 1004;
    public static int INVALD_ROLE = 1005;
    public static int USERNAME_NOT_FOUND = 1006;

    public static int DEPARTMENT_DELETE_FAILED = 1101;
}
