/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.ybo.transportsrennes.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.ybo.transportscommun.util.Theme;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.activity.alerts.TabAlertes;
import fr.ybo.transportsrennes.activity.bus.BusRennes;
import fr.ybo.transportsrennes.activity.bus.ListArretByPosition;
import fr.ybo.transportsrennes.activity.itineraires.ItineraireRequete;
import fr.ybo.transportsrennes.activity.parkrelais.ListParkRelais;
import fr.ybo.transportsrennes.activity.velos.ListStationsByPosition;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;

public class DashboardFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root;
		if (TransportsRennesApplication.getTheme(getActivity()) == Theme.NOIR) {
			root = inflater.inflate(R.layout.fragment_dashboard_noir, container);
		} else {
			root = inflater.inflate(R.layout.fragment_dashboard, container);
		}
		root.findViewById(R.id.home_btn_bus).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), BusRennes.class));
			}
		});
		root.findViewById(R.id.home_btn_bus_gps).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), ListArretByPosition.class));
			}
		});
		root.findViewById(R.id.home_btn_alert).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), TabAlertes.class));
			}
		});
		root.findViewById(R.id.home_btn_velo).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), ListStationsByPosition.class));
			}
		});
		root.findViewById(R.id.home_btn_parking).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), ListParkRelais.class));
			}
		});
		root.findViewById(R.id.home_btn_itineraires).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), ItineraireRequete.class));
			}
		});
		return root;
	}
}
