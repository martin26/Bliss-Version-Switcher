/*
 * Copyright 2013-2017 Jonathan Vasquez <jon@xyinn.org>
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.vasquez.Windows;

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
