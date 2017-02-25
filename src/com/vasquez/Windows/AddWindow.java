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

public class AddWindow extends JDialog {
    protected EntryWithModel tableManager;
    protected JTable entryTable;
    protected JComboBox version;
    protected JTextField path;
    protected JTextField flags;
    protected JCheckBox expansion;
    
    public AddWindow(JFrame mainWindow, EntryWithModel tableManager, JTable entryTable) {
        super(mainWindow, "Add Entry", Dialog.ModalityType.DOCUMENT_MODAL);

        // Set window properties
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Bring in the table manager and entry table resources
        this.tableManager = tableManager;
        this.entryTable = entryTable;

        // Create components and listeners
        JButton find = new JButton("Set Path");
        JButton add = new JButton("Add");
        JButton cancel = new JButton("Cancel");

        find.addActionListener(new FindGameListener(this));
        add.addActionListener(new addListener()); 
        cancel.addActionListener(new cancelListener());

        JLabel versionL = new JLabel("Version:");
        JLabel pathL = new JLabel("Path (Game.exe):");
        JLabel flagsL = new JLabel("Flags:");

        version = new JComboBox(Listing.classicVersions);
        path = new JTextField();
        flags = new JTextField();
        expansion = new JCheckBox("Expansion");
    
        path.setEditable(false);
        
        expansion.addItemListener(new ExpansionListener());
        version.setSelectedIndex(0);

        // Create the layout and add the components to their respective places
        JPanel centerPanel = new JPanel(new GridLayout(3,2));
        JPanel southPanel = new JPanel(new GridLayout(1,2));

        southPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

        getContentPane().add(BorderLayout.CENTER, centerPanel);
        getContentPane().add(BorderLayout.SOUTH, southPanel);

        southPanel.add(expansion);
        southPanel.add(find);
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
}
