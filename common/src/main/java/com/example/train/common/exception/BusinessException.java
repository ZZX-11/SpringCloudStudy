package com.example.train.common.exception;

public class BusinessException extends RuntimeException {

    private BusinessExceptionEnum e;

    public BusinessException(BusinessExceptionEnum e) {
        this.e = e;
    }

    public BusinessExceptionEnum getE() {
        return e;
    }

    public void setE(BusinessExceptionEnum e) {
        this.e = e;
    }

    /**
     * 不写入堆栈信息，提高性能
     */
//   通过重写这个方法，减少在出现异常时的堆栈信息输出，即避免它递归更深层的堆栈
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
