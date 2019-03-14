package org.webank.dependents.main;

import java.io.File;
import java.util.List;
import org.webank.dependents.service.AppService;

public class AppMain {
	
	private static final String SOURCE_DIR = "./dependents/";
	
	private static final String OUTPUT_FIX = "./output/license-";
	
	public static void main(String[] args) throws Exception {

		File file_dir = new File(SOURCE_DIR);
		File[] files = file_dir.listFiles();
		if(files != null) {
			for (File file : files) {
				String fileName = file.getName();
				List<String> dataList = AppService.parserDependents(SOURCE_DIR + fileName);
				String data = AppService.meger(dataList);
				String result = AppService.buildResultWithLicense(data);
				AppService.outPutResult(result, OUTPUT_FIX + fileName);
				System.out.println(result);
			}
		}
	}

}
