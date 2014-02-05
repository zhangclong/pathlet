/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.code.pathlet.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * Utility for scanning a directory for added, removed and changed
 * files and reporting these events via registered Listeners.
 *
 * @author Charlie Zhang
 * @since 2011-04-29
 */
public class ModificationScanner
{	
    private long scanInterval;  //scan cycle time interval in millisecond.
    private List<Listener> listeners = Collections.synchronizedList(new ArrayList<Listener>());
    private Map<String, Long> prevScan = Collections.emptyMap();
    private FilenameFilter filter;
    private File[] scanDirs;
    private volatile boolean running = false;
    private boolean reportExisting = true;
    private Timer timer;
    private TimerTask task;
    private boolean recursive=true;

    /**
     * Listener
     * 
     * Marker for notifications re file changes.
     */
    public interface Listener
    {
    }

    
    public interface DiscreteListener extends Listener
    {
    	/**
    	 * Be triggered after found one file changed.
    	 * 
    	 * @param filename Canonical Path of the file
    	 * @throws Exception
    	 */
        public void fileChanged (String filename) throws Exception;
        
    	/**
    	 * Be triggered after found one file added.
    	 * 
    	 * @param filename Canonical Path of the file
    	 * @throws Exception
    	 */
        public void fileAdded (String filename) throws Exception;
        
    	/**
    	 * Be triggered after found one file removed.
    	 * 
    	 * @param filename Canonical Path of the file
    	 * @throws Exception
    	 */
        public void fileRemoved (String filename) throws Exception;
    }
    
    
    public interface BulkListener extends Listener
    {
        public void filesChanged (List<String> filenames) throws Exception;
    }


    /**
     * 
     */
    public ModificationScanner ()
    {       
    }

    /**
     * Get the scan interval
     * @return interval between scans in millisecond
     */
    public long getScanInterval()
    {
        return scanInterval;
    }

    /**
     * Set the scan interval
     * @param scanInterval pause between scans in millisecond
     */
    public synchronized void setScanInterval(long scanInterval)
    {
        this.scanInterval = scanInterval;
        
        if (running)
        {
            stop();

            timer = newTimer();
            task = newTimerTask();

            schedule(timer, task);
            running = true;
        }
    }


    public void setScanDirs (File[] dirs)
    {
        this.scanDirs = dirs;
    }
    
    public File[] getScanDirs ()
    {
        return this.scanDirs;
    }
    
    public void setRecursive (boolean recursive)
    {
        this.recursive=recursive;
    }
    
    public boolean getRecursive ()
    {
        return this.recursive;
    }
    /**
     * Apply a filter to files found in the scan directory.
     * Only files matching the filter will be reported as added/changed/removed.
     * @param filter
     */
    public void setFilenameFilter (FilenameFilter filter)
    {
        this.filter = filter;
    }

    /**
     * Get any filter applied to files in the scan dir.
     * @return
     */
    public FilenameFilter getFilenameFilter ()
    {
        return filter;
    }

    /**
     * Whether or not an initial scan will report all files as being
     * added.
     * @param reportExisting if true, all files found on initial scan will be 
     * reported as being added, otherwise not
     */
    public void setReportExistingFilesOnStartup (boolean reportExisting)
    {
        this.reportExisting = reportExisting;
    }

    /**
     * Add an added/removed/changed listener
     * @param listener
     */
    public synchronized void addListener (Listener listener)
    {
        if (listener == null)
            return;

        this.listeners.add(listener);   
    }



    /**
     * Remove a registered listener
     * @param listener the Listener to be removed
     */
    public synchronized void removeListener (Listener listener)
    {
        if (listener == null)
            return;
        
        this.listeners.remove(listener);    
    }


    /**
     * Start the scanning action.
     */
    public synchronized void start ()
    {
        if (running)
            return;

        running = true;

        if (reportExisting)
        {
            // if files exist at startup, report them
            scan();
        }
        else
        {
            //just register the list of existing files and only report changes
        	prevScan = scanFiles();
        }

        timer = newTimer();
        task = newTimerTask();

        schedule(timer, task);
        
    }

    public TimerTask newTimerTask ()
    {
        return new TimerTask()
        {
            public void run() { scan(); }
        };
    }

    public Timer newTimer ()
    {
        return new Timer(true);
    }
    
    public void schedule (Timer timer, TimerTask task)
    {
        if (timer==null) {
            throw new IllegalArgumentException("Timer is null");
        }
        
        if (task==null) {
            throw new IllegalArgumentException("TimerTask is null");
        }
        
        if (getScanInterval() > 0) {
            timer.schedule(task, getScanInterval(), getScanInterval());
        }
    }
    /**
     * Stop the scanning.
     */
    public synchronized void stop ()
    {
        if (running)
        {
            running = false; 
            timer.cancel();
            task.cancel();
            task=null;
            timer=null;
        }
    }

    /**
     * Perform a pass of the scanner and report changes
     */
    public void scan ()
    {
        Map currentScan = scanFiles();
        reportDifferences(currentScan, prevScan);
        this.prevScan = currentScan;
    }

    /**
     * Recursively scan all files in the designated directories.
     * @return Map of name of file to last modified time
     */
    public Map<String, Long> scanFiles ()
    {
        if (scanDirs==null)
            return Collections.emptyMap();
        
        HashMap<String, Long> scanInfo = new HashMap<String, Long>();
        
        for(File scanDir : scanDirs) {
            if ((scanDir != null) && (scanDir.exists()))
                scanFile(scanDir, scanInfo);
        }
        
        return scanInfo;
    }


    /**
     * Report the adds/changes/removes to the registered listeners
     * 
     * @param currentScan the info from the most recent pass
     * @param oldScan info from the previous pass
     */
    public void reportDifferences (Map currentScan, Map oldScan) 
    {
        List bulkChanges = new ArrayList();
        
        Set oldScanKeys = new HashSet(oldScan.keySet());
        Iterator itor = currentScan.entrySet().iterator();
        while (itor.hasNext())
        {
            Map.Entry entry = (Map.Entry)itor.next();
            if (!oldScanKeys.contains(entry.getKey()))
            {
                debug("File added: "+entry.getKey());
                reportAddition ((String)entry.getKey());
                bulkChanges.add(entry.getKey());
            }
            else if (!oldScan.get(entry.getKey()).equals(entry.getValue()))
            {
                debug("File changed: "+entry.getKey());
                reportChange((String)entry.getKey());
                oldScanKeys.remove(entry.getKey());
                bulkChanges.add(entry.getKey());
            }
            else
                oldScanKeys.remove(entry.getKey());
        }

        if (!oldScanKeys.isEmpty())
        {

            Iterator keyItor = oldScanKeys.iterator();
            while (keyItor.hasNext())
            {
                String filename = (String)keyItor.next();
                debug("File removed: "+filename);
                reportRemoval(filename);
                bulkChanges.add(filename);
            }
        }
        
        if (!bulkChanges.isEmpty())
            reportBulkChanges(bulkChanges);
    }


    /**
     * Get last modified time on a single file or recurse if
     * the file is a directory. 
     * @param f file or directory
     * @param scanInfoMap map of filenames to last modified times
     */
    private void scanFile (File f, Map<String, Long> scanInfoMap)
    {
        try
        {
            if (!f.exists())
                return;

            if (f.isFile())
            {
                if ((filter == null) || ((filter != null) && filter.accept(f.getParentFile(), f.getName())))
                {
                    String name = f.getCanonicalPath();
                    long lastModified = f.lastModified();
                    scanInfoMap.put(name, new Long(lastModified));
                }
            }
            else if (f.isDirectory() && (this.recursive || containsFile(scanDirs, f)))
            {
                File[] files = f.listFiles();
                for (int i=0;i<files.length;i++)
                    scanFile(files[i], scanInfoMap);
            }
        }
        catch (IOException e)
        {
            warn("Error scanning watched files", e);
        }
    }
    
    private boolean containsFile(File[] files, File f) {
    	for(File file : files) {
    		if(file.equals(f)) 
    			return true;
    	}
    	
    	return false;
    }

    private void warn(String message, Throwable t)
    {
        System.out.println("[ModificationScanner] " + message);
    	t.printStackTrace();
    }
    
    private void debug(String message)
    {
        //System.out.println("[ModificationScanner] " + message);
    }
    

    /**
     * Report a file addition to the registered FileAddedListeners
     * @param filename
     */
    private void reportAddition (String filename)
    {
        Iterator<Listener> itor = this.listeners.iterator();
        while (itor.hasNext())
        {
            Object l = itor.next();
            try
            {
                if (l instanceof DiscreteListener)
                    ((DiscreteListener)l).fileAdded(filename);
            }
            catch (Exception e)
            {
                warn(l +" failed on '"+filename, e);
            }
            catch (Error e)
            {
                warn(l +" failed on '"+filename, e);
            }
        }
    }


    /**
     * Report a file removal to the FileRemovedListeners
     * @param filename
     */
    private void reportRemoval (String filename)
    {
    	Iterator<Listener> itor = this.listeners.iterator();
        while (itor.hasNext())
        {
            Object l = itor.next();
            try
            {
                if (l instanceof DiscreteListener)
                    ((DiscreteListener)l).fileRemoved(filename);
            }
            catch (Exception e)
            {
                warn(l + " failed on '" + filename, e);
            }
            catch (Error e)
            {
                warn(l + " failed on '" + filename, e);
            }
        }
    }


    /**
     * Report a file change to the FileChangedListeners
     * @param filename
     */
    private void reportChange (String filename)
    {
    	Iterator<Listener> itor = this.listeners.iterator();
        while (itor.hasNext())
        {
            Object l = itor.next();
            try
            {
                if (l instanceof DiscreteListener)
                    ((DiscreteListener)l).fileChanged(filename);
            }
            catch (Exception e)
            {
                warn(l + " failed on '" + filename, e);
            }
            catch (Error e)
            {
                warn(l + " failed on '" + filename, e);
            }
        }
    }
    
    private void reportBulkChanges (List filenames)
    {
    	Iterator<Listener> itor = this.listeners.iterator();
        while (itor.hasNext())
        {
            Object l = itor.next();
            try
            {
                if (l instanceof BulkListener)
                    ((BulkListener)l).filesChanged(filenames);
            }
            catch (Exception e)
            {
            	warn(l +" failed on '"+filenames.toString(), e);
            }
            catch (Error e)
            {
                warn(l +" failed on '"+filenames.toString(),e);
            }
        }
    }
    
    

}
