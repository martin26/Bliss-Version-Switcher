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

package com.vasquez;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AboutWindow extends JDialog {
    public AboutWindow(JFrame mainWindow, String name, String version, String releaseDate, String author, String contact, String license) {
        super(mainWindow, "About", Dialog.ModalityType.DOCUMENT_MODAL);

        // Set window properties
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Bring in the table manager and entry table resources
        this.name = name;
        this.version = version;
        this.releaseDate = releaseDate;
        this.author = author;
        this.contact = contact;
        this.license = license;

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
        }
    }

    private String name;
    private String version;
    private String releaseDate;
    private String author;
    private String contact;
    private String license;
}
