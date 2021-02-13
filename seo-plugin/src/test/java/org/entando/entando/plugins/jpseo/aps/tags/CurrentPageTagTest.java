package org.entando.entando.plugins.jpseo.aps.tags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.page.IPage;
import org.entando.entando.plugins.jpseo.aps.system.services.page.PageMetatag;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CurrentPageTagTest {

    CurrentPageTag currentPageTag = new CurrentPageTag();

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

        assertEquals("friendly_code_en", currentPageTag.getValue());

    }

}
