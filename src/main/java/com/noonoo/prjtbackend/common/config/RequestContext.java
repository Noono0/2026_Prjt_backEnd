package com.noonoo.prjtbackend.common.config;

public class RequestContext {

    private static final ThreadLocal<String> CLIENT_IP = new ThreadLocal<>();
    private static final ThreadLocal<String> LOGIN_MEMBER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> LOGIN_MEMBER_SEQ = new ThreadLocal<>();

    private RequestContext() {}

    public static void setClientIp(String clientIp) {
        CLIENT_IP.set(clientIp);
    }

    public static String getClientIp() {
        return CLIENT_IP.get();
    }

    public static void setLoginMemberId(String loginMemberId) {
        LOGIN_MEMBER_ID.set(loginMemberId);
    }

    public static String getLoginMemberId() {
        return LOGIN_MEMBER_ID.get();
    }

    public static void setLoginMemberSeq(Long loginMemberSeq) {
        LOGIN_MEMBER_SEQ.set(loginMemberSeq);
    }

    public static Long getLoginMemberSeq() {
        return LOGIN_MEMBER_SEQ.get();
    }

    public static void clear() {
        CLIENT_IP.remove();
        LOGIN_MEMBER_ID.remove();
        LOGIN_MEMBER_SEQ.remove();
    }
}
