package com.foolox.game.common.result;

/**
 * comment:
 *
 * @author: lipengfei
 * @date: 11/05/2019
 */
public class CodeMessage {
    private int code;
    private String message;

    //通用异常 500100
    public static CodeMessage SUCCESS = new CodeMessage(0,"sucess");
    public static CodeMessage SERVER_ERROR = new CodeMessage(500100,"服务端异常");
    public static CodeMessage VALIDATE_ERROR = new CodeMessage(500101,"参数校验异常：%s");

    //登录模块 500200
    public static CodeMessage LOGIN_TOKEN_EXPIRED = new CodeMessage(500210,"Token已过期");
    public static CodeMessage LOGIN_TOKEN_INCORRECT = new CodeMessage(500211,"无效Token");
    public static CodeMessage LOGIN_USER_NOT_EXIST = new CodeMessage(500212,"用户不存在");
    public static CodeMessage LOGIN_PASSWORD_INCORRECT = new CodeMessage(500213,"用户名或密码错误");
    public static CodeMessage LOGIN_STATE_INCORRECT = new CodeMessage(500214,"用户被冻结");
    /*public static CodeMessage SESSION_ERROR = new CodeMessage(500210,"Session不存在或已失效");
    public static CodeMessage LOGIN_FIELD_EMPT_ERROR = new CodeMessage(500211,"用户名或密码为空");
    public static CodeMessage LOGIN_MOBILE_FORMAT_ERROR = new CodeMessage(500212,"手机号格式错误");
    public static CodeMessage LOGIN_MOBILE_NOT_EXIST_ERROR = new CodeMessage(500213,"手机号码不存在");
    public static CodeMessage LOGIN_INCORRECT_PASSWORD_ERROR = new CodeMessage(500214,"密码不匹配");*/

    //商品模块 500300

    //订单模块 500400

    //秒杀模块 500500
    public static CodeMessage SECKILL_GOODS_INCORRECT_ERROR = new CodeMessage(500500,"该商品没有参加秒杀活动");
    public static CodeMessage SECKILL_GOODS_OVER_ERROR = new CodeMessage(500501,"秒杀完毕");
    public static CodeMessage SECKILL_DUPLICATE_ERROR = new CodeMessage(500502,"你已经抢到该商品，不要贪心哦");

    private CodeMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public CodeMessage fillArgs(Object... args) {
//        int code = this.code;
        this.message = String.format(this.message,args);
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CodeMessage{");
        sb.append("code=").append(code);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
