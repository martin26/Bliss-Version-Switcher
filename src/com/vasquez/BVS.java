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

package com.vasquez;

import com.vasquez.Windows.AboutWindow;
import com.vasquez.Windows.AddWindow;
import com.vasquez.Utilities.Logger;
import com.vasquez.Windows.ModifyWindow;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;

public class BVS {
    // GUI Components
    private JFrame mainFrame;
    private JPanel southPanel;
    private JPanel centerPanel;
    private JTable entryTable;
    private EntryWithModel tableManager;

    // File Switcher
    private FileSwitcher fs;

    // Program Information
    private final String name = "Bliss Version Switcher";
    private final String version = "1.4.3";
    private final String releaseDate = "March 4, 2017";
    private final String author = "Jonathan Vasquez";
    private final String contact = "jon@xyinn.org";
    private final String license = "2-BSD";
    
    public static void main(String[] args) {
        Logger.EnableLogging();

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
        mainFrame.setSize(500,250);
        mainFrame.setLocationRelativeTo(null); // Center the application
        mainFrame.setResizable(false);

        // Buttons on South
        JButton add = new JButton("Add");
        JButton delete = new JButton("Delete");
        JButton modify = new JButton("Modify");
        JButton launch = new JButton("Launch");
        JButton about = new JButton("About");
        JButton copy = new JButton("Copy");
        
        // Button Listeners
        add.addActionListener(new AddListener());
        delete.addActionListener(new DeleteListener());
        modify.addActionListener(new ModifyListener());
        launch.addActionListener(new LaunchListener());
        about.addActionListener(new AboutListener());
        copy.addActionListener(new CopyListener());

        // Create South Panel
        GridLayout buttonGrid = new GridLayout(1,5,3,3);
        southPanel = new JPanel(buttonGrid);
        southPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        mainFrame.getContentPane().add(BorderLayout.SOUTH, southPanel);

        southPanel.add(add);
        southPanel.add(modify);
        southPanel.add(copy);
        southPanel.add(delete);
        southPanel.add(launch);
        southPanel.add(about);

        // Create Center Panel
        centerPanel = new JPanel();
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        mainFrame.getContentPane().add(BorderLayout.CENTER, centerPanel);

        // Table Stuff (List that contains data)
        tableManager = new EntryWithModel();
        entryTable = new JTable(tableManager.getModel());

        // Create File Switcher
        fs = new FileSwitcher(tableManager);
        
        JScrollPane tableScroller = new JScrollPane(entryTable);
        tableScroller.setPreferredSize(new Dimension(480,180));
        tableScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        entryTable.setFillsViewportHeight(true);
        entryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entryTable.setPreferredScrollableViewportSize(new Dimension(450,180));
        entryTable.getTableHeader().setReorderingAllowed(false);
        entryTable.getTableHeader().setResizingAllowed(false);
        
        // Sets the width of each column

        // Last Ran
        TableColumn column = entryTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(75);
        
        // Path
        column = entryTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(600);

        // Flags
        column = entryTable.getColumnModel().getColumn(4);
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
            if(entryTable.getSelectedRow() != -1) {
                int result = tableManager.delEntry(entryTable.getSelectedRow());

                if(result != -1) {
                    entryTable.setRowSelectionInterval(result, result);
                } else if(result == -1 && tableManager.getSize() != 0) {
                    entryTable.setRowSelectionInterval(0, 0);
                }

                entryTable.repaint();
            }
        }
    }

    private class ModifyListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            showModifyWindow();
        }
    }

    private class LaunchListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if(entryTable.getSelectedRow() != -1) {
                Entry ent = tableManager.getEntry(entryTable.getSelectedRow());
                fs.setEntry(ent);
                fs.launch();
            }
        }
    }

    private class AboutListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            showAboutWindow();
        }
    }
    
    private class CopyListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if(entryTable.getSelectedRow() != -1) {
                int result = tableManager.copyEntry(entryTable.getSelectedRow());
                entryTable.setRowSelectionInterval(result, result);
            }
        }
    }
    
    private void showAddWindow() {
        JDialog addWindow = new AddWindow(mainFrame, tableManager, entryTable);
        
        addWindow.setLocation(mainFrame.getLocation());
        addWindow.setSize(400,175);
        addWindow.setVisible(true);
    }

    public void showModifyWindow() {
        if(entryTable.getSelectedRow() != -1) {
            JDialog modifyWindow = new ModifyWindow(mainFrame, tableManager, fs, entryTable, entryTable.getSelectedRow());

            modifyWindow.setLocation(mainFrame.getLocation());
            modifyWindow.setSize(400,175);
            modifyWindow.setVisible(true);
        }
    }

    private void showAboutWindow() {
        JDialog aboutWindow = new AboutWindow(mainFrame, name, version, releaseDate, author, contact, license);

        aboutWindow.setLocation(mainFrame.getLocation());
        aboutWindow.setSize(400,225);
        aboutWindow.setVisible(true);
    }
}
