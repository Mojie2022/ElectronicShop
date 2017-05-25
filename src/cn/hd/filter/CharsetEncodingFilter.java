package cn.hd.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Created by xzl on 2017/5/24.
 */
@WebFilter(filterName = "*")
public class CharsetEncodingFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        //处理post提交数据包含中文时出现乱码问题
        req.setCharacterEncoding("utf-8");
        //处理响应客户端数据包含中文时出现乱码问题
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
