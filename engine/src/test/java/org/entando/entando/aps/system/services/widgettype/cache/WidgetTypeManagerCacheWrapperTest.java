package org.entando.entando.aps.system.services.widgettype.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.entando.entando.aps.system.services.cache.CustomConcurrentMapCache;
import org.entando.entando.aps.system.services.widgettype.IWidgetTypeDAO;
import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class WidgetTypeManagerCacheWrapperTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private IWidgetTypeDAO widgetTypeDAO;

    @InjectMocks
    private WidgetTypeManagerCacheWrapper cacheWrapper;

    @Test
    void testCacheConcurrencyIssue() throws Exception {

        CustomConcurrentMapCache cache = new CustomConcurrentMapCache("Entando_WidgetTypeManager", true);
        Mockito.when(cacheManager.getCache("Entando_WidgetTypeManager")).thenReturn(cache);

        WidgetType loginFormType = new WidgetType();
        loginFormType.setCode("login_form");

        Map<String, WidgetType> widgetTypesMap = new HashMap<>();
        widgetTypesMap.put("login_form", loginFormType);

        Mockito.when(widgetTypeDAO.loadWidgetTypes()).thenReturn(widgetTypesMap);
        cacheWrapper.initCache(widgetTypeDAO);

        Assertions.assertEquals(1, cacheWrapper.getWidgetTypes().size());
        Assertions.assertEquals(1, ((List<String>) cache.get("WidgetTypeManager_codes").get()).size());
        Assertions.assertNotNull(cache.get("WidgetTypeManager_type_login_form"));

        // Simulates an unlucky concurrent removal where the value has been removed from the cache but its cached code is not.
        cache.evict("WidgetTypeManager_type_login_form");

        Assertions.assertEquals(0, cacheWrapper.getWidgetTypes().size());
        Assertions.assertEquals(1, ((List<String>) cache.get("WidgetTypeManager_codes").get()).size());
        Assertions.assertNull(cache.get("WidgetTypeManager_type_login_form"));
    }
}
