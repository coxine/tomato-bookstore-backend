package tg.cos.tomatomall.configure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tg.cos.tomatomall.util.TokenUtil;

import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    TokenUtil tokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        // 仅对 POST /api/accounts 进行放行
        if ("POST".equals(method) && "/api/accounts".equals(uri)) {
            return true;
        }
        String token = request.getHeader("token");
        if (token != null && tokenUtil.verifyToken(token)) {
            request.getSession().setAttribute("currentUser",tokenUtil.getAccount(token));
            return true;
        }else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"NOT LOGIN!!! Token is invalid - Method: " + method + ", URI: " + uri + "\"}");
            System.err.println("NOT LOGIN!!! Token is invalid - Method: " + method + ", URI: " + uri);
            return false;
        }
    }

}
