package org.entando.entando.plugins.jpseo.web.page.model;

import java.util.Map;
import java.util.Objects;

public class SeoData {

    private Map<String, SeoDataByLang> SeoDataByLang;
    private String friendlyCode;
    private Boolean useExtraDescriptions;
    private Boolean useExtraTitles;

    public Map<String, SeoDataByLang> getSeoDataByLang() {
        return SeoDataByLang;
    }

    public SeoData() {
    }

    public void setSeoDataByLang(
            Map<String, SeoDataByLang> SeoDataByLang) {
        this.SeoDataByLang = SeoDataByLang;
    }

    public String getFriendlyCode() {
        return friendlyCode;
    }

    public void setFriendlyCode(String friendlyCode) {
        this.friendlyCode = friendlyCode;
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
                Objects.equals(friendlyCode, that.friendlyCode) &&
                Objects.equals(useExtraDescriptions, that.useExtraDescriptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SeoDataByLang, friendlyCode, useExtraDescriptions);
    }

    @Override
    public String toString() {
        return "SeoData{" +
                "SeoDataByLang=" + SeoDataByLang +
                ", friendlyCode='" + friendlyCode + '\'' +
                ", useExtraDescription=" + useExtraDescriptions +
                ", useExtraTitles=" + useExtraTitles +
                '}';
    }
}
