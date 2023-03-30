package org.entando.entando.web.health;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.entando.entando.aps.system.services.tenants.TenantStatus;
import org.entando.entando.web.tenant.model.TenantStatsDto;

@Data
@Builder
public class HealthDto {

    private TenantStatsDto stats;
    private Map<String, TenantStatus> additionals;

}
