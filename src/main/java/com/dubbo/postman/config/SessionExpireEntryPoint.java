/*
 * MIT License
 *
 * Copyright (c) 2019 everythingbest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dubbo.postman.config;

import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author everythingbest
 * cas拦截点配置
 * 因为是前后端分离,在前端检测登陆是否失效,需要在每次进行ajax请求的时候带上ajax-header,
 * 在session过期的时候,会执行这个类的commence方法{@link AuthenticationEntryPoint}
 *  Used by {@link ExceptionTranslationFilter} to commence an authentication scheme.
 */
public class SessionExpireEntryPoint implements AuthenticationEntryPoint {

    static final String AJAX_TYPE = "ajax-type";

    static final String AJAX_HEADER = "ajax-header";

    final CasAuthenticationEntryPoint casAuthenticationEntryPoint;

    SessionExpireEntryPoint(final CasAuthenticationEntryPoint casAuthenticationEntryPoint){

        this.casAuthenticationEntryPoint = casAuthenticationEntryPoint;
    }

    /**
     * 在cas授权失败的时候会进入这个方法
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        //判断请求类型是否是ajax
        if(request.getHeader(AJAX_TYPE) != null || request.getParameter(AJAX_TYPE)!=null){

            //设置过期标识,让前端js进行处理
            response.setHeader(AJAX_HEADER,"time-out");

            try {
                //直接返回错误信息,前端js进行拦截
                response.sendError(HttpServletResponse.SC_OK,"session已经过期");

            } catch (IOException e) {
            }
        }else{

            casAuthenticationEntryPoint.commence(request,response,authException);
        }
    }
}
