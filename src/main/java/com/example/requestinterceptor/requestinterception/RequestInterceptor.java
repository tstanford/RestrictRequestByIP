package com.example.requestinterceptor.requestinterception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Logger;

public class RequestInterceptor implements HandlerInterceptor {

    Logger logger = Logger.getLogger(RequestInterceptor.class.getName());

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            Method method = ((HandlerMethod)handler).getMethod();
            if(method.isAnnotationPresent(RestrictToIp.class)) {
                RestrictToIp methodAnnotation = method.getAnnotation(RestrictToIp.class);
                if (methodAnnotation != null) {
                    logger.info("RestrictToIp annotation for on request method");
                    String ipAddressForRequest = getIpAddressForRequest(request);
                    boolean ipAllowed = Arrays.stream(methodAnnotation.ipAddresses())
                            .filter(ip -> ip.equals(ipAddressForRequest))
                            .findAny()
                            .isPresent();

                    if (!ipAllowed) {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                    }

                    logger.info(String.format("ip %s is %sallowed", ipAddressForRequest, ipAllowed ? "" : "not "));

                    return ipAllowed;
                }
            }
        }
        return true;
    }

    private String getIpAddressForRequest(HttpServletRequest request) {
        // https://stackoverflow.com/questions/22877350/how-to-extract-ip-address-in-spring-mvc-controller-get-call
        for (String header: IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                String ip = ipList.split(",")[0];
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}
