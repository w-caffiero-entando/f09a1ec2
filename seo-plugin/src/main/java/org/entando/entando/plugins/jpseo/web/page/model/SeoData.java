package org.entando.entando.plugins.jpseo.web.page.model;

import java.util.Map;
import java.util.Objects;

public class SeoData {

    private Map<String, SeoDataByLang> SeoDataByLang;
    private String friendlyCode;
    private Boolean useExtraDescriptorSearch;

    public Map<String, SeoDataByLang> getSeoDataByLang() {
        return SeoDataByLang;
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

    public Boolean getUseExtraDescriptorSearch() {
        return useExtraDescriptorSearch;
    }

    public void setUseExtraDescriptorSearch(Boolean useExtraDescriptorSearch) {
        this.useExtraDescriptorSearch = useExtraDescriptorSearch;
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
                Objects.equals(useExtraDescriptorSearch, that.useExtraDescriptorSearch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(SeoDataByLang, friendlyCode, useExtraDescriptorSearch);
    }

    @Override
    public String toString() {
        return "SeoData{" +
                "SeoDataByLang=" + SeoDataByLang +
                ", friendlyCode='" + friendlyCode + '\'' +
                ", useExtraDescriptorSearch=" + useExtraDescriptorSearch +
                '}';
    }
}
