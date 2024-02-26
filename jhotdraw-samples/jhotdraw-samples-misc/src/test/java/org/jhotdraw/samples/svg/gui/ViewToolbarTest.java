package org.jhotdraw.samples.svg.gui;

import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ViewToolbarTest {
    protected ViewToolBar toolBar;
    protected JComponent miniComponent;
    protected JComponent expandedComponent;

    @Before
    public void setup() {
        toolBar = new ViewToolBar();
        toolBar.setView(new DefaultDrawingView());
        toolBar.editor = new DefaultDrawingEditor();

        miniComponent = toolBar.createDisclosedComponent(1);
        expandedComponent = toolBar.createDisclosedComponent(2);
    }

    @Test
    public void testCreateDisclosedComponent() {
        JComponent state0 = toolBar.createDisclosedComponent(0);
        JComponent state1 = toolBar.createDisclosedComponent(1);
        JComponent state2 = toolBar.createDisclosedComponent(2);
        JComponent state3 = toolBar.createDisclosedComponent(3);

        assertNull(state0);
        assertNotNull(state1);
        assertNotNull(state2);
        assertNull(state3);
    }

    @Test
    public void testMiniPanelHasCorrectTextFieldCount() {
        assertEquals(0, Arrays.stream(miniComponent.getComponents()).filter(component -> component instanceof JTextField).count());
    }

    @Test
    public void testMiniPanelHasCorrectButtonCount() {
        assertEquals(2, Arrays.stream(miniComponent.getComponents()).filter(component -> component instanceof AbstractButton).count());
    }

    @Test
    public void testExpandedPanelHasCorrectTextFieldCount() {
        assertEquals(2, Arrays.stream(expandedComponent.getComponents()).filter(component -> component instanceof JTextField).count());
    }

    @Test
    public void testExpandedPanelHasCorrectButtonCount() {
        assertEquals(2, Arrays.stream(expandedComponent.getComponents()).filter(component -> component instanceof AbstractButton).count());
    }
}
