package org.entando.entando.web.tenant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TenantStatsDto {
    private int count;
    private int unknown;
    private int pending;
    private int ready;
    private int failed;
}
