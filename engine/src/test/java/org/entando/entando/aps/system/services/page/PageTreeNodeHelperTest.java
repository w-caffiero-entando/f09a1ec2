package org.entando.entando.aps.system.services.page;

import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.IPageManager;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PageTreeNodeHelperTest {

    @Mock
    private IPageManager pageManager;

    @InjectMocks
    private PageTreeNodeHelper helper;

    @BeforeEach
    void setUp() {
        mockTree();
    }

    @Test
    void userBelongingToFreeGroupShouldSeeFreePages() {
        // invocation of "homepage" child nodes; The system returns with nodes p1, p2, p3
        List<IPage> pages = helper.getNodes("homepage", List.of("free"));
        verifyNodesList(pages, "p1", "p2", "p3");

        // invocation of child nodes "p2"; The system returns with nodes p22
        pages = helper.getNodes("p2", List.of("free"));
        verifyNodesList(pages, "p22");

        // invocation of child nodes "p21" (unauthorized page and unauthorized children); The system returns with an empty list of nodes
        pages = helper.getNodes("p21", List.of("free"));
        verifyNodesList(pages, new String[]{});

        // invocation of child nodes "p12"; The system returns with nodes p121, p123
        pages = helper.getNodes("p12", List.of("free"));
        verifyNodesList(pages, "p121", "p123");

        // invocation of child nodes "p22"; The system returns with nodes p222
        pages = helper.getNodes("p22", List.of("free"));
        verifyNodesList(pages, "p222");

        // invocation of child nodes "p31" (unauthorized page and unauthorized children); The system returns with an empty list of nodes
        pages = helper.getNodes("p31", List.of("free"));
        verifyNodesList(pages, new String[]{});
    }

    @Test
    void userBelongingToReservedGroupShouldSeeReservedPages() {
        // invocation of child nodes "homepage" (unauthorized page but root); The system returns with nodes p112, p122, p21, p221, p223
        List<IPage> pages = helper.getNodes("homepage", List.of("reserved"));
        verifyNodesList(pages, "p112", "p122", "p21", "p221", "p223");

        // invocation of child nodes "p2" (unauthorized page with authorized children); The system returns with nodes p21
        pages = helper.getNodes("p2", List.of("reserved"));
        verifyNodesList(pages, "p21", "p221", "p223");

        // invocation of child nodes "p21"; The system returns with nodes p211, p212, p213
        pages = helper.getNodes("p21", List.of("reserved"));
        verifyNodesList(pages, "p211", "p212", "p213");

        // invocation of child nodes "p12" (unauthorized page with an authorized child); The system returns with nodes p122
        pages = helper.getNodes("p12", List.of("reserved"));
        verifyNodesList(pages, "p122");

        // invocation of child nodes "p22" (unauthorized page with authorized children); The system returns with nodes p221, p223
        pages = helper.getNodes("p22", List.of("reserved"));
        verifyNodesList(pages, "p221", "p223");

        // child node invocation "p31" (unauthorized page with unauthorized children); The system returns with <empty_list> nodes
        pages = helper.getNodes("p31", List.of("reserved"));
        verifyNodesList(pages);
    }

    @Test
    void userBelongingToAdministratorsGroupShouldSeeAllPages() {
        // invocation of "homepage" child nodes; The system returns with nodes p1, p2, p3
        List<IPage> pages = helper.getNodes("homepage", List.of("administrators"));
        verifyNodesList(pages, "p1", "p2", "p3");

        // invocation of child nodes "p2"; The system returns with nodes p21, p22
        pages = helper.getNodes("p2", List.of("administrators"));
        verifyNodesList(pages, "p21", "p22");

        // invocation of child nodes "p21"; The system returns with nodes p211, p212, p213
        pages = helper.getNodes("p21", List.of("administrators"));
        verifyNodesList(pages, "p211", "p212", "p213");

        // invocation of child nodes "p12"; The system returns with nodes p121, p122, p123
        pages = helper.getNodes("p12", List.of("administrators"));
        verifyNodesList(pages, "p121", "p122", "p123");

        // invocation of child nodes "p22"; The system returns with nodes p221, p222, p223
        pages = helper.getNodes("p22", List.of("administrators"));
        verifyNodesList(pages, "p221", "p222", "p223");

        // invocation of child nodes "p31"; The system returns with nodes p311, p312, p313
        pages = helper.getNodes("p31", List.of("administrators"));
        verifyNodesList(pages, "p311", "p312", "p313");
    }

    private void verifyNodesList(List<IPage> pages, String... expectedCodes) {
        Assertions.assertEquals(expectedCodes.length, pages.size());
        for (int i = 0; i < expectedCodes.length; i++) {
            Assertions.assertEquals(expectedCodes[i], pages.get(i).getCode());
        }
    }

    /**
     * homepage (free)
     * ├──p1 (free)
     * │   ├──p11 (free)
     * │   │   ├──p111 (free)
     * │   │   ├──p112 (reserved)
     * │   │   └──p113 (administrator)
     * │   └──p12 (free)
     * │       ├──p121 (free)
     * │       ├──p122 (reserved)
     * │       └──p123 (free)
     * ├──p2 (free)
     * │   ├──p21 (reserved)
     * │   │   ├──p211 (reserved)
     * │   │   ├──p212 (reserved)
     * │   │   └──p213 (reserved)
     * │   └──p22 (free)
     * │       ├──p221 (reserved)
     * │       ├──p222 (free)
     * │       └──p223 (reserved)
     * └──p3 (free)
     *     ├──p31 (administrator)
     *     │   ├──p311 (administrator)
     *     │   ├──p312 (administrator)
     *     │   └──p313 (administrator)
     *     └──p32 (free)
     *         ├──p321 (free)
     *         ├──p322 (administrator)
     *         └──p323 (free)
     */
    private void mockTree() {
        mockPage("homepage", "free", "p1", "p2", "p3");

        mockPage("p1", "free", "p11", "p12");
        mockPage("p11", "free", "p111", "p112", "p113");
        mockPage("p111", "free");
        mockPage("p112", "reserved");
        mockPage("p113", "administrator");

        mockPage("p12", "free", "p121", "p122", "p123");
        mockPage("p121", "free");
        mockPage("p122", "reserved");
        mockPage("p123", "free");

        mockPage("p2", "free", "p21", "p22");
        mockPage("p21", "reserved", "p211", "p212", "p213");
        mockPage("p211", "reserved");
        mockPage("p212", "reserved");
        mockPage("p213", "reserved");
        mockPage("p22", "free", "p221", "p222", "p223");
        mockPage("p221", "reserved");
        mockPage("p222", "free");
        mockPage("p223", "reserved");

        mockPage("p3", "free", "p31", "p32");
        mockPage("p31", "administrator", "p311", "p312", "p313");
        mockPage("p311", "administrator");
        mockPage("p312", "administrator");
        mockPage("p313", "administrator");
        mockPage("p32", "free", "p321", "p322", "p323");
        mockPage("p321", "free");
        mockPage("p322", "administrator");
        mockPage("p323", "free");
    }

    private void mockPage(String pageCode, String group, String... children) {
        IPage page = Mockito.mock(IPage.class);
        Mockito.lenient().when(page.getCode()).thenReturn(pageCode);
        Mockito.lenient().when(page.getGroup()).thenReturn(group);
        Mockito.lenient().when(page.getChildrenCodes()).thenReturn(children);
        Mockito.lenient().when(pageManager.getDraftPage(pageCode)).thenReturn(page);
    }
}
