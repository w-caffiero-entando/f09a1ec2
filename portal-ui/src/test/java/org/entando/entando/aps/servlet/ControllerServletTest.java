package org.entando.entando.aps.servlet;

import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.io.Reader;
import org.entando.entando.aps.servlet.ControllerServlet;
import org.entando.entando.aps.servlet.ControllerServlet.EntTemplateLoader;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;
import org.entando.entando.ent.exception.EntException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ControllerServletTest {

    @Mock
    private IGuiFragmentManager guiFragmentManager;

    @Test
    void shouldBeOk() throws Exception {
        EntTemplateLoader templateLoader = new EntTemplateLoader(guiFragmentManager);

        Assertions.assertEquals(-1, templateLoader.getLastModified(new Object()));

        templateLoader.closeTemplateSource(new Object());

        Reader r = templateLoader.getReader("test", null);
        Assertions.assertNotNull(r);

        GuiFragment guiFragment = new GuiFragment();
        guiFragment.setGui("testGui");
        Mockito.when(guiFragmentManager.getGuiFragment(any())).thenReturn(guiFragment);
        Assertions.assertEquals("testGui", templateLoader.findTemplateSource("code"));


        Mockito.when(guiFragmentManager.getGuiFragment(any())).thenReturn(null);
        Object obj = templateLoader.findTemplateSource("code");
        Assertions.assertNull(obj);

        Mockito.when(guiFragmentManager.getGuiFragment(any())).thenThrow(new EntException("errortest"));
        Assertions.assertThrows(IOException.class, ()-> templateLoader.findTemplateSource("code"));



    }
}
