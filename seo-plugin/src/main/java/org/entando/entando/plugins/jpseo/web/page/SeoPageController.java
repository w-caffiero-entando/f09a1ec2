package org.entando.entando.plugins.jpseo.web.page;

import com.agiletec.aps.system.exception.ApsSystemException;
import com.agiletec.aps.system.services.user.UserDetails;
import java.util.HashMap;
import java.util.Map;
import org.entando.entando.aps.system.services.page.IPageService;
import org.entando.entando.aps.system.services.page.PageAuthorizationService;
import org.entando.entando.aps.system.services.page.model.PageDto;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageDto;
import org.entando.entando.plugins.jpseo.aps.system.services.page.SeoPageService;
import org.entando.entando.plugins.jpseo.web.page.model.SeoPageRequest;
import org.entando.entando.plugins.jpseo.web.page.validator.SeoPageValidator;
import org.entando.entando.web.common.exceptions.ResourcePermissionsException;
import org.entando.entando.web.common.exceptions.ValidationConflictException;
import org.entando.entando.web.common.exceptions.ValidationGenericException;
import org.entando.entando.web.common.model.RestResponse;
import org.entando.entando.web.common.model.SimpleRestResponse;
import org.entando.entando.web.page.model.PagePositionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@RestController
@RequestMapping(value = "/plugins/seo/pages")
@SessionAttributes("user")
public class SeoPageController implements ISeoPageController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPageService pageService;

    @Autowired
    private SeoPageValidator seoPageValidator;

    @Autowired
    private PageAuthorizationService authorizationService;

    public IPageService getPageService() {
        return pageService;
    }

    public void setPageService(SeoPageService pageService) {
        this.pageService = pageService;
    }

    public SeoPageValidator getSeoPageValidator() {
        return seoPageValidator;
    }

    public void setSeoPageValidator(SeoPageValidator seoPageValidator) {
        this.seoPageValidator = seoPageValidator;
    }

    public PageAuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(PageAuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public ResponseEntity<RestResponse<PageDto, Map<String, String>>> getSeoPage(UserDetails user, String pageCode,
            String status) {
        logger.debug("get seo page {}", pageCode);

        Map<String, String> metadata = new HashMap<>();
        if (!this.getAuthorizationService().isAuth(user, pageCode)) {
            return new ResponseEntity<>(new RestResponse<>(new PageDto(), metadata), HttpStatus.UNAUTHORIZED);
        }
        SeoPageDto page = (SeoPageDto) this.getPageService().getPage(pageCode, status);
        metadata.put("status", status);
        return new ResponseEntity<>(new RestResponse<>(page, metadata), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<SimpleRestResponse<SeoPageDto>> addPage(UserDetails user,
            SeoPageRequest pageRequest, BindingResult bindingResult) throws ApsSystemException {
        logger.debug("creating page with request {}", pageRequest);
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }

        getSeoPageValidator().checkFriendlyCode(pageRequest.getSeoData().getFriendlyCode());

        //business validations
        getSeoPageValidator().validate(pageRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationConflictException(bindingResult);
        }
        SeoPageDto dto = (SeoPageDto) this.getPageService().addPage(pageRequest);
        return new ResponseEntity<>(new SimpleRestResponse<>(dto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestResponse<SeoPageDto, Map<String, String>>> updatePage(
            UserDetails user,String pageCode,
           SeoPageRequest pageRequest, BindingResult bindingResult) {
        logger.debug("updating page {} with request {}", pageCode, pageRequest);

        if (!this.getAuthorizationService().isAuth(user, pageCode)) {
            throw new ResourcePermissionsException(bindingResult, user.getUsername(), pageCode);
        }
        //field validations
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }
        this.getSeoPageValidator().validateBodyCode(pageCode, pageRequest, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationGenericException(bindingResult);
        }

        getSeoPageValidator().checkFriendlyCode(pageRequest.getSeoData().getFriendlyCode());

        PagePositionRequest pagePositionRequest = new PagePositionRequest();
        pagePositionRequest.setParentCode(pageRequest.getParentCode());
        pagePositionRequest.setCode(pageCode);
        int position = pageService.getPages(pageCode).size() + 1;
        pagePositionRequest.setPosition(position);

        this.getSeoPageValidator().validateMovePage(pageCode, bindingResult, pagePositionRequest);

        SeoPageDto page = (SeoPageDto) pageService.updatePage(pageCode, pageRequest);
        Map<String, String> metadata = new HashMap<>();
        return new ResponseEntity<>(new RestResponse<>(page, metadata), HttpStatus.OK);
    }

}
