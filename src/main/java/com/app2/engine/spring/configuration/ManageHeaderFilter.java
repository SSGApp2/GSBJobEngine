package com.app2.engine.spring.configuration;

import com.app2.engine.util.UserMapDetails;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
public class ManageHeaderFilter implements Filter {

    @Autowired
    UserMapDetails userMapDetails;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            //ManageHeader
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest();
            userMapDetails.setMapUserDetails(request);
            String username = userMapDetails.getUsername();
            String roleStr = userMapDetails.getRole();
            //set param to log4j
            MDC.put("Username", username);
            MDC.put("Role", roleStr);
        } catch (Exception e) {
            //no auth
        }
        chain.doFilter(req, res);
    }
}
