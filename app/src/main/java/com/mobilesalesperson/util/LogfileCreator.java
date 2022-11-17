package com.mobilesalesperson.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class LogfileCreator {

	public static void appendLog(String errorMsg) {
		
		File root_path = Environment.getExternalStorageDirectory();
		File folder_path = new File(root_path.getAbsoluteFile() + "/"+ "Android/AMSP");
		File logFile = new File(folder_path, "AMSPLog.txt");
		
		SimpleDateFormat sdf;
		String currentDateTime;

		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			currentDateTime = sdf.format(new Date());

			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));

			buf.append(currentDateTime + " - " + errorMsg);
			System.out.println("Text Write to file:" + errorMsg);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
