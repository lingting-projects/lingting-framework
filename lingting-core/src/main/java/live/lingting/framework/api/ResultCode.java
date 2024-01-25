package live.lingting.framework.api;


import live.lingting.framework.exception.BizException;

/**
 * @author lingting 2022/9/19 13:55
 */
public interface ResultCode {

    /**
     * 返回的code
     *
     * @return int
     */
    Integer getCode();

    /**
     * 返回消息
     *
     * @return string
     */
    String getMessage();

    /**
     * 更新消息
     */
    default ResultCode with(String message) {
        Integer code = getCode();
        return new ResultCode() {
            @Override
            public Integer getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

    /**
     * 组装消息
     *
     * @return 返回一个新的组装消息后的对象
     */
    default ResultCode format(Object... args) {
        String message = String.format(getMessage(), args);
        return with(message);
    }

    /**
     * 转异常
     */
    default BizException toException() {
        return new BizException(this);
    }

    default BizException toException(Exception e) {
        return new BizException(this, e);
    }

    /**
     * 抛出异常
     */
    default void throwException() throws BizException {
        throw toException();
    }

    default void throwException(Exception e) throws BizException {
        throw toException(e);
    }

}
