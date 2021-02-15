package org.entando.entando.plugins.jpseo.web.page.model;

import java.util.Map;
import java.util.Objects;

public class SeoData {

    private Map<String, SeoDataByLang> SeoDataByLang;
    private Boolean useExtraDescriptions = false;
    private Boolean useExtraTitles = false;

    public Map<String, SeoDataByLang> getSeoDataByLang() {
        return SeoDataByLang;
    }

    public SeoData() {
    }

    public void setSeoDataByLang(
            Map<String, SeoDataByLang> SeoDataByLang) {
        this.SeoDataByLang = SeoDataByLang;
    }

    public Boolean getUseExtraDescriptions() {
        return useExtraDescriptions;
    }

    public void setUseExtraDescriptions(Boolean useExtraDescriptions) {
        this.useExtraDescriptions = useExtraDescriptions;
    }

    public Boolean getUseExtraTitles() {
        return useExtraTitles;
    }

    public void setUseExtraTitles(Boolean useExtraTitles) {
        this.useExtraTitles = useExtraTitles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SeoData that = (SeoData) o;
        return Objects.equals(SeoDataByLang, that.SeoDataByLang) &&
                Objects.equals(useExtraDescriptions, that.useExtraDescriptions) &&
                Objects.equals(useExtraTitles, that.useExtraTitles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SeoDataByLang, useExtraDescriptions, useExtraTitles);
    }

    @Override
    public String toString() {
        return "SeoData{" +
                "SeoDataByLang=" + SeoDataByLang +
                ", useExtraDescription=" + useExtraDescriptions +
                ", useExtraTitles=" + useExtraTitles +
                '}';
    }
}
