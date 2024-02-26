package org.jhotdraw.samples.svg.gui;

import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.*;

public class ViewToolbarTest {
	@Test
	public void testCreateDisclosedComponent() {
		ViewToolBar toolBar = new ViewToolBar();
		
		toolBar.setView(new DefaultDrawingView());
		toolBar.editor = new DefaultDrawingEditor();
		
		JComponent state1 = toolBar.createDisclosedComponent(1);
		JComponent state2 = toolBar.createDisclosedComponent(2);
		
		{ // limit scope of state3
			JComponent state3 = toolBar.createDisclosedComponent(3);
			assertNull(state3);
		}
		
		
		assertNotNull(state1);
		assertNotNull(state2);
	}
}
