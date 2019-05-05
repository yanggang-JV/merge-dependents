package org.webank.dependents.main;

import java.io.File;

public class AppMain1 {

	public static void main(String[] args) throws Exception {
	    
        File file = new File(AppMainBat.SOURCE_DIR + AppMainBat.DEFAULT_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
	    Runtime.getRuntime().exec("cmd /C start gen.bat ");
	}
}