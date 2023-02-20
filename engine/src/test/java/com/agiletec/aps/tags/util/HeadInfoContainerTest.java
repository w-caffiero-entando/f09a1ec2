package com.agiletec.aps.tags.util;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HeadInfoContainerTest {

    @Test
    void testAddInfo() {
        HeadInfoContainer headInfoContainer = new HeadInfoContainer();
        headInfoContainer.addInfo("key1", "value1");
        headInfoContainer.addInfo("key1", "value2");
        headInfoContainer.addInfo("key1", "value2");
        headInfoContainer.addInfo("key2", "value3");

        List<String> info1 = headInfoContainer.getInfos("key1");
        Assertions.assertEquals(2, info1.size());
        Assertions.assertEquals("value1", info1.get(0));
        Assertions.assertEquals("value2", info1.get(1));

        List<String> info2 = headInfoContainer.getInfos("key2");
        Assertions.assertEquals(1, info2.size());
        Assertions.assertEquals("value3", info2.get(0));
    }
}
