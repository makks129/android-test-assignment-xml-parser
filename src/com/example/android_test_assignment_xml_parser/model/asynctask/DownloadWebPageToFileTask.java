package com.example.android_test_assignment_xml_parser.model.asynctask;

import android.os.AsyncTask;

import java.io.*;
import java.net.URL;

public class DownloadWebPageToFileTask extends AsyncTask<String, Void, Void> {

	@Override
	protected Void doInBackground(String... params) {
		String webPageURL = params[0];
		String filePath = params[1];
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		final int bufferSize = 1024;
		try {
			try {
				in = new BufferedInputStream(new URL(webPageURL).openStream());
				out = new BufferedOutputStream(new FileOutputStream(filePath));
				byte[] buffer = new byte[bufferSize];
				int count;
				while ((count = in.read(buffer, 0, bufferSize)) != -1) {
					out.write(buffer, 0, count);
				}
				out.flush();
			} finally {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

