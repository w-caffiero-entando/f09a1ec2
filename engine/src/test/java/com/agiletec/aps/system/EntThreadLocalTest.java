/*
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.agiletec.aps.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EntThreadLocalTest {

    @Test
    void reqCtxThreadShouldInitInsertGet() {
        final String keytest = "keytest";
        final String valuetest = "valuetest";
        EntThreadLocal.clear();
        EntThreadLocal.set(keytest, valuetest);
        EntThreadLocal.clear();
        Assertions.assertNull(EntThreadLocal.get(keytest));
    }

    @Test
    void reqCtxThreadShouldRemoveOrDestroy() {
        final String keytest = "keytest";
        final String valuetest = "valuetest";
        EntThreadLocal.clear();
        EntThreadLocal.set(keytest, valuetest);
        EntThreadLocal.remove(keytest);
        Assertions.assertNull(EntThreadLocal.get(keytest));

        EntThreadLocal.clear();
        EntThreadLocal.set(keytest, valuetest);
        EntThreadLocal.destroy();
        Assertions.assertNull(EntThreadLocal.get(keytest));

        EntThreadLocal.set(keytest, valuetest);
        Assertions.assertEquals(valuetest, EntThreadLocal.get(keytest));

    }
}
