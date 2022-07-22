package com.jia.filter;

import com.alibaba.fastjson.JSON;
import com.jia.common.BaseContext;
import com.jia.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class loginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1、获取本次请求的URI

        String url = request.getRequestURI();
        log.info("拦截到{}",url);
        //2、判断本次请求是否需要处理
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/common/**",
                "/user/login",
                //不过滤静态资源
                "/backend/**",
                "/front/**",

        };
        //3、如果不需要处理，则直接放行
        boolean check = check(urls, url);
        if (check){
            filterChain.doFilter(request,response);
            return;
        }

        //4、判断登录状态，如果已登录，则直接放行

        if (request.getSession().getAttribute("employee")!= null){

            Long empId =(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        if (request.getSession().getAttribute("user")!= null){

            Long userId =(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        //5、如果未登录则返回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;



    }
    public boolean check(String[] urls,String url){
        for (String s : urls) {
            boolean match = PATH_MATCHER.match(s, url);
            if (match){
                return true;
            }
        }return false;
    }
}
