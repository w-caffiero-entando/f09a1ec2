package com.agiletec.aps.util;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

import org.assertj.core.api.Assertions;
import org.entando.entando.aps.system.services.tenants.ITenantManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApsTenantApplicationUtilsTest {

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private ITenantManager tenantManager;

    @Test
    void shouldExtractCorrectTenantDomainWithEveryInput(){

        try(MockedStatic<ApsWebApplicationUtils> apsWebApplicationUtils = Mockito.mockStatic(ApsWebApplicationUtils.class)){
            apsWebApplicationUtils.when(() -> ApsWebApplicationUtils.getBean(ITenantManager.class,httpServletRequest)).thenReturn(tenantManager);

            Mockito.when(httpServletRequest.getServerName()).thenReturn("www.test.com");
            Mockito.when(tenantManager.getTenantCodeByDomainPrefix("test")).thenReturn("wwwCode");
            Optional<String> res= ApsTenantApplicationUtils.extractCurrentTenantCode(httpServletRequest);
            Assertions.assertThat(res.get()).isNotEmpty().isEqualTo("wwwCode");

            Mockito.when(httpServletRequest.getServerName()).thenReturn("code.test.com");
            Mockito.when(tenantManager.getTenantCodeByDomainPrefix("code")).thenReturn("tCode");
            res= ApsTenantApplicationUtils.extractCurrentTenantCode(httpServletRequest);
            Assertions.assertThat(res.get()).isNotEmpty().isEqualTo("tCode");

            Mockito.when(httpServletRequest.getServerName()).thenReturn("localhost");
            Mockito.when(tenantManager.getTenantCodeByDomainPrefix("localhost")).thenReturn(null);
            res= ApsTenantApplicationUtils.extractCurrentTenantCode(httpServletRequest);
            Assertions.assertThat(res).isNotPresent();
        }
    }

    @Test
    void shouldGetSetRemoveWorkCorrectTenantCodeWithEveryInput() {

        Optional<String> tanant = ApsTenantApplicationUtils.getTenant();
        Assertions.assertThat(tanant).isNotPresent();

        ApsTenantApplicationUtils.setTenant("tCode");
        tanant = ApsTenantApplicationUtils.getTenant();
        Assertions.assertThat(tanant.get()).isNotEmpty().isEqualTo("tCode");

        ApsTenantApplicationUtils.removeTenant();
        tanant = ApsTenantApplicationUtils.getTenant();
        Assertions.assertThat(tanant).isNotPresent();

    }


}
