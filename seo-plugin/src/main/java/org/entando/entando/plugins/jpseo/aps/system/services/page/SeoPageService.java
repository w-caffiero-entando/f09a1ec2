package org.entando.entando.plugins.jpseo.aps.system.services.page;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.lang.Lang;
import com.agiletec.aps.system.services.lang.LangManager;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.PageManager;
import com.agiletec.aps.util.ApsProperties;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.entando.entando.aps.system.exception.ResourceNotFoundException;
import org.entando.entando.aps.system.exception.RestServerError;
import org.entando.entando.aps.system.services.page.PageService;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.plugins.jpseo.web.page.model.SeoData;
import org.entando.entando.plugins.jpseo.web.page.model.SeoDataByLang;
import org.entando.entando.plugins.jpseo.web.page.model.SeoMetaTag;
import org.entando.entando.plugins.jpseo.web.page.model.SeoPageRequest;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.page.model.PagePositionRequest;
import org.entando.entando.web.page.model.PageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

@Service
@Primary
public class SeoPageService extends PageService {

    private static final Logger logger = LoggerFactory.getLogger(SeoPageService.class);

    @Autowired
    private PageManager pageManager;

    @Autowired
    private LangManager langManager;

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

        return mapPageToSeoPageDto(pageCode, page);
    }


    private SeoPageDto mapPageToSeoPageDto(String pageCode, IPage page) {

        String token = this.getPageTokenManager().encrypt(pageCode);
        PageDto pageDto = this.getDtoBuilder().convert(page);
        pageDto.setToken(token);
        pageDto.setReferences(super.getReferencesInfo(page));

        pageDto.setDisplayedInMenu(page.isShowable());

        SeoPageMetadata seoMetadata;
        if (page.getMetadata() instanceof SeoPageMetadata) {
            seoMetadata = (SeoPageMetadata) page.getMetadata();
        }
        else {
            seoMetadata = new SeoPageMetadata();
        }
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
        seoData.setUseExtraDescriptions(seoMetadata.isUseExtraDescriptions());
        seoData.setUseExtraTitles(seoMetadata.isUseExtraTitles());

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
                            PageMetatag descriptionMetaTag = (PageMetatag) descriptions.get(lang);
                            seoMetadataDescription = descriptionMetaTag.getValue();
                            inheritDescriptionFromDefaultLang = descriptionMetaTag.isUseDefaultLangValue();
                        }
                        ApsProperties keywords = seoMetadata.getKeywords();
                        if (null != keywords) {
                            PageMetatag keywordsMetaTag = (PageMetatag) keywords.get(lang);
                            seoMetadataKeyword = keywordsMetaTag.getValue();
                            inheritKeywordsFromDefaultLang = keywordsMetaTag.isUseDefaultLangValue();
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


    @Override
    public SeoPageDto addPage(PageRequest pageRequest) {
        super.validateRequest(pageRequest);
        final SeoPageRequest seoPageRequest = (SeoPageRequest) pageRequest;
        SeoData seoData = seoPageRequest.getSeoData();
        final SeoPageMetadata seoPageMetadata = mapSeoDataToSeoPageMetadata(seoData, pageRequest);
        IPage page = super.createPage(pageRequest);
        page.setMetadata(seoPageMetadata);
        try {
            pageManager.addPage(page);
        } catch (ApsSystemException e) {
            logger.error("error addding seo page: {}", e);
        }
        IPage addedPage = this.getPageManager().getDraftPage(page.getCode());
        return mapPageToSeoPageDto(addedPage.getCode(), page);
    }

    @Override
    public SeoPageDto updatePage(String pageCode, PageRequest pageRequest) {
        IPage oldPage = this.getPageManager().getDraftPage(pageCode);
        if (null == oldPage) {
            throw new ResourceNotFoundException( null, "page", pageCode);
        } else {
            this.validateRequest(pageRequest);
            if (!oldPage.getGroup().equals(pageRequest.getOwnerGroup())) {
                BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(oldPage, "page");
                bindingResult.reject("2", new String[]{oldPage.getGroup(), pageRequest.getOwnerGroup()},
                        "page.update.group.invalid");
                throw new ValidationGenericException(bindingResult);
            } else {
                try {
                    if (!oldPage.getParentCode().equals(pageRequest.getParentCode())) {
                        PagePositionRequest pagePositionRequest = new PagePositionRequest();
                        pagePositionRequest.setParentCode(pageRequest.getParentCode());
                        pagePositionRequest.setCode(pageCode);
                        int position = this.getPages(pageCode).size() + 1;
                        pagePositionRequest.setPosition(position);
                        this.movePage(pageCode, pagePositionRequest);
                        oldPage = this.getPageManager().getDraftPage(pageCode);
                    }
                    final SeoPageRequest seoPageRequest = (SeoPageRequest) pageRequest;
                    SeoData seoData = seoPageRequest.getSeoData();
                    final SeoPageMetadata seoPageMetadata = mapSeoDataToSeoPageMetadata(seoData, pageRequest);
                    IPage newPage = super.updatePage(oldPage, pageRequest);
                    newPage.setMetadata(seoPageMetadata);
                    pageManager.updatePage(newPage);
                    IPage updatedPage = this.getPageManager().getDraftPage(pageCode);
                    updatedPage.setPosition(oldPage.getPosition());
                    return mapPageToSeoPageDto(pageCode, updatedPage);
                } catch (ApsSystemException e) {
                    this.logger.error("Error updating page {}", pageCode, e);
                    throw new RestServerError("error in update page", e);
                }
            }
        }
    }

    private SeoPageMetadata mapSeoDataToSeoPageMetadata(SeoData seoData,
            PageRequest pageRequest) {
        SeoPageMetadata seoPageMetadata = new SeoPageMetadata();
        seoPageMetadata.setModel(getPageModelManager().getPageModel(pageRequest.getPageModel()));
        seoPageMetadata.setGroup(pageRequest.getOwnerGroup());
        final Map<String, String> titles = pageRequest.getTitles();
        if (null!=seoData.getFriendlyCode()) {
            seoPageMetadata.setFriendlyCode(seoData.getFriendlyCode());
        }
        if (null!=seoData.getUseExtraDescriptions()) {
            seoPageMetadata.setUseExtraDescriptions(seoData.getUseExtraDescriptions());
        }
        if  (null!=seoData.getUseExtraTitles()) {
            seoPageMetadata.setUseExtraTitles(seoData.getUseExtraTitles());
        }
        seoPageMetadata.setCharset(pageRequest.getCharset());
        seoPageMetadata.setMimeType(pageRequest.getContentType());
        seoPageMetadata.setShowable(pageRequest.isDisplayedInMenu());
        Set<String> extraGroups = pageRequest.getJoinGroups().stream().collect(Collectors.toSet());

        seoPageMetadata.setExtraGroups(extraGroups);
        ApsProperties keywordsAps = new ApsProperties();
        ApsProperties descriptionsAps = new ApsProperties();
        ApsProperties titlesAps = new ApsProperties();

        Map<String, Map<String, PageMetatag>> langMetaTags = new HashMap();
        String defaultLang = langManager.getDefaultLang().getCode();
        List<Lang> systemLangs = langManager.getLangs();
        List<String> systemLangsString = systemLangs.stream().map(f -> f.getCode()).collect(Collectors.toList());

        seoData.getSeoDataByLang().forEach((lang, seoDataByLang) -> {
            Boolean inheritKeywords = false;
            Boolean inheritDescription = false;
            if (systemLangsString.contains(lang)) {
                if (!lang.equals(defaultLang)) {
                    inheritKeywords = seoDataByLang.isInheritKeywordsFromDefaultLang();
                    inheritDescription = seoDataByLang.isInheritDescriptionFromDefaultLang();
                }
                PageMetatag keywordsPageMetaTag = new PageMetatag(lang, "keywords", seoDataByLang.getKeywords().trim(),
                        inheritKeywords);

                PageMetatag descriptionPageMetaTag = new PageMetatag(lang, "description",
                        seoDataByLang.getDescription().trim(), inheritDescription);

                keywordsAps.put(lang, keywordsPageMetaTag);
                descriptionsAps.put(lang, descriptionPageMetaTag);
                langMetaTags.put(lang, mapLangMetaTags(seoDataByLang.getMetaTags()));
            } else {
                logger.warn("Lang not valid :{}. SeoDataByLang not added", lang);
            }
        });

        seoPageMetadata.setKeywords(keywordsAps);
        seoPageMetadata.setDescriptions(descriptionsAps);

        titles.forEach((key, value) ->
        {
            if (systemLangsString.contains(key)) {
                titlesAps.setProperty(key, value);
            } else {
                logger.warn("Lang not valid :{}. Title not added", key);
            }
        });

        seoPageMetadata.setTitles(titlesAps);
        seoPageMetadata.setComplexParameters(langMetaTags);
        seoPageMetadata.setUpdatedAt(new Date());
        return seoPageMetadata;
    }

    private Map<String, PageMetatag> mapLangMetaTags(List<SeoMetaTag> metaTags) {
        final List<PageMetatag> pageMetatagList = metaTags.stream().map(metaTag -> {
            PageMetatag pageMetatag = new PageMetatag(metaTag.getType(), metaTag.getKey(),
                    metaTag.getValue(), metaTag.getType(), metaTag.getUseDefaultLang());
            return pageMetatag;
        }).collect(Collectors.toList());

        return convertPageMetatagListToMap(pageMetatagList);
    }

    public Map<String, PageMetatag> convertPageMetatagListToMap(List<PageMetatag> list) {
        Map<String, PageMetatag> map = list.stream().collect(Collectors.toMap(PageMetatag::getKey, meta -> meta));
        return map;
    }


}
