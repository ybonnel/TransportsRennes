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
package fr.ybo.transportsrennes.activity.alerts;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import android.database.Cursor;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ViewById;

import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseSimpleActivity;
import fr.ybo.transportscommun.util.IconeLigne;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;

/**
 * Activitée permettant d'afficher les détails d'une station.
 *
 * @author ybonnel
 */
@EActivity(R.layout.detailalert)
public class DetailAlert extends BaseSimpleActivity {

	@ViewById
	TextView titreAlert;

	@ViewById
	ImageView iconeLigne;

	@ViewById(R.id.detailAlert_Detail)
	TextView detail;

	@Extra("alert")
	Alert alert;

	@AfterViews
	void afterViews() {
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
		titreAlert.setText(alert.getTitleFormate());
		if (!alert.lines.isEmpty()) {
			iconeLigne.setImageResource(IconeLigne.getIconeResource(alert.lines.iterator().next()));
		}
		detail.setText(Html.fromHtml(alert.getDetailFormatte(getArretToBold())));
	}

	private Collection<String> getArretToBold() {
		Collection<String> arretsToBold = new HashSet<String>(20);
		for (String line : alert.lines) {
			StringBuilder requete = new StringBuilder();
			requete.append("select Arret.nom from Arret, Ligne, ArretRoute ");
			requete.append("where Ligne.nomCourt = :nomCourt and ArretRoute.ligneId = Ligne.id ");
			requete.append("and Arret.id = ArretRoute.arretId");
			Cursor cursor =
					TransportsRennesApplication.getDataBaseHelper().executeSelectQuery(requete.toString(),
							Collections.singletonList(line));
			while (cursor.moveToNext()) {
				arretsToBold.add(cursor.getString(0));
			}
			cursor.close();
		}
		return arretsToBold;
	}
}
