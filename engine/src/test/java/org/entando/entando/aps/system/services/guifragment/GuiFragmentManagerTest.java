package org.entando.entando.aps.system.services.guifragment;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GuiFragmentManagerTest {

    private static final String FRAGMENT_CODE = "my_fragment";

    @Mock
    private IGuiFragmentDAO guiFragmentDAO;

    @InjectMocks
    private GuiFragmentManager manager;

    @Test
    void testGetGuiFragmentUtilizersGuiWp() throws Exception {
        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setGui("<@wp.fragment code=\"" + FRAGMENT_CODE + "\" escapeXml=false />");
        testGetGuiFragmentUtilizers(guiFragment);
    }

    @Test
    void testGetGuiFragmentUtilizersDefaultGuiWp() throws Exception {
        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setDefaultGui("<@wp.fragment code=\"" + FRAGMENT_CODE + "\" escapeXml=false />");
        testGetGuiFragmentUtilizers(guiFragment);
    }

    @Test
    void testGetGuiFragmentUtilizersGuiIncude() throws Exception {
        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setGui("<#include \"" + FRAGMENT_CODE + "\">");
        testGetGuiFragmentUtilizers(guiFragment);
    }

    @Test
    void testGetGuiFragmentUtilizersDefaultGuiIncude() throws Exception {
        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setDefaultGui("<#include \"" + FRAGMENT_CODE + "\">");
        testGetGuiFragmentUtilizers(guiFragment);
    }

    @Test
    void testGetGuiFragmentUtilizersNotFound() throws Exception {
        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setDefaultGui("<#include \"another_fragment\">");
        Mockito.when(guiFragmentDAO.searchGuiFragments(Mockito.any())).thenReturn(List.of(FRAGMENT_CODE));
        Mockito.when(guiFragmentDAO.loadGuiFragment(FRAGMENT_CODE)).thenReturn(guiFragment);
        List<GuiFragment> utilizers = manager.getGuiFragmentUtilizers(FRAGMENT_CODE);
        Assertions.assertTrue(utilizers.isEmpty());
    }

    private void testGetGuiFragmentUtilizers(GuiFragment guiFragment) throws Exception {
        Mockito.when(guiFragmentDAO.searchGuiFragments(Mockito.any())).thenReturn(List.of(FRAGMENT_CODE));
        Mockito.when(guiFragmentDAO.loadGuiFragment(FRAGMENT_CODE)).thenReturn(guiFragment);
        List<GuiFragment> utilizers = manager.getGuiFragmentUtilizers(FRAGMENT_CODE);
        Assertions.assertEquals(1, utilizers.size());
        Assertions.assertEquals(guiFragment, utilizers.get(0));
    }
}
