package com.example.android_test_assignment_xml_parser.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.example.android_test_assignment_xml_parser.R;
import com.example.android_test_assignment_xml_parser.model.asynctask.DownloadWebPageToFileTask;
import com.example.android_test_assignment_xml_parser.model.asynctask.MapXMLToPOJOTask;
import com.example.android_test_assignment_xml_parser.model.mapping.pojo.Trip;
import com.example.android_test_assignment_xml_parser.model.mapping.pojo.Trips;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

public class TripsActivity extends Activity implements MapXMLToPOJOTask.Delegate {

	private ListView tripsList;
	private TripsListViewAdapter adapter;
	private Button sortByPriceBtn;
	private Button sortByTimeBtn;
	private ProgressDialog loadDialog;

	private Trips trips;

	private boolean priceReverseOrder;
	private boolean timeReverseOrder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trips_list);

		initializeFields();
		hideSortButtonsWhileTheyAreNotWorking();
	}

	private void initializeFields() {
		tripsList = (ListView) findViewById(R.id.trips_list);
		sortByPriceBtn = (Button) findViewById(R.id.sort_price_btn);
		sortByTimeBtn = (Button) findViewById(R.id.sort_time_btn);
		loadDialog = new ProgressDialog(this);
	}

	private void hideSortButtonsWhileTheyAreNotWorking() {
		sortByPriceBtn.setVisibility(View.GONE);
		sortByTimeBtn.setVisibility(View.GONE);
	}

	public void onClickGetTripsButton(View view) {
		loadDialog.setMessage(getString(R.string.load_dialog));
		loadDialog.show();

		String webPageURL = getString(R.string.xml_url);
		String filePath = getApplicationContext().getFilesDir().getPath() + getString(R.string.file_path);
		try {
			final DownloadWebPageToFileTask downloadTask = new DownloadWebPageToFileTask();
			downloadTask.execute(webPageURL, filePath);

			final File xml = new File(filePath);
			trips = new Trips();
			final MapXMLToPOJOTask mapTask = new MapXMLToPOJOTask();
			mapTask.setDelegate(this);
			mapTask.execute(xml, trips);

			loadDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					downloadTask.cancel(true);
					mapTask.cancel(true);
					xml.delete();
					hideSortButtonsWhileTheyAreNotWorking();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			dismissDialogShowToastFail();
		}
	}

	private void dismissDialogShowToastFail() {
		loadDialog.cancel();
		Toast.makeText(this, getString(R.string.load_fail), Toast.LENGTH_LONG).show();
	}

	@Override
	public void mapXMLToPOJOTaskFinishedWithResult(MapXMLToPOJOTask sender, Object result) {
		try {
			adapter = new TripsListViewAdapter(this, result);
			tripsList.setAdapter(adapter);
			sortByPriceBtn.setVisibility(View.VISIBLE);
			sortByTimeBtn.setVisibility(View.VISIBLE);
			loadDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
			dismissDialogShowToastFail();
		}
	}

	public void onClickSortByPriceButton(View view) {
		if (requiredFieldsAreNull()) {
			return;
		}
		if (!priceReverseOrder){
			Collections.sort(trips.trip, getComparatorTripByPrice());
		} else {
			Collections.sort(trips.trip, Collections.reverseOrder(getComparatorTripByPrice()));
		}
		priceReverseOrder = !priceReverseOrder;
		adapter.notifyDataSetChanged();
	}

	private boolean requiredFieldsAreNull() {
		return trips == null || trips.trip == null || adapter == null;
	}

	private Comparator<Trip> getComparatorTripByPrice() {
		return new Comparator<Trip>() {
			@Override
			public int compare(Trip lhs, Trip rhs) {
				double lPrice = Double.parseDouble(lhs.price.elementValue);
				double rPrice = Double.parseDouble(rhs.price.elementValue);
				return (lPrice < rPrice ? -1 : (lPrice == rPrice ? 0 : 1));
			}
		};
	}

	public void onClickSortByTimeButton(View view) {
		if (requiredFieldsAreNull()) {
			return;
		}
		if (!timeReverseOrder){
			Collections.sort(trips.trip, getComparatorTripByTime());
		} else {
			Collections.sort(trips.trip, Collections.reverseOrder(getComparatorTripByTime()));
		}
		timeReverseOrder = !timeReverseOrder;
		adapter.notifyDataSetChanged();
	}

	private Comparator<Trip> getComparatorTripByTime() {
		return new Comparator<Trip>() {
			@Override
			public int compare(Trip lhs, Trip rhs) {
				String lTimeString = lhs.duration.replaceAll("[^0-9]", "");
				String rTimeString = rhs.duration.replaceAll("[^0-9]", "");
				int lTime = Integer.parseInt(lTimeString);
				int rTime = Integer.parseInt(rTimeString);
				return (lTime < rTime ? -1 : (lTime == rTime ? 0 : 1));
			}
		};
	}
}
