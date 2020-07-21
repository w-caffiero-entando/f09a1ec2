package org.entando.entando.plugins.jpseo.web.page.model;

import java.util.Objects;
import org.entando.entando.web.page.model.PageRequest;

public class SeoPageRequest extends PageRequest {

    private SeoData seoData;


    public SeoData getSeoData() {
        return seoData;
    }

    public void setSeoData(SeoData seoData) {
        this.seoData = seoData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SeoPageRequest that = (SeoPageRequest) o;
        return Objects.equals(seoData, that.seoData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seoData);
    }

    @Override
    public String toString() {
        return "SeoPageRequest{" +
                "seo=" + seoData +
                '}';
    }
}
