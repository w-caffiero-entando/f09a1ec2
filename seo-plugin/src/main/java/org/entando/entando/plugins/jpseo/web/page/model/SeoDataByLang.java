package org.entando.entando.plugins.jpseo.web.page.model;

import java.util.List;
import java.util.Objects;

public class SeoDataByLang {

    private String description;
    private String keywords;
    private String friendlyCode;
    private List<SeoMetaTag> metaTags;
    private boolean inheritDescriptionFromDefaultLang;
    private boolean inheritKeywordsFromDefaultLang;
    private boolean inheritFriendlyCodeFromDefaultLang;

    public SeoDataByLang() {
    }

    public SeoDataByLang(String description,
            String keywords,
            String friendlyCode,
            List<SeoMetaTag> metaTags,
            boolean inheritDescriptionFromDefaultLang,
            boolean inheritKeywordsFromDefaultLang,
            boolean inheritFriendlyCodeFromDefaultLang) {
        this.description = description;
        this.keywords = keywords;
        this.friendlyCode = friendlyCode;
        this.metaTags = metaTags;
        this.inheritDescriptionFromDefaultLang = inheritDescriptionFromDefaultLang;
        this.inheritKeywordsFromDefaultLang = inheritKeywordsFromDefaultLang;
        this.inheritFriendlyCodeFromDefaultLang = inheritFriendlyCodeFromDefaultLang;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getFriendlyCode() {
        return friendlyCode;
    }

    public void setFriendlyCode(String friendlyCode) {
        this.friendlyCode = friendlyCode;
    }

    public List<SeoMetaTag> getMetaTags() {
        return metaTags;
    }

    public void setMetaTags(List<SeoMetaTag> metaTags) {
        this.metaTags = metaTags;
    }

    public boolean isInheritDescriptionFromDefaultLang() {
        return inheritDescriptionFromDefaultLang;
    }

    public void setInheritDescriptionFromDefaultLang(boolean inheritDescriptionFromDefaultLang) {
        this.inheritDescriptionFromDefaultLang = inheritDescriptionFromDefaultLang;
    }

    public boolean isInheritKeywordsFromDefaultLang() {
        return inheritKeywordsFromDefaultLang;
    }

    public void setInheritKeywordsFromDefaultLang(boolean inheritKeywordsFromDefaultLang) {
        this.inheritKeywordsFromDefaultLang = inheritKeywordsFromDefaultLang;
    }

    public boolean isInheritFriendlyCodeFromDefaultLang() {
        return inheritFriendlyCodeFromDefaultLang;
    }

    public void setInheritFriendlyCodeFromDefaultLang(boolean inheritFriendlyCodeFromDefaultLang) {
        this.inheritFriendlyCodeFromDefaultLang = inheritFriendlyCodeFromDefaultLang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SeoDataByLang that = (SeoDataByLang) o;
        return inheritDescriptionFromDefaultLang == that.inheritDescriptionFromDefaultLang &&
                inheritKeywordsFromDefaultLang == that.inheritKeywordsFromDefaultLang &&
                inheritFriendlyCodeFromDefaultLang == that.inheritFriendlyCodeFromDefaultLang &&
                Objects.equals(description, that.description) &&
                Objects.equals(keywords, that.keywords) &&
                Objects.equals(friendlyCode, that.friendlyCode) &&
                Objects.equals(metaTags, that.metaTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, keywords, friendlyCode, metaTags, inheritDescriptionFromDefaultLang,
                inheritKeywordsFromDefaultLang, inheritFriendlyCodeFromDefaultLang);
    }

    @Override
    public String toString() {
        return "SeoDataByLang{" +
                "description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", friendlyCode='" + friendlyCode + '\'' +
                ", metaTags=" + metaTags +
                ", inheritDescriptionFromDefaultLang=" + inheritDescriptionFromDefaultLang +
                ", inheritKeywordsFromDefaultLang=" + inheritKeywordsFromDefaultLang +
                ", inheritFriendlyCodeFromDefaultLang=" + inheritFriendlyCodeFromDefaultLang +
                '}';
    }
}
