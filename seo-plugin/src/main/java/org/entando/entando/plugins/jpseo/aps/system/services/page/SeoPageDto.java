package org.entando.entando.plugins.jpseo.aps.system.services.page;

import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.plugins.jpseo.web.page.model.SeoData;

public class SeoPageDto extends PageDto {
    private SeoData seoData;

    public SeoData getSeoData() {
        return seoData;
    }

    public void setSeoData(SeoData seoData) {
        this.seoData = seoData;
    }

    @Override
    public String toString() {
        return "SeoPageDto{" +
                "seoData=" + seoData +
                '}';
    }
}
