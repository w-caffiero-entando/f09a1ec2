package org.entando.entando.plugins.jpsolr.aps.system.solr;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SolrLastReloadInfoTest {

    @Test
    void shouldGetDateByType() {
        Date defaultDate = Date.from(Instant.parse("2021-06-25T05:12:35Z"));
        Date type1Date = Date.from(Instant.parse("2023-06-25T05:12:35Z"));

        Map<String, Date> datesByTypeMap = Map.of("type1", type1Date);

        SolrLastReloadInfo reloadInfo = new SolrLastReloadInfo();
        reloadInfo.setDate(defaultDate);
        reloadInfo.setDatesByType(datesByTypeMap);

        Assertions.assertEquals(type1Date, reloadInfo.getDateByType("type1"));
        Assertions.assertEquals(defaultDate, reloadInfo.getDateByType("another-type"));
    }
}
