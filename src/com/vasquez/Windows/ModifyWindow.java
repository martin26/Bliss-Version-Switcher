/*
 * Copyright 2013-2017 Jonathan Vasquez <jon@xyinn.org>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.vasquez.Windows;

import com.vasquez.EntryWithModel;
import com.vasquez.FileSwitcher;
import com.vasquez.Listing;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ModifyWindow extends JDialog {
    private EntryWithModel tableManager;
    private JTable entryTable;
    private FileSwitcher fs;
    private JComboBox version;
    private JTextField path;
    private JTextField flags;
    private JCheckBox expansion;
    private JCheckBox wasLastRan;
    private boolean originalWasLastRan;
    
    private int selectedEntry;
    private int versionIndex;
    
    public ModifyWindow(JFrame mainWindow, EntryWithModel tableManager, FileSwitcher fs, JTable entryTable, int selectedEntry) {
        super(mainWindow, "Modify Entry", Dialog.ModalityType.DOCUMENT_MODAL);

        // Set window properties
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Bring in the table manager and entry table resources
        this.tableManager = tableManager;
        this.entryTable = entryTable;
        this.selectedEntry = selectedEntry;
        this.fs = fs;
		
        // Create components and listeners
        JButton find = new JButton("Set Path");
        JButton modify = new JButton("Modify");
        JButton cancel = new JButton("Cancel");

        find.addActionListener(new FindGameListener(this));
        modify.addActionListener(new modifyListener());
        cancel.addActionListener(new cancelListener());

        JLabel versionL = new JLabel("Version:");
        JLabel pathL = new JLabel("Path (Game.exe):");
        JLabel flagsL = new JLabel("Flags:");

        path = new JTextField(tableManager.getSelectedPath(selectedEntry));
        flags = new JTextField(tableManager.getSelectedFlags(selectedEntry));
        expansion = new JCheckBox("Expansion", tableManager.isSelectedExpansion(selectedEntry));
        wasLastRan = new JCheckBox("Last Ran", tableManager.wasLastRan(selectedEntry));
        
        // Used to decide whether or not we need to reset the
        // last ran entry in the file switcher
        originalWasLastRan = wasLastRan.isSelected();
        		
        path.setEditable(false);
          
        if(expansion.isSelected()) {
            version = new JComboBox(Listing.expansionVersions);
        } else {
            version = new JComboBox(Listing.classicVersions);
        }

        expansion.addItemListener(new ExpansionListener());

        // Find the information after you get the 'expansion' check box value
        versionIndex = getVersionIndex(tableManager.getSelectedVersion(selectedEntry));

        if(versionIndex != -1) {
            version.setSelectedIndex(versionIndex);
        } else {
            version.getEditor().setItem(tableManager.getSelectedVersion(entryTable.getSelectedRow()));
        }

        // Create the layout and add the components to their respective places
        JPanel centerPanel = new JPanel(new GridLayout(3,2));
        JPanel southPanel = new JPanel(new GridLayout(1,2,3,3));
        southPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

        getContentPane().add(BorderLayout.CENTER, centerPanel);
        getContentPane().add(BorderLayout.SOUTH, southPanel);

        JPanel checkBoxes = new JPanel();
        JPanel buttons = new JPanel();
        
        buttons.setLayout(new GridLayout(2,3));
        
        buttons.add(find);
        buttons.add(new JLabel("")); // This is just padding for the grid to have an empty element
        buttons.add(modify);
        buttons.add(cancel);
        
        checkBoxes.setLayout(new BoxLayout(checkBoxes, BoxLayout.PAGE_AXIS));
        checkBoxes.add(expansion);
        checkBoxes.add(wasLastRan);
        
        southPanel.add(checkBoxes);
        southPanel.add(buttons);

        centerPanel.add(versionL);
        centerPanel.add(version);
        centerPanel.add(pathL);
        centerPanel.add(path);
        centerPanel.add(flagsL);
        centerPanel.add(flags);
    }

    private class modifyListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if(!version.getSelectedItem().toString().isEmpty() && !path.getText().isEmpty()) {
                tableManager.modifyEntry(version.getSelectedItem().toString(), path.getText(),
                		                 flags.getText(), expansion.isSelected(), wasLastRan.isSelected(), selectedEntry);
                
                if(originalWasLastRan != wasLastRan.isSelected()) {
                	if(!wasLastRan.isSelected()) {
                		fs.resetLastRanEntry();
                	}
                	else {
                		fs.setLastRanEntry(tableManager.getEntry(selectedEntry));
                	}
                }
                
                entryTable.setRowSelectionInterval(selectedEntry, selectedEntry);
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
    
    private class FindGameListener implements ActionListener {
        private JDialog parentWindow;
        
        public FindGameListener(JDialog window) {
            parentWindow = window;
        }
        
        public void actionPerformed(ActionEvent ev) {
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Select Game.exe Only", "exe");
            fc.setFileFilter(filter);
            fc.setCurrentDirectory(new File("C:\\"));
            
            int result = fc.showOpenDialog(parentWindow);
            
            if(result == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                path.setText(file.getAbsolutePath());
            }
        }
    }

    private class ExpansionListener implements ItemListener {
        public void itemStateChanged(ItemEvent arg0) {
            DefaultComboBoxModel d = null;

            if(tableManager.isSelectedExpansion(selectedEntry) && expansion.isSelected()) {
                d = new DefaultComboBoxModel(Listing.expansionVersions);
                version.setModel(d);

                if(versionIndex != -1) {
                    version.setSelectedIndex(versionIndex);
                } else {
                    version.getEditor().setItem(tableManager.getSelectedVersion(entryTable.getSelectedRow()));
                }
            } else if (tableManager.isSelectedExpansion(selectedEntry) && !expansion.isSelected()){
                d = new DefaultComboBoxModel(Listing.classicVersions);
                version.setModel(d);
                version.setSelectedIndex(0);
            } else if (!tableManager.isSelectedExpansion(selectedEntry) && expansion.isSelected()){
                d = new DefaultComboBoxModel(Listing.expansionVersions);
                version.setModel(d);
                version.setSelectedIndex(0);
            } else if (!tableManager.isSelectedExpansion(selectedEntry) && !expansion.isSelected()){
                d = new DefaultComboBoxModel(Listing.classicVersions);
                version.setModel(d);

                if(versionIndex != -1) {
                    version.setSelectedIndex(versionIndex);
                } else {
                    version.getEditor().setItem(tableManager.getSelectedVersion(entryTable.getSelectedRow()));
                }
            }
        }
    }

    // Returns the index of the version you are trying to modify in the ComboBox
    private int getVersionIndex(String x) {
        if(expansion.isSelected()) {
            for(int i = 0; i < Listing.expansionVersions.length; i++) {
                if(Listing.expansionVersions[i].equalsIgnoreCase(tableManager.getSelectedVersion(selectedEntry))) {
                    return i;
                }
            }
        } else {
            for(int i = 0; i < Listing.classicVersions.length; i++) {
                if(Listing.classicVersions[i].equalsIgnoreCase(tableManager.getSelectedVersion(selectedEntry))) {
                    return i;
                }
            }
        }

        return -1;
    }
}
