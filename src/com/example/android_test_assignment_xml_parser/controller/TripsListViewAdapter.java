package com.example.android_test_assignment_xml_parser.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.android_test_assignment_xml_parser.R;
import com.example.android_test_assignment_xml_parser.model.mapping.pojo.Trip;
import com.example.android_test_assignment_xml_parser.model.mapping.pojo.Trips;

public class TripsListViewAdapter extends BaseAdapter{

	private final Trips trips;
	private final LayoutInflater inflater;

	private TextView fromCity;
	private TextView toCity;
	private TextView fromDate;
	private TextView toDate;
	private TextView fromTime;
	private TextView toTime;
	private TextView flightDuration;
	private TextView flightCarrier;
	private TextView flightNumber;
	private TextView flightEq;
	private TextView flightPrice;


	public TripsListViewAdapter(Context context, Object trips) {
		this.trips = (Trips) trips;
		inflater = LayoutInflater.from(context);
	}


	@Override
	public int getCount() {
		return trips.trip.size();

	}

	@Override
	public Object getItem(int position) {
		return trips.trip.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = inflater.inflate(R.layout.trips_list_item, null);
			view.setTag(view);
		} else {
			view = (View) view.getTag();
		}

		initializeViews(view);
		setViewsFromItemListData(position);

		return view;
	}

	private void initializeViews(View view) {
		fromCity = (TextView) view.findViewById(R.id.flight_from_city);
		toCity = (TextView) view.findViewById(R.id.flight_to_city);
		fromDate = (TextView) view.findViewById(R.id.flight_from_date);
		toDate = (TextView) view.findViewById(R.id.flight_to_date);
		fromTime = (TextView) view.findViewById(R.id.flight_from_time);
		toTime = (TextView) view.findViewById(R.id.flight_to_time);
		flightDuration = (TextView) view.findViewById(R.id.flight_duration);
		flightCarrier = (TextView) view.findViewById(R.id.flight_carrier);
		flightNumber = (TextView) view.findViewById(R.id.flight_number);
		flightEq = (TextView) view.findViewById(R.id.flight_eq);
		flightPrice = (TextView) view.findViewById(R.id.flight_price);
	}

	private void setViewsFromItemListData(int position) {
		Trip trip = trips.trip.get(position);
		fromCity.setText(trip.takeoff.city);
		toCity.setText(trip.landing.city);
		fromDate.setText(trip.takeoff.date);
		toDate.setText(trip.landing.date);
		fromTime.setText(trip.takeoff.time);
		toTime.setText(trip.landing.time);
		flightDuration.setText("(" + trip.duration + ")");
		flightCarrier.setText(trip.flight.carrier);
		flightNumber.setText(trip.flight.number);
		flightEq.setText(trip.flight.eq);
		flightPrice.setText(trip.price.elementValue);
	}
}
