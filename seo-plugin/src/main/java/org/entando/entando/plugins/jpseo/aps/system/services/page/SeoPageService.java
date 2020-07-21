package org.entando.entando.plugins.jpseo.aps.system.services.page;

import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.PageManager;
import com.agiletec.aps.util.ApsProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.services.page.PageService;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.plugins.jpseo.web.page.model.SeoData;
import org.entando.entando.plugins.jpseo.web.page.model.SeoDataByLang;
import org.entando.entando.plugins.jpseo.web.page.model.SeoMetaTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

@Service
@Primary
public class SeoPageService extends PageService {

    private static final Logger logger = LoggerFactory.getLogger(SeoPageService.class);

    @Autowired
    private PageManager pageManager;

    @Override
    public SeoPageDto getPage(String pageCode, String status) {
        IPage page = super.loadPage(pageCode, status);
        if (null == page) {
            logger.warn("no page found with code {} and status {}", pageCode, status);
            DataBinder binder = new DataBinder(pageCode);
            BindingResult bindingResult = binder.getBindingResult();
            String errorCode = status.equals(STATUS_DRAFT) ? ERRCODE_PAGE_NOT_FOUND : ERRCODE_PAGE_ONLY_DRAFT;
            bindingResult.reject(errorCode, new String[]{pageCode, status}, "page.NotFound");
            throw new ResourceNotFoundException(bindingResult);
        }
        String token = this.getPageTokenManager().encrypt(pageCode);
        PageDto pageDto = this.getDtoBuilder().convert(page);
        pageDto.setToken(token);
        pageDto.setReferences(super.getReferencesInfo(page));
        SeoPageMetadata seoMetadata = (SeoPageMetadata) page.getMetadata();
        SeoPageDto seoPageDto = mapPageDtoToSeoPageDto(pageDto, seoMetadata);
        return seoPageDto;
    }

    private SeoPageDto mapPageDtoToSeoPageDto(PageDto pageDto, SeoPageMetadata seoMetadata) {
        SeoPageDto seoPageDto = new SeoPageDto();
        seoPageDto.setCharset(pageDto.getCharset());
        seoPageDto.setCode(pageDto.getCode());
        seoPageDto.setSeo(pageDto.isSeo());
        seoPageDto.setChildren(pageDto.getChildren());
        seoPageDto.setContentType(pageDto.getContentType());
        seoPageDto.setDisplayedInMenu(pageDto.isDisplayedInMenu());
        seoPageDto.setFullPath(pageDto.getFullPath());
        seoPageDto.setFullTitles(pageDto.getFullTitles());
        seoPageDto.setJoinGroups(pageDto.getJoinGroups());
        seoPageDto.setToken(pageDto.getToken());
        seoPageDto.setLastModified(pageDto.getLastModified());
        seoPageDto.setNumWidget(pageDto.getNumWidget());
        seoPageDto.setParentCode(pageDto.getParentCode());
        seoPageDto.setStatus(pageDto.getStatus());
        seoPageDto.setTitles(pageDto.getTitles());
        seoPageDto.setPageModel(pageDto.getPageModel());
        seoPageDto.setOwnerGroup(pageDto.getOwnerGroup());
        seoPageDto.setOnlineInstance(pageDto.isOnlineInstance());
        SeoData seoData = new SeoData();
        seoData.setFriendlyCode(seoMetadata.getFriendlyCode());
        seoData.setUseExtraDescriptorSearch(seoMetadata.isUseExtraDescriptions());
        Map<String, SeoDataByLang> seoDataByLangMap = new HashMap<>();
        if (seoMetadata.getComplexParameters() != null) {
            seoMetadata.getComplexParameters().entrySet().stream()
                    .forEach(e -> {
                        String lang = e.getKey();
                        final Map<String, PageMetatag> metatagMap = e.getValue();
                        final List<SeoMetaTag> pageMetaTagList = metatagMap.entrySet().stream().map(meta ->
                                {
                                    final PageMetatag metatag = meta.getValue();
                                    return new SeoMetaTag(metatag.getKey(),
                                            metatag.getKeyAttribute(),
                                            metatag.getValue(),
                                            metatag.isUseDefaultLangValue());
                                }
                        ).collect(Collectors.toList());
                        boolean inheritDescriptionFromDefaultLang = false;
                        boolean inheritKeywordsFromDefaultLang = false;
                        String seoMetadataDescription = null;
                        String seoMetadataKeyword = null;
                        ApsProperties descriptions = seoMetadata.getDescriptions();
                        if (null != descriptions) {
                            PageMetatag metatag = (PageMetatag) descriptions.get(lang);
                            seoMetadataDescription = metatag.getValue();
                            inheritDescriptionFromDefaultLang = metatag.isUseDefaultLangValue();

                        }
                        ApsProperties keywords = seoMetadata.getKeywords();
                        if (null != keywords) {
                            PageMetatag metatag = (PageMetatag) keywords.get(lang);
                            seoMetadataKeyword = metatag.getValue();
                            inheritDescriptionFromDefaultLang = metatag.isUseDefaultLangValue();
                        }
                        SeoDataByLang seoDataByLang = new SeoDataByLang(
                                seoMetadataDescription,
                                seoMetadataKeyword,
                                pageMetaTagList,
                                inheritDescriptionFromDefaultLang,
                                inheritKeywordsFromDefaultLang);
                        seoDataByLangMap.put(lang, seoDataByLang);
                    });
            seoData.setSeoDataByLang(seoDataByLangMap);
        }
        seoPageDto.setSeoData(seoData);
        return seoPageDto;
    }

}
