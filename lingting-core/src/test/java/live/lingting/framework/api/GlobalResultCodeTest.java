package live.lingting.framework.api;

import live.lingting.framework.exception.BizException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author lingting 2024-01-25 16:02
 */
class GlobalResultCodeTest {

    @Test
    void test() {
        GlobalResultCode resultCode = GlobalResultCode.SERVER_ERROR;
        R<Object> r = R.failed(resultCode);
        assertEquals(resultCode.getCode(), r.code());
        assertEquals(resultCode.getMessage(), r.message());
        String message = "testMessage";
        ResultCode with = resultCode.with(message);
        assertEquals(message, with.getMessage());
        assertThrows(BizException.class, with::throwException);
        BizException exception = with.toException();
        assertEquals(with.getMessage(), exception.getMessage());
        assertEquals(with.getCode(), exception.getCode());
    }

}