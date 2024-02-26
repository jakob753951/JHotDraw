/*
 * @(#)CanvasToolBar.java
 *
 * Copyright (c) 2007-2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.gui;

import dk.sdu.mmmi.featuretracer.lib.FeatureEntryPoint;
import org.jhotdraw.gui.action.ButtonFactory;
import org.jhotdraw.gui.plaf.palette.PaletteFormattedTextFieldUI;
import org.jhotdraw.gui.plaf.palette.PaletteButtonUI;

import java.awt.*;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.DefaultFormatterFactory;

import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.GridConstrainer;
import org.jhotdraw.gui.JLifeFormattedTextField;
import org.jhotdraw.formatter.JavaNumberFormatter;
import org.jhotdraw.util.*;
import org.jhotdraw.util.prefs.PreferencesUtil;

/**
 * ViewToolBar.
 * <p>
 * Note: you must explicitly set the view before createDisclosedComponents is
 * called for the first time.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ViewToolBar extends AbstractToolBar {

    private static final long serialVersionUID = 1L;
    private DrawingView view;

    private static final ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.svg.Labels");
    /**
     * Creates new instance.
     */
    public ViewToolBar() {
        setName(labels.getString(getID() + ".toolbar"));
        setDisclosureStateCount(3);
    }

    public void setView(DrawingView view) {
        this.view = view;
        prefs = PreferencesUtil.userNodeForPackage(getClass());
        GridConstrainer constrainer = (GridConstrainer) view.getVisibleConstrainer();
        constrainer.setHeight(prefs.getDouble("view.gridSize", 8d));
        constrainer.setWidth(prefs.getDouble("view.gridSize", 8d));
    }

    @Override
    @FeatureEntryPoint("ViewToolbar")
    protected JComponent createDisclosedComponent(int state) {
        // Abort if no editor is set
        if (editor == null) {
            return null;
        }

        return switch (state) {
            case 1 -> miniPanel();
            case 2 -> expandedPanel();
            default -> null;
        };
    }

    private JPanel miniPanel() {
        // Toggle Grid Button
        AbstractButton gridButton = getGridButton();
        GridBagConstraints gridButtonConstraints = getGridButtonConstraints();

        // Zoom button
        AbstractButton zoomButton = getZoomButton(gridButton.getPreferredSize().height);
        GridBagConstraints zoomButtonConstraints = getZoomButtonConstraints();

        
        // JPanel
        JPanel panel = setupJPanel();
        panel.add(gridButton, gridButtonConstraints);
        panel.add(zoomButton, zoomButtonConstraints);
        return panel;
    }

    private JPanel expandedPanel() {
        // Grid size field
        JLifeFormattedTextField gridSizeField = getGridSizeField();
        GridBagConstraints gridSizeFieldConstraints = getGridSizeFieldConstraints();

        // Toggle grid button
        AbstractButton gridButton = getGridButton();
        GridBagConstraints gridButtonConstraints = getGridButtonConstraints();

        // Zoom factor field
        JLifeFormattedTextField scaleFactorField = getScaleFactorField();
        GridBagConstraints scaleFactorFieldConstraints = getScaleFactorFieldConstraints();

        // Zoom button
        AbstractButton zoomButton = getZoomButton(scaleFactorField.getPreferredSize().height);
        GridBagConstraints zoomButtonConstraints = getZoomButtonConstraints();
        
        
        // JPanel
        JPanel panel = setupJPanel();
        panel.add(gridSizeField, gridSizeFieldConstraints);
        panel.add(gridButton, gridButtonConstraints);
        panel.add(scaleFactorField, scaleFactorFieldConstraints);
        panel.add(zoomButton, zoomButtonConstraints);
        return panel;
    }

    private AbstractButton getGridButton() {
        AbstractButton gridButton = ButtonFactory.createToggleGridButton(view);
        gridButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(gridButton));
        labels.configureToolBarButton(gridButton, "alignGrid");
        return gridButton;
    }

    private JLifeFormattedTextField getGridSizeField() {
        JLifeFormattedTextField gridSizeField = new JLifeFormattedTextField();
        gridSizeField.setColumns(4);
        gridSizeField.setToolTipText(labels.getString("view.gridSize.toolTipText"));
        gridSizeField.setHorizontalAlignment(JLifeFormattedTextField.RIGHT);
        gridSizeField.putClientProperty("Palette.Component.segmentPosition", "first");
        gridSizeField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(gridSizeField));
        gridSizeField.setFormatterFactory(JavaNumberFormatter.createFormatterFactory(0d, 1000d, 1d, true));
        gridSizeField.setHorizontalAlignment(JTextField.LEADING);
        final GridConstrainer constrainer = (GridConstrainer) view.getVisibleConstrainer();
        gridSizeField.addPropertyChangeListener(evt -> {
            if ("value".equals(evt.getPropertyName())) {
                if (evt.getNewValue() != null) {
                    constrainer.setWidth((Double) evt.getNewValue());
                    constrainer.setHeight((Double) evt.getNewValue());
                    prefs = PreferencesUtil.userNodeForPackage(getClass());
                    try {
                        prefs.putDouble("view.gridSize", (Double) evt.getNewValue());
                    } catch (IllegalStateException e) { //ignore
                    }
                    view.getComponent().repaint();
                }
            }
        });
        gridSizeField.setValue(constrainer.getHeight());
        return gridSizeField;
    }

    private JLifeFormattedTextField getScaleFactorField() {
        final JLifeFormattedTextField scaleFactorField = new JLifeFormattedTextField();
        scaleFactorField.setColumns(4);
        scaleFactorField.setToolTipText(labels.getString("view.zoomFactor.toolTipText"));
        scaleFactorField.setHorizontalAlignment(JLifeFormattedTextField.RIGHT);
        scaleFactorField.putClientProperty("Palette.Component.segmentPosition", "first");
        scaleFactorField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(scaleFactorField));
        scaleFactorField.setHorizontalAlignment(JTextField.LEADING);
        scaleFactorField.setValue(view.getScaleFactor());
        scaleFactorField.addPropertyChangeListener(evt -> {
            if ("value".equals(evt.getPropertyName())) {
                if (evt.getNewValue() != null) {
                    view.setScaleFactor((Double) evt.getNewValue());
                }
            }
        });
        JavaNumberFormatter formatter = new JavaNumberFormatter(0.01d, 50d, 100d, false, "%");
        formatter.setUsesScientificNotation(false);
        formatter.setMaximumFractionDigits(1);
        scaleFactorField.setFormatterFactory(new DefaultFormatterFactory(formatter));
        view.addPropertyChangeListener(evt -> {
            if (Objects.equals(evt.getPropertyName(), DrawingView.SCALE_FACTOR_PROPERTY)) {
                if (evt.getNewValue() != null) {
                    scaleFactorField.setValue(evt.getNewValue());
                }
            }
        });
        return scaleFactorField;
    }

    private AbstractButton getZoomButton(int height) {
        AbstractButton zoomButton = ButtonFactory.createZoomButton(view);
        zoomButton.setUI((PaletteButtonUI) PaletteButtonUI.createUI(zoomButton));
        labels.configureToolBarButton(zoomButton, "view.zoomFactor");
        zoomButton.setText("100 %");
        zoomButton.setPreferredSize(new Dimension(zoomButton.getPreferredSize().width, height));
        return zoomButton;
    }

    private static GridBagConstraints getGridButtonConstraints() {
        GridBagConstraints gridButtonConstraints = new GridBagConstraints();
        gridButtonConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridButtonConstraints.gridx = 1;
        gridButtonConstraints.gridy = 0;
        return gridButtonConstraints;
    }

    private static GridBagConstraints getGridSizeFieldConstraints() {
        GridBagConstraints gridSizeFieldConstraints = new GridBagConstraints();
        gridSizeFieldConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridSizeFieldConstraints.gridx = 0;
        gridSizeFieldConstraints.gridy = 0;
        return gridSizeFieldConstraints;
    }

    private static GridBagConstraints getScaleFactorFieldConstraints() {
        GridBagConstraints scaleFactorFieldConstraints = new GridBagConstraints();
        scaleFactorFieldConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        scaleFactorFieldConstraints.gridx = 0;
        scaleFactorFieldConstraints.gridy = 1;
        scaleFactorFieldConstraints.insets = new Insets(3, 0, 0, 0);
        return scaleFactorFieldConstraints;
    }

    private static GridBagConstraints getZoomButtonConstraints() {
        GridBagConstraints zoomButtonConstraints = new GridBagConstraints();
        zoomButtonConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        zoomButtonConstraints.gridx = 1;
        zoomButtonConstraints.gridy = 1;
        zoomButtonConstraints.gridwidth = GridBagConstraints.REMAINDER;
        zoomButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        zoomButtonConstraints.insets = new Insets(3, 0, 0, 0);
        zoomButtonConstraints.weighty = 1;
        zoomButtonConstraints.weightx = 1;
        return zoomButtonConstraints;
    }

    private static JPanel setupJPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 5, 5, 8));
        panel.setLayout(new GridBagLayout());
        return panel;
    }

    @Override
    protected String getID() {
        return "view";
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setOpaque(false);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
