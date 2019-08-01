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

package com.dubbo.postman.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author everythingbest powered by WebApiResonse
 * @param <T>
 */
public class WebApiRspDto<T>{

    private static final Logger logger = LoggerFactory.getLogger(WebApiRspDto.class);

    public static final int SUCCESS_CODE = 0;
    public static final int ERROR_CODE = 1;

    private int code;
    private String error;
    private T data;

    private long elapse;

    /**
     * 是否需要重试。只有在 code != 0，也就是说有错的时候才有意义。
     * 为真时表示一定要重试，为假时表示一定不要重试，为空时由调用方自行决定。
     */
    private Boolean isNeedRetry;


    public static <T> WebApiRspDto<T> success(T data) {
        WebApiRspDto<T> response = new WebApiRspDto<>();
        response.setCode(SUCCESS_CODE);
        response.setData(data);
        return response;
    }

    public static <T> WebApiRspDto<T> success(T data, long elapse) {
        WebApiRspDto<T> response = new WebApiRspDto<>();
        response.setCode(SUCCESS_CODE);
        response.setData(data);
        response.setElapse(elapse);
        return response;
    }

    public static <T> WebApiRspDto<T> error(String errorMessage) {
        return WebApiRspDto.error(errorMessage, ERROR_CODE);
    }

    public static <T> WebApiRspDto<T> error(String errorMessage, int errorCode) {
        return WebApiRspDto.error(errorMessage, errorCode, null);
    }

    public static <T> WebApiRspDto<T> error(String errorMessage, Boolean isNeedRetry) {
        return WebApiRspDto.error(errorMessage, ERROR_CODE, isNeedRetry);
    }

    public static <T> WebApiRspDto<T> error(String errorMessage, int errorCode, Boolean isNeedRetry) {
        WebApiRspDto<T> response = new WebApiRspDto<>();
        response.setCode(errorCode);
        response.setError(errorMessage);
        response.setNeedRetry(isNeedRetry);
        return response;
    }

    public static <T> WebApiRspDto<T> asProcess(WebApiRspDto.Procedure<T> procedure) {
        return asProcess(procedure, Exception::toString);
    }

    public static <T> WebApiRspDto<T> asProcess(WebApiRspDto.Procedure<T> procedure, Function<Exception, String> exceptionHandler) {
        try {
            return success(procedure.apply());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return error(exceptionHandler.apply(e));
        }
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getNeedRetry() {
        return isNeedRetry;
    }

    public void setNeedRetry(Boolean needRetry) {
        isNeedRetry = needRetry;
    }

    public long getElapse() {
        return elapse;
    }

    public void setElapse(long elapse) {
        this.elapse = elapse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        WebApiRspDto<?> that = (WebApiRspDto<?>) o;

        if (code != that.code) {return false;}
        if (error != null ? !error.equals(that.error) : that.error != null) {return false;}
        return data.equals(that.data);

    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + data.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "WebApiResponse{" +
                "code=" + code +
                "elapse=" + elapse +
                ", error='" + error + '\'' +
                ", data=" + data +
                '}';
    }

    @FunctionalInterface
    public interface Procedure<T> {
        T apply() throws Exception;
    }
}
