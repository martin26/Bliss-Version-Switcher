/*
 * Copyright 2013-2015 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vasquez;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class AddWindow extends JDialog {
    public AddWindow(JFrame mainWindow, EntryWithModel tableManager, JTable entryTable) {
        super(mainWindow, "Add Entry", Dialog.ModalityType.DOCUMENT_MODAL);

        // Set window properties
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Bring in the table manager and entry table resources
        this.tableManager = tableManager;
        this.entryTable = entryTable;

        // Create components and listeners
        JButton add = new JButton("Add");
        JButton cancel = new JButton("Cancel");

        add.addActionListener(new addListener());
        cancel.addActionListener(new cancelListener());

        JLabel versionL = new JLabel("Version:");
        JLabel pathL = new JLabel("Path:");
        JLabel flagsL = new JLabel("Flags:");

        version = new JComboBox(Listing.classicVersions);
        path = new JTextField();
        flags = new JTextField();
        expansion = new JCheckBox("Expansion");

        expansion.addItemListener(new ExpansionListener());
        version.setEditable(true);
        version.setSelectedIndex(0);

        // Create the layout and add the components to their respective places
        JPanel centerPanel = new JPanel(new GridLayout(3,2));
        JPanel southPanel = new JPanel(new GridLayout(1,2));

        southPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

        getContentPane().add(BorderLayout.CENTER, centerPanel);
        getContentPane().add(BorderLayout.SOUTH, southPanel);

        southPanel.add(expansion);
        southPanel.add(add);
        southPanel.add(cancel);

        centerPanel.add(versionL);
        centerPanel.add(version);
        centerPanel.add(pathL);
        centerPanel.add(path);
        centerPanel.add(flagsL);
        centerPanel.add(flags);
    }

    private class addListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            // Used to set the row to the new value added to the list
            int result = 0;

            if(!version.getSelectedItem().toString().isEmpty() && !path.getText().isEmpty()) {
                result = tableManager.addEntry(version.getSelectedItem().toString(), path.getText(), flags.getText(), expansion.isSelected());
                entryTable.setRowSelectionInterval(result, result);
            }

            entryTable.repaint();
            dispose();
        }
    }

    private class cancelListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
        	dispose();
        }
    }
    
    private class ExpansionListener implements ItemListener {
        public void itemStateChanged(ItemEvent arg0) {
            DefaultComboBoxModel d = null;
            if(expansion.isSelected()) {
                d = new DefaultComboBoxModel(Listing.expansionVersions);
                version.setModel(d);
                version.setSelectedIndex(0);
            } else {
                d = new DefaultComboBoxModel(Listing.classicVersions);
                version.setModel(d);
                version.setSelectedIndex(0);
            }
        }
    }

    private EntryWithModel tableManager;
    private JTable entryTable;
    private JComboBox version;
    private JTextField path;
    private JTextField flags;
    private JCheckBox expansion;
}
