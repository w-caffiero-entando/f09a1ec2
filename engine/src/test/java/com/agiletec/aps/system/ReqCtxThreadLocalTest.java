package com.agiletec.aps.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReqCtxThreadLocalTest {

    @Test
    void reqCtxThreadShouldInitInsertGet() {
        final String keytest = "keytest";
        final String valuetest = "valuetets";
        ReqCtxThreadLocal.init();
        ReqCtxThreadLocal.set(keytest, valuetest);
        ReqCtxThreadLocal.init();
        Assertions.assertNull(ReqCtxThreadLocal.get(keytest));
    }

    @Test
    void reqCtxThreadShouldRemoveOrDestroy() {
        final String keytest = "keytest";
        final String valuetest = "valuetets";
        ReqCtxThreadLocal.init();
        ReqCtxThreadLocal.set(keytest, valuetest);
        ReqCtxThreadLocal.remove(keytest);
        Assertions.assertNull(ReqCtxThreadLocal.get(keytest));

        ReqCtxThreadLocal.init();
        ReqCtxThreadLocal.set(keytest, valuetest);
        ReqCtxThreadLocal.destroy();
        Assertions.assertNull(ReqCtxThreadLocal.get(keytest));

        ReqCtxThreadLocal.set(keytest, valuetest);
        Assertions.assertEquals(valuetest, ReqCtxThreadLocal.get(keytest));

    }
}
