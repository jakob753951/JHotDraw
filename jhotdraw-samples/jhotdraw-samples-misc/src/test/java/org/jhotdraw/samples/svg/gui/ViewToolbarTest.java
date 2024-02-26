package org.jhotdraw.samples.svg.gui;

import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;

import java.awt.*;
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
        Component[] components = miniComponent.getComponents();
        long textFieldCount = Arrays.stream(components).filter(component -> component instanceof JTextField).count();
        assertEquals(0, textFieldCount);
    }

    @Test
    public void testMiniPanelHasCorrectButtonCount() {
        Component[] components = miniComponent.getComponents();
        long buttonCount = Arrays.stream(components).filter(component -> component instanceof AbstractButton).count();
        assertEquals(2, buttonCount);
    }

    @Test
    public void testExpandedPanelHasCorrectTextFieldCount() {
        Component[] components = expandedComponent.getComponents();
        long textFieldCount = Arrays.stream(components).filter(component -> component instanceof JTextField).count();
        assertEquals(2, textFieldCount);
    }

    @Test
    public void testExpandedPanelHasCorrectButtonCount() {
        Component[] components = expandedComponent.getComponents();
        long buttonCount = Arrays.stream(components).filter(component -> component instanceof AbstractButton).count();
        assertEquals(2, buttonCount);
    }
}
