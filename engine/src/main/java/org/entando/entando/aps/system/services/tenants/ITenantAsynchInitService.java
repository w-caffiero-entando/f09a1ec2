package org.entando.entando.aps.system.services.tenants;

import java.util.concurrent.CompletableFuture;
import javax.servlet.ServletContext;

public interface ITenantAsynchInitService {
    CompletableFuture<Void> startAsynchInitializeTenants(ServletContext svCtx);
}
