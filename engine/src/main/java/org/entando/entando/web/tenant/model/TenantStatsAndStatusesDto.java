
package org.entando.entando.web.tenant.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import org.entando.entando.aps.system.services.tenants.TenantStatus;

@Data
@Builder
public class TenantStatsAndStatusesDto {
    private TenantStatsDto stats;
    private Map<String, TenantStatus> mandatoryStatuses = new HashMap<>();
    private Map<String, TenantStatus> additionalStatuses = new HashMap<>();
}
