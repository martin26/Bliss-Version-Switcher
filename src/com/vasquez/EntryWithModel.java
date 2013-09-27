/* 
 * Copyright (C) 2013 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * 
 * */

package com.vasquez;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class EntryWithModel {
	public EntryWithModel() {
		entriesFile = "Entries.txt";
		
		list = new ArrayList<Entry>();
		loadData();
		peModel = new EntryModel();
	}
    
    public void addEntry(String version, String path, String flags, boolean expansion) {
    	peModel.addEntry(version, path, flags, expansion);
    }
    
    public void modifyEntry(String version, String path, String flags, boolean expansion, int e) {
    	peModel.modifyEntry(version, path, flags, expansion, e);
    }
    
    public Entry delEntry(int e) {
    	return peModel.delEntry(e);
    }
    
    public Entry getEntry(int i) {
    	return list.get(i);
    }
    
    public Object getValueAt(int row, int col) {
    	return peModel.getValueAt(row, col);
    }
    
    public String getSelectedVersion(int row) {
    	return peModel.getSelectedVersion(row);
    }
    
    public String getSelectedPath(int row) {
    	return peModel.getSelectedPath(row);
    }
    
    public String getSelectedFlags(int row) {
    	return peModel.getSelectedFlags(row);
    }
    
	public boolean isSelectedExpansion(int row) {
		return peModel.isSelectedExpansion(row);
	}
    
    public void printList() {
    	System.out.println("Version\tPath\tFlags");
    	
    	for(Entry e: list) {
    		System.out.println(e.getVersion() + "\t" + e.getPath() + "\t" + e.getFlags());
    	}
    }
    
    public int getSize() {
    	return list.size();
    }
    
    // Gets a multi-dimensional representation of the ArrayList
    public Object[][] getData() {
    	Object[][] data = new Object[list.size()][];
    	
    	for(int i = 0; i < list.size(); i++) {
    		data[i] = list.get(i).toObjectArray();
    	}
    	
    	return data;
    }
    
    public int saveData() {
    	try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(entriesFile));
			
			for(Entry entry: list) {
				bw.write(entry.getVersion() + ";" + entry.isExpansion() + ";" + entry.getPath() + ";" + entry.getFlags() + "\r\n");
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return 0;
    }
    
    public int loadData() {
    	// Make sure we start off with a fresh list
    	list.clear();
    	
    	try {
    		
    		// Make sure that the Entries.txt file exists (Even if it is empty)
    		File ent = new File(entriesFile);
    		
    		if(!ent.exists()) {
    			ent.createNewFile();
    		}
    		
			BufferedReader br = new BufferedReader(new FileReader(entriesFile));
			
			String line;
			
			while((line = br.readLine()) != null) {
				String[] result = line.split(";");
		
				list.add(new Entry(result[0], result[2], result[3], Boolean.parseBoolean(result[1])));
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return 0;
    }
    
    public EntryModel getModel() {
    	return peModel;
    }
    
    public ArrayList<Entry> getList() {
        return list;
    }
    
	private class EntryModel extends AbstractTableModel {
    	public int getColumnCount() {
    		return columnNames.length;
    	}

    	public int getRowCount() {
    		return list.size();
    	}
    	
    	public String getColumnName(int col) {
    		return columnNames[col];
    	}

    	public Object getValueAt(int row, int col) {
    	
    		Entry e = list.get(row);
    		
    		switch(col) {
    		case 0:
    			return e.getVersion();
    		case 1:
    			return e.isExpansion();
    		case 2:
    			return e.getPath();
    		case 3:
    			return e.getFlags();
    		default:
    			return "error";
    		}	
    	}
    	
    	// Get the class type of the column so that we can get the check boxes to render correctly
		public Class getColumnClass(int c) {
              return getValueAt(0, c).getClass();
          }
    	
    	public String getSelectedVersion(int row) {
    		return list.get(row).getVersion();	
    	}
    	
    	public String getSelectedPath(int row) {
    		return list.get(row).getPath();	
    	}
    	
    	public String getSelectedFlags(int row) {
    		return list.get(row).getFlags();
    	}
    	
    	public boolean isSelectedExpansion(int row) {
    		return list.get(row).isExpansion();
    	}
    	
    	public void addEntry(String version, String path, String flags, boolean expansion) {
    	     list.add(new Entry(version, path, flags, expansion));
    	     saveData();
    	     fireTableDataChanged();
    	}
    	
    	public void modifyEntry(String version, String path, String flags, boolean expansion, int e) {
    		Entry t = list.get(e);
    		
    		t.setVersion(version);
    		t.setExpansion(expansion);
    		t.setPath(path);
    		t.setFlags(flags);
    		
   	     	saveData();
   	     	fireTableDataChanged();
    	}
    	
	   public Entry delEntry(int e) {
	    	int entry = e;
	    	
	    	if(entry != -1) {
	    		Entry r = list.remove(entry);
	    		saveData();
	    		fireTableDataChanged();
	    		
	    		return r;		
	    	}
	    	
	    	// Returns null if some error happened
	    	return null;
	    }	
    }
    
    private String entriesFile;
    private ArrayList<Entry> list;
	private String[] columnNames = {"Version", "Exp", "Path", "Flags"};
	private EntryModel peModel;
}
