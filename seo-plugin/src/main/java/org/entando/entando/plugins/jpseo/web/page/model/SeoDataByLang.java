package org.entando.entando.plugins.jpseo.web.page.model;

import java.util.List;
import java.util.Objects;

public class SeoDataByLang {

    private String description;
    private String keywords;
    private List<SeoMetaTag> metaTags;
    private boolean inheritDescriptionFromDefaultLang;
    private boolean inheritKeywordsFromDefaultLang;

    public SeoDataByLang() {
    }

    public SeoDataByLang(String description,
            String keywords,
            List<SeoMetaTag> metaTags,
            boolean inheritDescriptionFromDefaultLang,
            boolean inheritKeywordsFromDefaultLang) {
        this.description = description;
        this.keywords = keywords;
        this.metaTags = metaTags;
        this.inheritDescriptionFromDefaultLang = inheritDescriptionFromDefaultLang;
        this.inheritKeywordsFromDefaultLang = inheritKeywordsFromDefaultLang;
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
                Objects.equals(description, that.description) &&
                Objects.equals(keywords, that.keywords) &&
                Objects.equals(metaTags, that.metaTags);
    }

    @Override
    public int hashCode() {
        return Objects.hash( description, keywords, metaTags, inheritDescriptionFromDefaultLang,
                inheritKeywordsFromDefaultLang);
    }

    @Override
    public String toString() {
        return "SeoProperties{" +
                "description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", metaTags=" + metaTags +
                ", inheritDescriptionFromDefaultLang=" + inheritDescriptionFromDefaultLang +
                ", inheritKeywordsFromDefaultLang=" + inheritKeywordsFromDefaultLang +
                '}';
    }
}
