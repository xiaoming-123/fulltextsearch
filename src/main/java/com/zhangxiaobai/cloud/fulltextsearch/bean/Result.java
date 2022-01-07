package com.zhangxiaobai.cloud.fulltextsearch.bean;

public class Result {

    private final String message;

    private final Object data;

    private final Integer status;

    private final Boolean success;

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public Integer getStatus() {
        return status;
    }

    public Boolean getSuccess() {
        return success;
    }

    private Result(String message, Object data, Integer status, Boolean success) {
        this.message = message;
        this.data = data;
        this.status = status;
        this.success = success;
    }

    public static Result SUCCESS(Object data) throws Exception {
        return new Result("", data, 200, true);
    }

    public static Result FAILURE(String message) {
        return new Result(message, null, 200, false);
    }

    public static Result SUCCESS = new Result("", null, 200, true);

    public static Result FAILURE = new Result("", null, 200, false);

}
