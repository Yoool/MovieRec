package com.work.movierec.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.work.movierec.result.CodeMsg;
import com.work.movierec.result.Result;

@ControllerAdvice
@ResponseBody
//ȫ���쳣������
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e) {
        //��ӡ�쳣��ϸ��Ϣ
        e.printStackTrace();
        if (e instanceof GlobalException)
            return Result.error(((GlobalException) e).getcodeMsg());
//        } else if (e instanceof BindException) {
//            BindException ex = (BindException) e;
//            List<ObjectError> errors = ex.getAllErrors();
//            String msg = errors.get(0).getDefaultMessage();
//            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
//        } else {
//            return Result.error(CodeMsg.SERVER_ERROR);
//        }
        return Result.error(CodeMsg.SERVER_ERROR);
    }
}
