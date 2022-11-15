package org.entando.entando.plugins.jpseo.utils.stubhelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.entando.entando.plugins.jpseo.web.page.model.SeoData;
import org.entando.entando.plugins.jpseo.web.page.model.SeoDataByLang;
import org.entando.entando.plugins.jpseo.web.page.model.SeoMetaTag;

public class SeoStubHelper {

    public static final String FRIENDLY_CODE = "FR_CODE";
    public static final boolean EXTRA_TITLES = true;
    public static final boolean USE_EXTRA_DESCRIPTIONS = true;

    public static final String SEO_DATA_DESCRIPTION = "wonderful image";
    public static final boolean SEO_DATA_INHERIT_DESCR_FROM_DEFAULT_LANG = true;
    public static final boolean SEO_DATA_INHERIT_KEYWORDS_FROM_DEFAULT_LANG = true;
    public static final boolean SEO_DATA_INHERIT_FRIENDLY_CODE_FROM_DEFAULT_LANG = true;
    public static final String SEO_DATA_KEYWORDS = "entando";
    public static final String SEO_DATA_LANG = "ITA";

    public static final List<String> SEO_METATAG_KEYS = Arrays.asList("name", "location");
    public static final List<String> SEO_METATAG_VALUES = Arrays.asList("SEO Wellness", "The MooN");
    public static final List<String> SEO_METATAG_TYPES = Arrays.asList("Type_1", "Type_2");
    public static final List<Boolean> SEO_METATAG_USE_DEFAULT_LANG = Arrays.asList(true, false);

    private SeoStubHelper() {}

    /**
     *
     * @return a stub for the SeoData object
     */
    public static SeoData getSeoDataStub() {

        SeoData seoData = new SeoData();
        seoData.setUseExtraTitles(EXTRA_TITLES);
        seoData.setUseExtraDescriptions(USE_EXTRA_DESCRIPTIONS);
        seoData.setSeoDataByLang(getSeoDataByLangStub());

        return seoData;
    }

    /**
     *
     * @return a stub for the Map<String, SeoDataByLang> object
     */
    public static Map<String, SeoDataByLang> getSeoDataByLangStub() {

        SeoDataByLang seoDataByLangIta = new SeoDataByLang();
        seoDataByLangIta.setDescription(SEO_DATA_DESCRIPTION);
        seoDataByLangIta.setInheritDescriptionFromDefaultLang(SEO_DATA_INHERIT_DESCR_FROM_DEFAULT_LANG);
        seoDataByLangIta.setInheritKeywordsFromDefaultLang(SEO_DATA_INHERIT_KEYWORDS_FROM_DEFAULT_LANG);
        seoDataByLangIta.setInheritFriendlyCodeFromDefaultLang(SEO_DATA_INHERIT_FRIENDLY_CODE_FROM_DEFAULT_LANG);
        seoDataByLangIta.setKeywords(SEO_DATA_KEYWORDS);
        seoDataByLangIta.setMetaTags(getSeoMetaTagListStub());

        Map<String, SeoDataByLang> seoDataByLangMap = new HashMap<>();
        seoDataByLangMap.put(SEO_DATA_LANG, seoDataByLangIta);

        return seoDataByLangMap;
    }

    /**
     *
     * @return a stub for the List<SeoMetaTag>
     */
    public static List<SeoMetaTag> getSeoMetaTagListStub() {

        return IntStream.range(0, SEO_METATAG_KEYS.size())
                .mapToObj(i -> {
                    SeoMetaTag seoMetaTag = new SeoMetaTag();
                    seoMetaTag.setKey(SEO_METATAG_KEYS.get(i));
                    seoMetaTag.setValue(SEO_METATAG_VALUES.get(i));
                    seoMetaTag.setType(SEO_METATAG_TYPES.get(i));
                    seoMetaTag.setUseDefaultLang(SEO_METATAG_USE_DEFAULT_LANG.get(i));
                    return seoMetaTag;
                }).collect(Collectors.toList());
    }

}
