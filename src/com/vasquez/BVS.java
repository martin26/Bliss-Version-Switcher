/* 
 * Copyright (C) 2013 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * 
 * */

package com.vasquez;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;

public class BVS {
	public static void main(String[] args) {
	  javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	BVS gui = new BVS();
        		gui.start();
            }
        });	
	}
	
	public void start() {
		// Create Frame
		mainFrame = new JFrame(name + " " + version);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(500,300);
		mainFrame.setLocationRelativeTo(null); // Center the application
		
		// Set UI to look native
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Buttons on South
		JButton add = new JButton("Add");
		JButton delete = new JButton("Delete");
		JButton modify = new JButton("Modify");
		JButton launch = new JButton("Launch");
		JButton about = new JButton("About");
		
		// Button Listeners
		add.addActionListener(new AddListener());
		delete.addActionListener(new DeleteListener());
		modify.addActionListener(new ModifyListener());
		launch.addActionListener(new LaunchListener());
		about.addActionListener(new AboutListener());
		
		// Create South Panel
		GridLayout buttonGrid = new GridLayout(1,4);
		southPanel = new JPanel(buttonGrid);
		
		southPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		mainFrame.getContentPane().add(BorderLayout.SOUTH, southPanel);
		
		southPanel.add(add);
		southPanel.add(delete);
		southPanel.add(modify);
		southPanel.add(launch);
		southPanel.add(about);
		
		// Create Center Panel
		centerPanel = new JPanel();
		mainFrame.getContentPane().add(BorderLayout.CENTER, centerPanel);
		
		// Table Stuff (List that contains data)
		tableManager = new EntryWithModel();
		entryTable = new JTable(tableManager.getModel());
		
		JScrollPane tableScroller = new JScrollPane(entryTable);
		tableScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			
		entryTable.setFillsViewportHeight(true);
		entryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entryTable.setPreferredScrollableViewportSize(new Dimension(450,180));
		
		// Sets the width of each column
		
		// Path
		TableColumn column = entryTable.getColumnModel().getColumn(2);
		column.setPreferredWidth(600);
		
		// Flags
		column = entryTable.getColumnModel().getColumn(3);
		column.setPreferredWidth(200);
		
		// Version
		column = entryTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(100);
		
		centerPanel.add(tableScroller);
		
		// Show the screen
		mainFrame.setVisible(true);
	}
	
	private class AddListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			showAddWindow();	
		}
	}
	
	private class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			tableManager.delEntry(entryTable.getSelectedRow());
			entryTable.repaint();
		}
	}
	
	private class ModifyListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			showModifyWindow();
		}
	}
	
	private class LaunchListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			System.out.println("Launching Diablo II...");
				
			if(entryTable.getSelectedRow() != -1 || entryTable.getSelectedColumn() != -1) {
				Entry ent = tableManager.getEntry(entryTable.getSelectedRow());
				
				FileSwitcher fs = new FileSwitcher(ent);
				
				fs.launch();
			}
		}
	}
	
	private class AboutListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			showAboutWindow();
		}
	}
	
	public void showAddWindow() {
		JFrame addWindow = new AddWindow(tableManager, entryTable);
		
		addWindow.setLocation(mainFrame.getLocation());
		addWindow.setSize(400,175);
		addWindow.setVisible(true);	
	}
	
	public void showModifyWindow() {
		JFrame modifyWindow = new ModifyWindow(tableManager, entryTable, entryTable.getSelectedRow());
		
		modifyWindow.setLocation(mainFrame.getLocation());
		modifyWindow.setSize(400,175);
		modifyWindow.setVisible(true);
	}
	
	public void showAboutWindow() {
		JFrame aboutWindow = new AboutWindow(name, version, author, contact, license);
		
		aboutWindow.setLocation(mainFrame.getLocation());
		aboutWindow.setSize(375,200);
		aboutWindow.setVisible(true);
	}

	// GUI Components
	private JFrame mainFrame;
	private JPanel southPanel;
	private JPanel centerPanel;
	private JTable entryTable;
	private EntryWithModel tableManager;
	
	// Program Information
	private String name = "Bliss Version Switcher";
	private String version = "1.0.1";
	private String author = "Jonathan Vasquez";
	private String contact = "JVasquez1011@Gmail.com";
	private String license = "MPL 2.0";
}
