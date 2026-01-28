package com.radarecom.radarecom.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.radarecom.radarecom.constants.Constants.TRANSID;

@Component
@AllArgsConstructor
@Order(1)
public class LogFilter extends OncePerRequestFilter {

    private static final Logger log = LogManager.getLogger(LogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String transId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        ThreadContext.put(TRANSID, transId);
        ThreadContext.put("path", request.getRequestURI());
        ThreadContext.put("method", request.getMethod());

        startLog();

        try {
            filterChain.doFilter(request, response);
        } finally {
            endLog(response, startTime);
            ThreadContext.clearAll();
        }

    }

    private void startLog(){
        ThreadContext.put("PTR", "START");
        log.info("Request START");
        ThreadContext.remove("PTR");
    }

    private void endLog(HttpServletResponse response, long startTime){
        long elapsed = System.currentTimeMillis() - startTime;
        ThreadContext.put("duration", String.valueOf(elapsed));
        ThreadContext.put("status", String.valueOf(response.getStatus()));
        ThreadContext.put("PTR", "END");
        log.info("Request END");
    }

}
