package com.rui;

/**
 * 所属项目:pa
 *
 * @author rui10038 邮箱：2450782689@qq.com
 * @version 1.0
 * //                .-~~~~~~~~~-._       _.-~~~~~~~~~-.
 * //            __.'              ~.   .~              `.__
 * //          .'//    JAVA无涯      \./     回头是岸     \\`.
 * //        .'//                     |                     \\`.
 * //      .'// .-~"""""""~~~~-._     |     _,-~~~~"""""""~-. \\`.
 * //    .'//.-"                 `-.  |  .-'                 "-.\\`.
 * //  .'//______.============-..   \ | /   ..-============.______\\`.
 * //.'______________________________\|/______________________________`.
 * @date 2020/2/11 -上午 11:13
 **/
public enum siginEnum {
    /**
     * 获取所有贴吧信息出错
     */
    SIGNIN_ERROR_TEIBA_IS_NULL("签到失败!并未获取到任何贴" +
            "息.",501,false),
    /**
     * cookie为空!
     */
    SIGNIN_ERROR_COOKIE_IS_NULL("签到失败!并未获取到任何贴吧信息.",501,false),
    SIGNIN_SUUCCESS("签到成功!",200,true),

    ;
    private String message;
    private int code;
    private boolean stats;

    siginEnum(String message) {
        this.message = message;
    }

    siginEnum(String message, int code, boolean stats) {
        this.message = message;
        this.code = code;
        this.stats = stats;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public boolean isStats() {
        return stats;
    }
}
