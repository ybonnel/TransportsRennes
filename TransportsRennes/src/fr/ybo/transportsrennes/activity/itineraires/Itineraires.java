/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ybo.transportsrennes.activity.itineraires;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ItemClick;

import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseListActivity;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.adapters.itineraires.TrajetAdapter;
import fr.ybo.transportsrennes.itineraires.ItineraireReponse;
import fr.ybo.transportsrennes.itineraires.Trajet;

@EActivity(R.layout.itineraires)
public class Itineraires extends BaseListActivity {

	@Extra("itineraireReponse")
	ItineraireReponse itineraireReponse;
	
	@Extra("heureDepart")
	int heureDepart;

	@AfterViews
	void afterViews() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
        setListAdapter(new TrajetAdapter(this, itineraireReponse.getTrajets(), heureDepart));
		getListView().setTextFilterEnabled(true);
	}

	@ItemClick
	void listItemClicked(Trajet trajet) {
		startActivity(TrajetOnMap_.intent(this).trajet(trajet).get());
    }
}
