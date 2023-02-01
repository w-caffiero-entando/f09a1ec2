package org.entando.entando.plugins.jpseo.aps.system.services.mapping.cache;

import com.agiletec.aps.system.services.page.IPageManager;
import com.agiletec.aps.system.services.page.Page;
import com.agiletec.aps.system.services.page.PageMetadata;
import com.agiletec.aps.util.ApsProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.entando.entando.plugins.jpseo.aps.system.services.mapping.ISeoMappingDAO;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;

@ExtendWith(MockitoExtension.class)
class SeoMappingCacheWrapperTest {

    private static final String PAGE_CODE = "page_code";

    @Mock
    private IPageManager pageManager;
    @Mock
    private ISeoMappingDAO seoMappingDAO;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;

    @InjectMocks
    private SeoMappingCacheWrapper seoMappingCacheWrapper;

    @BeforeEach
    void setUp() {
        seoMappingCacheWrapper.setSpringCacheManager(cacheManager);
        Mockito.when(cacheManager.getCache(ISeoMappingCacheWrapper.SEO_MAPPER_CACHE_NAME)).thenReturn(cache);
    }

    @Test
    void shouldCreateDraftPagesMappingIgnoringDefaultPageMetadata() throws Exception {
        Map<String, String> mapping = testCreateDraftPagesMapping(new PageMetadata());
        Assertions.assertTrue(mapping.isEmpty());
    }

    @Test
    void shouldCreateDraftPagesMappingIgnoringSeoPageMetadataWithoutFriendlyCodes() throws Exception {
        Map<String, String> mapping = testCreateDraftPagesMapping(new SeoPageMetadata());
        Assertions.assertTrue(mapping.isEmpty());
    }

    @Test
    void shouldCreateDraftPagesMappingWithSeoPageMetadataAndValidFriendlyCodes() throws Exception {
        ApsProperties friendlyCodes = new ApsProperties();
        friendlyCodes.setProperty("en", "code_en");
        friendlyCodes.setProperty("it", "code_it");
        SeoPageMetadata seoPageMetadata = new SeoPageMetadata();
        seoPageMetadata.setFriendlyCodes(friendlyCodes);

        Map<String, String> mapping = testCreateDraftPagesMapping(seoPageMetadata);

        Assertions.assertEquals(2, mapping.size());
        Assertions.assertEquals(PAGE_CODE, mapping.get("code_en"));
        Assertions.assertEquals(PAGE_CODE, mapping.get("code_it"));
    }

    @Test
    void shouldUpdateDraftPageReferences() {
        Map<String, String> oldMapping = Map.of("old_code_en", PAGE_CODE, "old_code_it", PAGE_CODE);
        Mockito.when(cache.get(ISeoMappingCacheWrapper.DRAFT_PAGES_MAPPING))
                .thenReturn(new SimpleValueWrapper(oldMapping));

        seoMappingCacheWrapper.updateDraftPageReferences(List.of("new_code_en", "new_code_it"), PAGE_CODE);

        ArgumentCaptor<Map<String, String>> mappingCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(cache).put(Mockito.eq(ISeoMappingCacheWrapper.DRAFT_PAGES_MAPPING), mappingCaptor.capture());
        Map<String, String> newMapping = mappingCaptor.getValue();

        Assertions.assertEquals(2, newMapping.size());
        Assertions.assertEquals(PAGE_CODE, newMapping.get("new_code_en"));
        Assertions.assertEquals(PAGE_CODE, newMapping.get("new_code_it"));
    }

    private Map<String, String> testCreateDraftPagesMapping(PageMetadata pageMetadata) throws Exception {
        Mockito.when(seoMappingDAO.loadMapping()).thenReturn(new HashMap<>());

        Page draftRoot = new Page();
        draftRoot.setCode(PAGE_CODE);
        draftRoot.setMetadata(pageMetadata);
        Mockito.when(pageManager.getDraftRoot()).thenReturn(draftRoot);

        seoMappingCacheWrapper.initCache(pageManager, seoMappingDAO, true);

        ArgumentCaptor<Map<String, String>> mappingCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(cache).put(Mockito.eq(ISeoMappingCacheWrapper.DRAFT_PAGES_MAPPING), mappingCaptor.capture());
        return mappingCaptor.getValue();
    }
}
