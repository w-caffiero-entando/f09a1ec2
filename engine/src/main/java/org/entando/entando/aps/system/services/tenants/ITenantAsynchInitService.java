package org.entando.entando.aps.system.services.tenants;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ITenantAsynchInitService {
    CompletableFuture<Void> startAsynchInitializeTenants();
}
