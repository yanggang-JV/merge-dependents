package org.webank.dependents.main;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import org.webank.dependents.service.AppService;

public class AppMain1 {
	
	private static final String SOURCE_DIR = "./dependents/";
	
	private static final String OUTPUT_FIX = "./output/license-";
	
	private static final String DEFAULT_FILE_NAME = "dependents.txt";
	
	public static void main(String[] args) throws Exception {
	    File file = new File(SOURCE_DIR + DEFAULT_FILE_NAME);
	    if (file.exists()) {
	        file.delete();
	    }
	    
	    Process process = Runtime.getRuntime().exec("cmd /C start gen.bat ");
	    while (process.isAlive()) {
	        Thread.sleep(1000);
	    }
	    
	    FileInputStream fis = new FileInputStream(file);
	    int v = 0;
	    int count = 0;
	    while (true) {
	        Thread.sleep(100);
	        int value = fis.available();
	        if (value - v == 0) {
	            count++;
	            if (count == 10) {
	                break;
	            }
	        }
	        v = value;
	    }
	    fis.close();
	    
        List<String> dataList = AppService.parserDependents(SOURCE_DIR + DEFAULT_FILE_NAME);
        String data = AppService.meger(dataList);
        String result = AppService.buildResultWithLicense(data);
        AppService.outPutResult(result, OUTPUT_FIX + DEFAULT_FILE_NAME);
	}

}
