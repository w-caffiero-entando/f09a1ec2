package org.entando.entando.web.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.entando.entando.aps.system.services.health.HealthService;
import org.entando.entando.aps.system.services.tenants.ITenantService;
import org.entando.entando.aps.system.services.tenants.TenantService;
import org.entando.entando.aps.system.services.tenants.TenantStatus;
import org.entando.entando.web.tenant.model.TenantStatsAndStatusesDto;
import org.entando.entando.web.tenant.model.TenantStatsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @Mock
    private HealthService healthService;

    @Mock
    private ITenantService tenantService;

    private HealthController healthController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        healthController = new HealthController(healthService, tenantService, "extended");
    }

    @Test
    void isHealthyWithWorkingSystemShouldReturnStatus200() {

        when(healthService.isHealthy()).thenReturn(true);

        assertEquals(HttpStatus.OK.value(), healthController.isHealthy(Optional.empty()).getStatusCodeValue());
    }

    @Test
    void isHealthyWithNotWorkingSystemShouldReturnStatus500() {

        when(healthService.isHealthy()).thenReturn(false);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), healthController.isHealthy(Optional.empty()).getStatusCodeValue());
    }

    @Test
    void isHealthyWithTenantsAndWorkingSystemShouldReturnStatus200() {

        when(healthService.isHealthy()).thenReturn(true);
        when(tenantService.getTenantStatsAndStatuses()).thenReturn(
                TenantStatsAndStatusesDto.builder()
                        .additionalStatuses(Map.of("t1",TenantStatus.UNKNOWN, "t2", TenantStatus.PENDING))
                        .mandatoryStatuses(Map.of("t3",TenantStatus.READY, "t4", TenantStatus.FAILED))
                        .stats(new TenantStatsDto(4,1,1,1,1)).build());

        ResponseEntity<HealthDto> resp = (ResponseEntity<HealthDto>) healthController.isHealthy(Optional.of(true));
        assertEquals(HttpStatus.OK.value(), resp.getStatusCodeValue());
        assertEquals(TenantStatus.UNKNOWN, resp.getBody().getAdditionals().get("t1"));
        assertEquals(TenantStatus.READY, resp.getBody().getMandatory().get("t3"));
        assertEquals(4, resp.getBody().getStats().getCount());
    }

}
