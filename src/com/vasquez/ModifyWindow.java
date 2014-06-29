/* 
 * Copyright 2013-2014 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vasquez;

import java.awt.BorderLayout;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class ModifyWindow extends JFrame {
	public ModifyWindow(EntryWithModel tableManager, JTable entryTable, int selectedEntry, JButton mainModifyButton) {
		super("Modify Entry");
		
		// Set window properties
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// Bring in the table manager and entry table resources
		this.tableManager = tableManager;
		this.entryTable = entryTable;
		this.selectedEntry = selectedEntry;
		this.mainModifyButton = mainModifyButton;
		
		// Create components and listeners
		JButton modify = new JButton("Modify");
		JButton cancel = new JButton("Cancel");
		
		modify.addActionListener(new modifyListener());
		cancel.addActionListener(new cancelListener());
		
		JLabel versionL = new JLabel("Version:");
		JLabel pathL = new JLabel("Path:");
		JLabel flagsL = new JLabel("Flags:");
		
		path = new JTextField(tableManager.getSelectedPath(selectedEntry));
		flags = new JTextField(tableManager.getSelectedFlags(selectedEntry));
		expansion = new JCheckBox("Expansion", tableManager.isSelectedExpansion(selectedEntry));
			
		if(expansion.isSelected()) {
			version = new JComboBox(Listing.expansionVersions);
		} else {
			version = new JComboBox(Listing.classicVersions);
		}
	
		expansion.addItemListener(new ExpansionListener());
		
		// Find the information after you get the 'expansion' check box value
		versionIndex = getVersionIndex(tableManager.getSelectedVersion(selectedEntry));
		
		// Makes the combo box editable so that the user can enter their own versions (New versions that Blizzard might release)
		version.setEditable(true);
		
		if(versionIndex != -1) {
			version.setSelectedIndex(versionIndex);
		} else {
			version.getEditor().setItem(tableManager.getSelectedVersion(entryTable.getSelectedRow()));
		}
		
		// Create the layout and add the components to their respective places
		JPanel centerPanel = new JPanel(new GridLayout(3,2));
		JPanel southPanel = new JPanel(new GridLayout(1,2));
		
		southPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

		getContentPane().add(BorderLayout.CENTER, centerPanel);
		getContentPane().add(BorderLayout.SOUTH, southPanel);
		
		southPanel.add(expansion);
		southPanel.add(modify);
		southPanel.add(cancel);
			
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
				tableManager.modifyEntry(version.getSelectedItem().toString(), path.getText(), flags.getText(), expansion.isSelected(), selectedEntry);
				entryTable.setRowSelectionInterval(selectedEntry, selectedEntry);
			}
			
			entryTable.repaint();
			dispose();
			mainModifyButton.setEnabled(true);
		}
	}
	
	private class cancelListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			dispose();
			mainModifyButton.setEnabled(true);
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
	
	private EntryWithModel tableManager;
	private JTable entryTable;
	private JComboBox version;
	private JTextField path;
	private JTextField flags;
	private JCheckBox expansion;
	private JButton mainModifyButton;
	private int selectedEntry;
	private int versionIndex;
}