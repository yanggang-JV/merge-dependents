package org.webank.dependents.main;

import java.util.List;
import org.webank.dependents.service.AppService;

public class AppMainBat {
    
    public static final String SOURCE_DIR = "dependents/";
    
    public static final String OUTPUT_FIX = "./output/license-";
    
    public static final String DEFAULT_FILE_NAME = "dependents.txt";
    
    public static void main(String[] args) throws Exception {

        List<String> dataList = AppService.parserDependents(SOURCE_DIR + DEFAULT_FILE_NAME);
        String data = AppService.meger(dataList);
        String result = AppService.buildResultWithLicense(data);
        AppService.outPutResult(result, OUTPUT_FIX + DEFAULT_FILE_NAME);
    }
}
