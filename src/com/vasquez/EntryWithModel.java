/* 
 * Copyright 2013-2014 Jonathan Vasquez <jvasquez1011@gmail.com>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
    
    public int addEntry(String version, String path, String flags, boolean expansion) {
    	return peModel.addEntry(version, path, flags, expansion);
    }
    
    public int modifyEntry(String version, String path, String flags, boolean expansion, int e) {
    	peModel.modifyEntry(version, path, flags, expansion, e);
    	
    	return e;
    }
    
    public int delEntry(int e) {
    	return peModel.delEntry(e);
    }
    
    public Entry getEntry(int i) {
    	return list.get(i);
    }
    
    public int copyEntry(int e) {
    	return peModel.copyEntry(e);
    }
    
    public int shiftUp(int e) {
    	return peModel.shiftUp(e);
    }
    
    public int shiftDown(int e) {
    	return peModel.shiftDown(e);
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
			
			// Ending the entry with a '0' so that if the path is null, it would just assign it a null value and proceed to the 0.
			// aka, using the same technique Blizzard uses in their data/global/excel entries to terminate data lines
			for(Entry entry: list) {
				bw.write(entry.getVersion() + ";" + entry.isExpansion() + ";" + entry.getPath() + ";" + entry.getFlags() + ";0;\r\n");
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
			
			String line = null;
			
			while((line = br.readLine()) != null) {
				String[] result = line.split(";");
				
				try {
					list.add(new Entry(result[0], result[2], result[3], Boolean.parseBoolean(result[1])));
				}
				catch(Exception e) {
					System.out.println("Corrupted File. Recreating...");
					
					// Closing the buffer so that we can delete the file, then reopening it.
					br.close();
					
					if(ent.exists() && ent.delete()) {
						ent.createNewFile();
					}
					
					br = new BufferedReader(new FileReader(entriesFile));
				}
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
    	
    	public int addEntry(String version, String path, String flags, boolean expansion) {
    	     list.add(new Entry(version, path, flags, expansion));
    	      
    	     saveData();
    	     fireTableDataChanged();
    	     
    	     return list.size()-1;
    	}
    	
    	public int modifyEntry(String version, String path, String flags, boolean expansion, int e) {
    		Entry t = list.get(e);
    		
    		t.setVersion(version);
    		t.setExpansion(expansion);
    		t.setPath(path);
    		t.setFlags(flags);
    		
   	     	saveData();
   	     	fireTableDataChanged();
   	     	
   	     	return e;
    	}
    	
	    public int delEntry(int entry) {	
	    	if(entry != -1) {
	    		list.remove(entry);
	    		saveData();
	    		fireTableDataChanged();
	    		
	    		return entry - 1;		
	    	}
	    	
	    	// Returns null if some error happened
	    	return -1;
	    }
	   
	    // Copies the entry that is passed to this method and then inserts it into the list
		public int copyEntry(int entry) {
	    	int next = entry + 1;
	   
	    	if(entry != -1) {
	    		Entry oldEntry = list.get(entry);
	    		Entry newEntry = new Entry(oldEntry.getVersion(), oldEntry.getPath(), oldEntry.getFlags(), oldEntry.isExpansion());
	    		
	    		list.add(next, newEntry);
	    		
	    		saveData();
	    		fireTableDataChanged();
	    		
	    		return next;
	    	} else { return -1; }
	    }
		
		public int shiftUp(int entry) {
			int previous = entry - 1;
			
			if(previous >= 0) {
				swap(entry, previous);
				return previous;
			} else { return -1; }
		}
		
		public int shiftDown(int entry) {
			int next = entry + 1;
			
			if(next < list.size()) {
				swap(entry, next);
				return next;
			} else { return -1; }
		}
		
		// Swaps two values
		private void swap(int source, int target) {
			Entry temp = list.get(target);
			list.set(target, list.get(source));
			list.set(source, temp);
			saveData();
    		fireTableDataChanged();
		}
    }
	
    private String entriesFile;
    private ArrayList<Entry> list;
	private String[] columnNames = {"Version", "Exp", "Path to \"Game.exe\"", "Flags"};
	private EntryModel peModel;
}