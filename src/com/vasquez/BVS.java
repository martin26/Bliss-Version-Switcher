/*
 * Copyright 2013-2015 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vasquez;

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

import com.vasquez.utils.Logger;

public class BVS {
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
        mainFrame.setSize(480,277);
        mainFrame.setLocationRelativeTo(null); // Center the application
        mainFrame.setResizable(false);

        // Create File Switcher
        fs = new FileSwitcher();

        // Buttons on South
        JButton add = new JButton("Add");
        JButton delete = new JButton("Delete");
        JButton modify = new JButton("Modify");
        JButton launch = new JButton("Launch");
        JButton about = new JButton("About");
        JButton copy = new JButton("Copy");
        JButton shiftDown = new JButton("Shift Down");
        JButton shiftUp = new JButton("Shift Up");

        // Button Listeners
        add.addActionListener(new AddListener());
        delete.addActionListener(new DeleteListener());
        modify.addActionListener(new ModifyListener());
        launch.addActionListener(new LaunchListener());
        about.addActionListener(new AboutListener());
        copy.addActionListener(new CopyListener());
        shiftDown.addActionListener(new ShiftDownListener());
        shiftUp.addActionListener(new ShiftUpListener());

        // Create South Panel
        GridLayout buttonGrid = new GridLayout(2,4,3,3);
        southPanel = new JPanel(buttonGrid);

        southPanel.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
        mainFrame.getContentPane().add(BorderLayout.SOUTH, southPanel);

        southPanel.add(add);
        southPanel.add(delete);
        southPanel.add(modify);
        southPanel.add(copy);
        southPanel.add(shiftUp);
        southPanel.add(shiftDown);
        southPanel.add(launch);
        southPanel.add(about);

        // Create Center Panel
        centerPanel = new JPanel();
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        mainFrame.getContentPane().add(BorderLayout.CENTER, centerPanel);

        // Table Stuff (List that contains data)
        tableManager = new EntryWithModel();
        entryTable = new JTable(tableManager.getModel());

        JScrollPane tableScroller = new JScrollPane(entryTable);
        tableScroller.setPreferredSize(new Dimension(462,180));
        tableScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        entryTable.setFillsViewportHeight(true);
        entryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entryTable.setPreferredScrollableViewportSize(new Dimension(450,180));
        entryTable.getTableHeader().setReorderingAllowed(false);
        entryTable.getTableHeader().setResizingAllowed(false);
        
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
            JDialog modifyWindow = new ModifyWindow(mainFrame, tableManager, entryTable, entryTable.getSelectedRow());

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

    private class ShiftUpListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if(entryTable.getSelectedRow() != -1) {
                int result = tableManager.shiftUp(entryTable.getSelectedRow());

                if(result != -1) {
                    entryTable.setRowSelectionInterval(result, result);
                } else {
                    entryTable.setRowSelectionInterval(0, 0);
                }
            }
        }
    }

    private class ShiftDownListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            if(entryTable.getSelectedRow() != -1) {
                int result = tableManager.shiftDown(entryTable.getSelectedRow());

                if(result != -1) {
                    entryTable.setRowSelectionInterval(result, result);
                } else {
                    entryTable.setRowSelectionInterval(tableManager.getSize()-1, tableManager.getSize()-1);
                }
            }
        }
    }

    // GUI Components
    private JFrame mainFrame;
    private JPanel southPanel;
    private JPanel centerPanel;
    private JTable entryTable;
    private EntryWithModel tableManager;

    // File Switcher
    private FileSwitcher fs;

    // Program Information
    private String name = "Bliss Version Switcher";
    private String version = "1.3.0";
    private String releaseDate = "Thursday, May 14, 2015";
    private String author = "Jonathan Vasquez";
    private String contact = "JVasquez1011@Gmail.com";
    private String license = "Mozilla Public License 2.0";
}
