package tg.cos.tomatomall.configure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tg.cos.tomatomall.util.TokenUtil;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    TokenUtil tokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
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
            throw new RuntimeException("NOT LOGIN!!! Token is invalid");
        }
    }

}
