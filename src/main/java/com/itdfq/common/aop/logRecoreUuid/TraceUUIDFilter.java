package com.itdfq.common.aop.logRecoreUuid;

import com.itdfq.common.utils.StrUtils;
import com.itdfq.common.utils.UuidUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 设置uuid
 * @author dfq 2025/3/26 14:12
 * @implNote
 */
@Component
@Order(1)
public class TraceUUIDFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request1, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) request1;
        String traceUuid = request.getHeader("trace_uuid");
        if (StrUtils.isNotBlank(traceUuid)) {
            MDC.put("trace_uuid", traceUuid);
        } else {
            MDC.put("trace_uuid", UuidUtils.generate().toString());
        }
        chain.doFilter(request, response);
    }


}
