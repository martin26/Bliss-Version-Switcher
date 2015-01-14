/* 
 * Copyright 2013-2015 Jonathan Vasquez <jvasquez1011@gmail.com>
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AboutWindow extends JFrame {
	public AboutWindow(String name, String version, String releaseDate, String author, String contact, String license, JButton about) {
		super("About");
		
		// Set window properties
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// Bring in the table manager and entry table resources
		this.name = name;
		this.version = version;
		this.releaseDate = releaseDate;
		this.author = author;
		this.contact = contact;
		this.license = license;
		this.about = about;
		
		// Create components and listeners
		JButton close = new JButton("Close");
		
		close.addActionListener(new CloseListener());
		
		JLabel nameL = new JLabel("Name:");
		JLabel versionL = new JLabel("Version:");
		JLabel releaseDateL = new JLabel("Released:");
		JLabel authorL = new JLabel("Author:");
		JLabel contactL = new JLabel("Contact:");
		JLabel licenseL = new JLabel("License:");
		
		JTextField nameTF = new JTextField(name);
		JTextField versionTF = new JTextField(version);
		JTextField releaseDateTF = new JTextField(releaseDate);
		JTextField authorTF = new JTextField(author);
		JTextField contactTF = new JTextField(contact);
		JTextField licenseTF = new JTextField(license);
		
		// Create the layout and add the components to their respective places
		JPanel centerPanel = new JPanel(new GridLayout(6,2));
		JPanel southPanel = new JPanel(new GridLayout(1,2));
		
		southPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

		getContentPane().add(BorderLayout.CENTER, centerPanel);
		getContentPane().add(BorderLayout.SOUTH, southPanel);
		
		southPanel.add(close);
		
		centerPanel.add(nameL);
		centerPanel.add(nameTF);
		centerPanel.add(versionL);
		centerPanel.add(versionTF);
		centerPanel.add(releaseDateL);
		centerPanel.add(releaseDateTF);
		centerPanel.add(authorL);
		centerPanel.add(authorTF);
		centerPanel.add(contactL);
		centerPanel.add(contactTF);
		centerPanel.add(licenseL);
		centerPanel.add(licenseTF);
	}
	
	private class CloseListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			dispose();
			about.setEnabled(true);
		}
	}
	
	private String name;
	private String version;
	private String releaseDate;
	private String author;
	private String contact;
	private String license;
	private JButton about;
}