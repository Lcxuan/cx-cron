package com.cxcron.common.utils;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 客户端 IP 获取工具。
 */
public final class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final List<String> IP_HEADERS = List.of(
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP"
    );

    private IpUtils() {
    }

    /**
     * 获取客户端 IP。
     *
     * <p>按常见代理头顺序读取，取不到时回退到 {@link HttpServletRequest#getRemoteAddr()}。
     * 代理头可能被客户端伪造，生产环境应确保应用只通过可信代理访问，并在代理层覆盖转发头。</p>
     *
     * @param request HTTP 请求
     * @return 客户端 IP，无法获取时返回空字符串
     */
    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        for (String header : IP_HEADERS) {
            String ip = resolveFirstIp(request.getHeader(header));
            if (StrUtil.isNotBlank(ip)) {
                return normalize(ip);
            }
        }
        return normalize(request.getRemoteAddr());
    }

    /**
     * 从逗号分隔的代理头中取第一个有效 IP。
     */
    private static String resolveFirstIp(String headerValue) {
        if (StrUtil.isBlank(headerValue)) {
            return null;
        }
        for (String candidate : StrUtil.splitTrim(headerValue, ',')) {
            if (StrUtil.isNotBlank(candidate) && !UNKNOWN.equalsIgnoreCase(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * 统一本机回环地址。
     */
    private static String normalize(String ip) {
        if (StrUtil.isBlank(ip)) {
            return "";
        }
        String value = ip.trim();
        if ("0:0:0:0:0:0:0:1".equals(value) || "::1".equals(value)) {
            return LOCALHOST_IPV4;
        }
        return value;
    }
}
