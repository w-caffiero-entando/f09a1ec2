package org.entando.entando.plugins.jpseo.web.page.model;

public class SeoMetaTag {

    private String key;
    private String type;
    private String value;
    private Boolean useDefaultLang;

    public SeoMetaTag() {
    }

    public SeoMetaTag(String key, String type, String value, Boolean useDefaultLang) {
        this.key = key;
        this.type = type;
        this.value = value;
        this.useDefaultLang = useDefaultLang;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getUseDefaultLang() {
        return useDefaultLang;
    }

    public void setUseDefaultLang(Boolean useDefaultLang) {
        this.useDefaultLang = useDefaultLang;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SeoMetaTag{" +
                "key='" + key + '\'' +
                ", seoAttributeName=" + type +
                ", value=" + value +
                ", useDefaultLang=" + useDefaultLang +
                '}';
    }
}
