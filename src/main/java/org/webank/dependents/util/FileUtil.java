package org.webank.dependents.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class FileUtil {

	public static void saveData(String data, String outPutPath) {

		OutputStreamWriter ow = null;
		try {
			File file = new File(outPutPath);
			ow = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			ow.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ow != null) {
				try {
					ow.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
