package org.entando.entando.plugins.jpseo.aps.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import org.entando.entando.plugins.jpseo.aps.system.services.page.PageMetatag;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageMetadata;
import org.entando.entando.plugins.jpseo.web.page.model.SeoDataByLang;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CurrentPageTagTest {

    private CurrentPageTag currentPageTag = new CurrentPageTag();

    @Test
    public void extractPageFriendlyCodesTest() {
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

        SeoDataByLang seoData = new SeoDataByLang();
        SeoDataByLang seoData2 = new SeoDataByLang();
        seoData.setInheritFriendlyCodeFromDefaultLang(true);
        seoData2.setInheritFriendlyCodeFromDefaultLang(true);
        seoData.setFriendlyCode("friendly_code_en");
        seoData2.setFriendlyCode("friendly_code_en");

        assertEquals(seoData.hashCode(), seoData2.hashCode());
        assertEquals(seoData.toString(), seoData2.toString());
        assertEquals(seoData, seoData2);

        assertEquals("friendly_code_en", currentPageTag.getValue());
    }

}
