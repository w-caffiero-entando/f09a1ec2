package org.entando.entando.plugins.jpsolr.web.content.model;

import org.entando.entando.web.common.model.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AdvRestContentListRequestTest {

    @Test
    void shouldGetSolrFilters() {
        AdvRestContentListRequest request = new AdvRestContentListRequest();
        request.setFilters(new Filter[]{new Filter()});
        SolrFilter[] filters = request.getFilters();
        Assertions.assertEquals(1, filters.length);
    }
}
