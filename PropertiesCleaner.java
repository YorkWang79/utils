package com.rimage.util;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesCleaner {
    private static final String PROPERTIE_FILE = "D:\\Workspaces\\MDS_Web_2017_jfinal\\WebRoot\\WEB-INF\\classes\\lang_zh_TW.properties";
    private static final String SRC_FOLDER = "D:\\Workspaces\\MDS_Web_2017_jfinal\\WebRoot";
    
    private ArrayList<String> propertiesKeys = new ArrayList<String>();
    private ArrayList<File> sourceFiles = new ArrayList<File>();
    private ArrayList<String> deleteKeys = new ArrayList<String>();
    
    private static boolean isShowUsing = false;
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new PropertiesCleaner().run();
    }
    
    public void run() {
        try {
            loadAllProperties(PROPERTIE_FILE);

            getAllFiles(new File(SRC_FOLDER));
            System.out.println("Get source files count: " + sourceFiles.size());
            
            searchFilesForProperties();
            
            deleteProperties();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadAllProperties(String filePath) throws IOException {
        Properties pps = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        pps.load(in);
        Enumeration en = pps.propertyNames(); 
        
        while(en.hasMoreElements()) {
            String strKey = (String) en.nextElement();
            propertiesKeys.add(strKey);
            //System.out.println(strKey);
            //String strValue = pps.getProperty(strKey);
            //System.out.println(strKey + "=" + strValue);
        }
        System.out.println("Get properties key count: " + propertiesKeys.size());
        
    }
    
    private void getAllFiles(File file){
        File[] fs = file.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory())
                    return true;
                String name = pathname.getName();
                return name.endsWith(".java") || name.endsWith(".html") || name.endsWith(".js");
            }
        });
        for(File f:fs){
            if(f.isDirectory()) 
                getAllFiles(f);
            if(f.isFile()) {
                sourceFiles.add(f);
                //System.out.println(f);
            }
        }
    }
    
    private void searchFilesForProperties() {
        int usedTimes = 0;
        int notUsedTimes = 0;
        for (String keyStr : propertiesKeys) {
            boolean isUsing = false;
            for (File file : sourceFiles) {
                isUsing = isUsing || searchFileForKey(file, keyStr);
            }
            if(!isUsing) {
                //System.out.println("Properties " + keyStr + " is not used anymore.");
                notUsedTimes ++;
                deleteKeys.add(keyStr);
            } else {
                usedTimes ++;
            }
        }
        if (isShowUsing) {
            System.out.println(usedTimes + " properties are using.");
        }
        System.out.println(notUsedTimes + " properties not used anymore.");
    }

    private boolean searchFileForKey(File file, String keyword){
        LineNumberReader reader = null;
        int times = 0;
        try {
            reader = new LineNumberReader(new FileReader(file));
            String readLine = null;
            ArrayList<String> lines = new ArrayList<String>();
            while((readLine = reader.readLine()) != null) {
//                int index = 0;
//                int next = 0;
                /*
                while((index = readLine.indexOf(keyword,next)) != -1) {
                    next = index + keyword.length();
                    times++;
                }
                if(times > 0) {
                    System.out.println("No." + reader.getLineNumber() + " line: has " + times + " times");
                }
                */
                if(readLine.indexOf(keyword) != -1) {
                    times++;
                    lines.add(reader.getLineNumber() + ": " + readLine);
                }
            }
            if(times > 0) {
                if (isShowUsing) {
                    System.out.println("Found " + keyword + " " + times + " in file " + file.getName());
                    for (String str : lines) {
                        System.out.println("    " + str);
                    }
                    System.out.println();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return times > 0;
    }
    
    private void deleteProperties() throws IOException, FileNotFoundException {
        if(deleteKeys.size() == 0)
            return;
        
        LineNumberReader reader = null;
        BufferedWriter bw = null;
        int times = 0;
        try {
            reader = new LineNumberReader(new FileReader(PROPERTIE_FILE));
            String readLine = null;
            ArrayList<String> lines = new ArrayList<String>();
            bw = new BufferedWriter(new FileWriter(PROPERTIE_FILE+"_1"));
            while((readLine = reader.readLine()) != null) {
                boolean keyInLine = false;
                for (String keyword : deleteKeys) {
                    keyInLine = keyInLine || readLine.indexOf(keyword) != -1;
                }
                if(!keyInLine) {
                    lines.add(readLine);
                    bw.write(readLine);
                    bw.newLine();
                }
            }
            bw.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
