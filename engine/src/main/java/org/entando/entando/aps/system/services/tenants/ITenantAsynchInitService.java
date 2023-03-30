package org.entando.entando.aps.system.services.tenants;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

public interface ITenantAsynchInitService {
    CompletableFuture<Void> initializeNotMandatoryTenants(Map<String, TenantStatus> statuses);
}
