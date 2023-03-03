package com.agiletec.aps.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApsSystemUtilsTest {

    @Test
    void testLog(){
        Assertions.assertTrue(ApsSystemUtils.directStdoutTrace("test print",true));
    }
}
