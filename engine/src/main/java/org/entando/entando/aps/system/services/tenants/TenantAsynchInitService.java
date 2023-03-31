package org.entando.entando.aps.system.services.tenants;

import com.agiletec.aps.util.ApsTenantApplicationUtils;
import com.agiletec.aps.util.ApsWebApplicationUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.entando.entando.aps.system.init.IInitializerManager.DatabaseMigrationStrategy;
import org.entando.entando.aps.system.init.InitializerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TenantAsynchInitService implements ITenantAsynchInitService {

    private final TenantDataAccessor tenantDataAccessor;
    private final InitializerManager initializerManager;
    private final DatabaseMigrationStrategy defaultDbInitStrategy;

    @Autowired
    public TenantAsynchInitService(TenantDataAccessor acecssor, InitializerManager im, @Value("${db.migration.strategy:}") String defaultStrategy){
        this.tenantDataAccessor = acecssor;
        this.initializerManager = im;
        this.defaultDbInitStrategy = Optional.ofNullable(defaultStrategy)
                .map(d -> DatabaseMigrationStrategy.valueOf(d.toUpperCase()))
                .orElse(DatabaseMigrationStrategy.DISABLED);
    }

    public CompletableFuture<Void> startAsynchInitializeTenants(ServletContext svCtx){
        Map<String,TenantStatus> statuses = tenantDataAccessor.getTenantStatuses();
        return CompletableFuture.runAsync(() -> manageTenantsInit(statuses, svCtx), Executors.newSingleThreadExecutor());
    }

    private void manageTenantsInit(Map<String, TenantStatus> statuses, ServletContext svCtx) {
        log.info("Start asynch initialization for not mandatory tenants");
        long startTenants = System.currentTimeMillis();

        statuses.entrySet().stream().forEach(entry -> {
            long startTenant = System.currentTimeMillis();
            String tenantCode = entry.getKey();
            try {
                statuses.put(tenantCode,TenantStatus.PENDING);

                ApsTenantApplicationUtils.setTenant(tenantCode);
                initDb(tenantCode);
                refreshBeanForTenantCode(svCtx);

                statuses.put(tenantCode,TenantStatus.READY);

            } catch (Throwable th) {
                statuses.put(tenantCode,TenantStatus.FAILED);
            } finally {
                log.info("Initialization of tenant '{}' completed in '{}' ms ", tenantCode, System.currentTimeMillis() - startTenant);
            }
        });

        log.info("End asynch initialization for not mandatory tenants in '{}' ms", System.currentTimeMillis() - startTenants);
    }

    private void initDb(String tenantCode) throws Exception {
        // compute strategy
        DatabaseMigrationStrategy strategy = tenantDataAccessor.getTenantConfigs().entrySet().stream()
                .filter(e -> StringUtils.equals(e.getKey(), tenantCode))
                .findFirst()
                .flatMap(e -> e.getValue().getDbMigrationStrategy())
                .filter(Objects::nonNull)
                .map(s -> DatabaseMigrationStrategy.valueOf(s.toUpperCase()))
                .orElse(defaultDbInitStrategy);
        Map<String, DataSource> datasources = new HashMap<>();
        datasources.put("portDataSource",tenantDataAccessor.getTenantDatasource(tenantCode));
        datasources.put("servDataSource",tenantDataAccessor.getTenantDatasource(tenantCode));
        initializerManager.initTenant(strategy, Optional.of(datasources));

    }

    private void refreshBeanForTenantCode(ServletContext svCtx) throws Throwable {
        ApsWebApplicationUtils.executeSystemRefresh(svCtx);
    }

}
