package org.entando.entando.aps.system.services.tenants;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TenantAsynchInitService implements ITenantAsynchInitService {

    public CompletableFuture<Void> initializeNotMandatoryTenants(Map<String, TenantStatus> statuses){
        return CompletableFuture.runAsync(() -> manageTenantsInit(statuses), Executors.newSingleThreadExecutor());
    }

    private void manageTenantsInit(Map<String, TenantStatus> statuses) {
        log.info("Start asynch initialization for not mandatory tenants");
        long startTenants = System.currentTimeMillis();

        statuses.entrySet().stream().forEach(entry -> {
            long startTenant = System.currentTimeMillis();
            String tenantCode = entry.getKey();
            try {
                statuses.put(tenantCode,TenantStatus.PENDING);

                initDb(tenantCode);

                refreshBeanForTenantCode(tenantCode);

                statuses.put(tenantCode,TenantStatus.READY);

            } catch (Throwable th) {
                statuses.put(tenantCode,TenantStatus.FAILED);
            } finally {
                log.info("Initialization of tenant '{}' completed in '{}' ms ", tenantCode, System.currentTimeMillis() - startTenant);
            }
        });

        log.info("End asynch initialization for not mandatory tenants in '{}' ms", System.currentTimeMillis() - startTenants);
    }

    private void initDb(String tenantCode) {

    }

    private void refreshBeanForTenantCode(String tenantCode) {

    }

}
