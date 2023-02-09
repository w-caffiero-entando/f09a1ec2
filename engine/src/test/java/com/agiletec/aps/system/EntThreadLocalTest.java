package com.agiletec.aps.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EntThreadLocalTest {

    @Test
    void reqCtxThreadShouldInitInsertGet() {
        final String keytest = "keytest";
        final String valuetest = "valuetets";
        EntThreadLocal.initOrClear();
        EntThreadLocal.set(keytest, valuetest);
        EntThreadLocal.initOrClear();
        Assertions.assertNull(EntThreadLocal.get(keytest));
    }

    @Test
    void reqCtxThreadShouldRemoveOrDestroy() {
        final String keytest = "keytest";
        final String valuetest = "valuetets";
        EntThreadLocal.initOrClear();
        EntThreadLocal.set(keytest, valuetest);
        EntThreadLocal.remove(keytest);
        Assertions.assertNull(EntThreadLocal.get(keytest));

        EntThreadLocal.initOrClear();
        EntThreadLocal.set(keytest, valuetest);
        EntThreadLocal.destroy();
        Assertions.assertNull(EntThreadLocal.get(keytest));

        EntThreadLocal.set(keytest, valuetest);
        Assertions.assertEquals(valuetest, EntThreadLocal.get(keytest));

    }
}
