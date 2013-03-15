package com.example.android_test_assignment_xml_parser.model.asynctask;

import android.os.AsyncTask;
import com.example.android_test_assignment_xml_parser.model.mapping.SimpleXMLToPOJOMapper;

import java.io.File;

public class MapXMLToPOJOTask extends AsyncTask<Object, Void, Object>{

	public interface Delegate {
		public void mapXMLToPOJOTaskFinishedWithResult(MapXMLToPOJOTask sender, Object result);
	}

	private MapXMLToPOJOTask.Delegate delegate;

	public MapXMLToPOJOTask.Delegate getDelegate() {
		return delegate;
	}

	public void setDelegate(MapXMLToPOJOTask.Delegate delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Object doInBackground(Object... params) {
		File xml = (File) params[0];
		Object pojo = params[1];
		try {
			SimpleXMLToPOJOMapper.mapXMLToPOJO(xml, pojo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 	return pojo;
	}

	@Override
	protected void onPostExecute(Object result) {
		if (delegate != null) {
			delegate.mapXMLToPOJOTaskFinishedWithResult(this, result);
		}
	}
}
