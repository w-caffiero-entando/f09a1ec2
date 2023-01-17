package org.entando.entando.aps.system.services.controller.executor;

import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.util.ApsProperties;
import org.entando.entando.aps.system.services.controller.executor.AbstractWidgetExecutorService.CurrentLogicWidget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CurrentLogicWidgetTest {

    @Test
    void testEquals() {
        CurrentLogicWidget w1 = getCurrentLogicWidget("code", "value");
        CurrentLogicWidget w2 = getCurrentLogicWidget("code", "value");
        Assertions.assertEquals(w1, w2);
        Assertions.assertEquals(w1.hashCode(), w2.hashCode());
    }

    @Test
    void testEqualsSameObject() {
        CurrentLogicWidget w = getCurrentLogicWidget("code", "value");
        Assertions.assertEquals(w, w);
    }

    @Test
    void testEqualsDifferentClass() {
        CurrentLogicWidget w = getCurrentLogicWidget("code", "value");
        Assertions.assertNotEquals(w, new Widget());
    }

    @Test
    void testEqualsDifferentCode() {
        CurrentLogicWidget w1 = getCurrentLogicWidget("code1", "value");
        CurrentLogicWidget w2 = getCurrentLogicWidget("code2", "value");
        Assertions.assertNotEquals(w1, w2);
    }

    @Test
    void testEqualsDifferentParam() {
        CurrentLogicWidget w1 = getCurrentLogicWidget("code", "value1");
        CurrentLogicWidget w2 = getCurrentLogicWidget("code", "value2");
        Assertions.assertNotEquals(w1, w2);
    }

    @Test
    void testEqualsDifferentConfig() {
        CurrentLogicWidget w1 = getCurrentLogicWidget("code", "value");
        CurrentLogicWidget w2 = getCurrentLogicWidget("code", "value");
        ApsProperties config1 = new ApsProperties();
        config1.setProperty("key1", "value1");
        ApsProperties config2 = new ApsProperties();
        config2.setProperty("key2", "value2");
        w1.setConfig(config1);
        w2.setConfig(config2);
        Assertions.assertNotEquals(w1, w2);
    }

    private CurrentLogicWidget getCurrentLogicWidget(String widgetCode, String param) {
        Widget concrete = new Widget();
        concrete.setTypeCode(widgetCode);
        ApsProperties params = new ApsProperties();
        params.setProperty("key", param);
        return new CurrentLogicWidget(concrete, params);
    }
}
