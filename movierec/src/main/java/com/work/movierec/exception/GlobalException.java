package com.work.movierec.exception;

import com.work.movierec.result.CodeMsg;

public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private CodeMsg codeMsg;

    public CodeMsg getcodeMsg() {
        return codeMsg;
    }

    public void setcodeMsg(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }

    public GlobalException(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }

}
