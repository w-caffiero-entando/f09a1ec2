package org.entando.entando.plugins.jpseo.aps.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import org.entando.entando.plugins.jpseo.aps.system.services.page.PageMetatag;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageMetadata;
import org.entando.entando.plugins.jpseo.web.page.model.SeoData;
import org.entando.entando.plugins.jpseo.web.page.model.SeoDataByLang;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CurrentPageTagTest {

    private CurrentPageTag currentPageTag = new CurrentPageTag();

    @Test
    void extractPageFriendlyCodesTest() {
        Lang currentLang = new Lang();
        currentLang.setCode("en");
        currentLang.setDescr("English");
        IPage currentPage = Mockito.mock(IPage.class);
        currentPageTag.extractPageFriendlyCodes(currentPage, currentLang);

        SeoPageMetadata pageMetadata = new SeoPageMetadata();
        PageMetatag pageMetadata2 = new PageMetatag("en", "friendlyCode", "friendly_code_en");
        pageMetadata.getFriendlyCodes().put("en", pageMetadata2);
        when(currentPage.getMetadata()).thenReturn(pageMetadata);

        currentPageTag.extractPageFriendlyCodes(currentPage, currentLang);

        SeoDataByLang seoDataByLang = new SeoDataByLang();
        SeoDataByLang seoDataByLang2 = new SeoDataByLang();
        seoDataByLang.setInheritFriendlyCodeFromDefaultLang(true);
        seoDataByLang2.setInheritFriendlyCodeFromDefaultLang(true);
        seoDataByLang.setFriendlyCode("friendly_code_en");
        seoDataByLang2.setFriendlyCode("friendly_code_en");

        assertEquals(seoDataByLang.hashCode(), seoDataByLang2.hashCode());
        assertEquals(seoDataByLang.toString(), seoDataByLang2.toString());
        assertEquals(seoDataByLang, seoDataByLang2);

        SeoData seoData = new SeoData();
        seoData.getSeoDataByLang().put("en", seoDataByLang);
        assertEquals(seoData.hashCode(), seoData.hashCode());

        assertEquals("friendly_code_en", currentPageTag.getValue());
    }

}
