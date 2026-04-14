/*
 * All rights Reserved, Designed By Jensen
 * @Title:  ReviewException.java
 * @Package com.jensen.codereview.exception
 * @author: Jensen
 * @date:   2026/4/14 11:00
 * @version V1.0
 */
package com.jensen.codereview.exception;

/**
 * @ClassName ReviewException
 * @Description 代码审查异常
 * @Author Jensen
 * @Date 2026/4/14 11:00
 */
public class ReviewException extends RuntimeException {

    public ReviewException(String message) {
        super(message);
    }

    public ReviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
