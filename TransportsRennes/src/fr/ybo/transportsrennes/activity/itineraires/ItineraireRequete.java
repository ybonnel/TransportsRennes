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

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import fr.ybo.opentripplanner.client.OpenTripPlannerException;
import fr.ybo.opentripplanner.client.modele.Message;
import fr.ybo.opentripplanner.client.modele.Request;
import fr.ybo.opentripplanner.client.modele.Response;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseSimpleActivity;
import fr.ybo.transportscommun.util.LocationUtil;
import fr.ybo.transportscommun.util.LocationUtil.UpdateLocationListenner;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportsrennes.R;
import fr.ybo.transportsrennes.application.TransportsRennesApplication;
import fr.ybo.transportsrennes.itineraires.ItineraireReponse;
import fr.ybo.transportsrennes.util.AdresseAdapter;
import fr.ybo.transportsrennes.util.CalculItineraires;
import fr.ybo.transportsrennes.util.TransportsRennesException;

@EActivity(R.layout.itinerairerequete)
public class ItineraireRequete extends BaseSimpleActivity implements UpdateLocationListenner {

	private static final LogYbo LOG_YBO = new LogYbo(ItineraireRequete.class);

	private LocationUtil locationUtil;

	/**
	 * Active le GPS.
	 */

	@Override
	protected void onResume() {
		super.onResume();
		locationUtil.activeGps();
	}

	@Override
	protected void onPause() {
		locationUtil.desactiveGps();
		super.onPause();
	}

	private Calendar calendar;

	@ViewById
	TextView dateItineraire;
	@ViewById
	TextView heureItineraire;
	@ViewById
	AutoCompleteTextView adresseDepart;
	@ViewById
	AutoCompleteTextView adresseArrivee;

	@Click
	void dateItineraireClicked() {
		showDialog(DATE_DIALOG_ID);
	}

	@Click
	void heureItineraireClicked() {
		showDialog(TIME_DIALOG_ID);
	}

	@Click
	void itineraireTermineClicked() {
		terminer();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		locationUtil = new LocationUtil(this, this);
		calendar = Calendar.getInstance();
		adresseDepart.setAdapter(new AdresseAdapter(this));
		adresseDepart.setTextColor(TransportsRennesApplication.getTextColor(this));
		adresseArrivee.setAdapter(new AdresseAdapter(this));
		adresseArrivee.setTextColor(TransportsRennesApplication.getTextColor(this));
		adresseArrivee.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					terminer();
					return true;
				}
				return false;
			}
		});
		majTextViews();
		if (!locationUtil.activeGps()) {
			Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
		}
	}

	private void terminer() {
		String adresseDepart = null;
		Editable textDepart = this.adresseDepart.getText();
		if (textDepart.length() > 0) {
			adresseDepart = textDepart.toString();
		}
		Editable textArrivee = this.adresseArrivee.getText();
		String adresseArrivee = null;
		if (textArrivee.length() > 0) {
			adresseArrivee = textArrivee.toString();
		}
		if ((adresseDepart == null || adresseArrivee == null)
				&& (locationUtil.getCurrentLocation() == null || locationUtil.getCurrentLocation().getAccuracy() > 50)) {
			Toast.makeText(this, R.string.erreur_gpsPasPret, Toast.LENGTH_LONG).show();
		} else {
			geoCoderAdresse(adresseDepart, adresseArrivee);
		}
	}

	@UiThread
	void dismissProgressDialog(ProgressDialog progressDialog) {
		progressDialog.dismiss();
	}

	@UiThread
	void showToast(int erreur) {
		Toast.makeText(this, erreur, Toast.LENGTH_LONG).show();
	}

	private void geoCoderAdresse(final String adresseDepart, final String adresseArrivee) {
		ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.geocodageAdresseDepart), true);
		backgroundGeoCoderAdresse(progressDialog, adresseDepart, adresseArrivee);
	}

	@UiThread
	void changeMessage(ProgressDialog progressDialog, int message) {
		progressDialog.setMessage(getString(message));
	}

	@Background
	void backgroundGeoCoderAdresse(ProgressDialog progressDialog, String adresseDepart, String adresseArrivee) {
		GeocodeResponse reponseDepart = null;
		GeocodeResponse reponseArrivee = null;
		if (adresseDepart != null) {
			GeocoderRequest geocoderRequest =
					new GeocoderRequestBuilder().setAddress(adresseDepart).setLanguage("fr")
							.setBounds(TransportsRennesApplication.getBounds()).getGeocoderRequest();
			reponseDepart = TransportsRennesApplication.getGeocodeUtil().geocode(geocoderRequest);
			if (reponseDepart != null && reponseDepart.getStatus() == GeocoderStatus.OVER_QUERY_LIMIT) {

			} else if (reponseDepart == null || reponseDepart.getStatus() != GeocoderStatus.OK) {
				showToast(R.string.erreur_geocodage_quota);
				return;
			}
		}
		if (adresseArrivee != null) {
			changeMessage(progressDialog, R.string.geocodageAdresseArrivee);
			GeocoderRequest geocoderRequest =
					new GeocoderRequestBuilder().setAddress(adresseArrivee).setLanguage("fr")
							.setBounds(TransportsRennesApplication.getBounds()).getGeocoderRequest();
			reponseArrivee = TransportsRennesApplication.getGeocodeUtil().geocode(geocoderRequest);
			if (reponseArrivee != null && reponseArrivee.getStatus() == GeocoderStatus.OVER_QUERY_LIMIT) {
				showToast(R.string.erreur_geocodage_quota);
			} else if (reponseArrivee == null || reponseArrivee.getStatus() != GeocoderStatus.OK) {
				showToast(R.string.erreur_geocodage);
			}
		}
		traiterReponseGeoCodage(reponseDepart, reponseArrivee);
	}

	@UiThread
	void traiterReponseGeoCodage(GeocodeResponse reponseDepart, GeocodeResponse reponseArrivee) {
		StringBuilder stringBuilder = new StringBuilder();
		boolean erreur = false;
		if (reponseDepart != null && reponseDepart.getResults().isEmpty()) {
			stringBuilder.append(getString(R.string.erreur_pasAdresseDepart));
			stringBuilder.append('\n');
			erreur = true;
		}
		if (reponseArrivee != null && reponseArrivee.getResults().isEmpty()) {
			stringBuilder.append(getString(R.string.erreur_pasAdresseArrivee));
			erreur = true;
		}
		if (erreur) {
			Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
		} else {
			if (reponseDepart != null && reponseDepart.getResults().size() > 1 || reponseArrivee != null
					&& reponseArrivee.getResults().size() > 1) {
				traiterAdresseMultiple(reponseDepart, reponseArrivee);
			} else {
				calculItineraire(reponseDepart == null ? null : reponseDepart.getResults().get(0),
						reponseArrivee == null ? null : reponseArrivee.getResults().get(0));
			}
		}
	}

	private void traiterAdresseMultiple(GeocodeResponse reponseDepart, GeocodeResponse reponseArrivee) {
		final GeocodeResponse reponseDepartTmp = reponseDepart;
		final GeocodeResponse reponseArriveeTmp = reponseArrivee;
		if (reponseDepart != null && reponseDepart.getResults().size() > 1) {
			// Choix de l'adresse de départ
			List<String> adresses = new ArrayList<String>(reponseDepartTmp.getResults().size());
			for (GeocoderResult result : reponseDepartTmp.getResults()) {
				adresses.add(result.getFormattedAddress());
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.textAdresseArrivee);
			builder.setItems(adresses.toArray(new String[adresses.size()]), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					if (reponseArriveeTmp != null && reponseArriveeTmp.getResults().size() > 1) {
						GeocoderResult result = reponseDepartTmp.getResults().get(item);
						reponseDepartTmp.getResults().clear();
						reponseDepartTmp.getResults().add(result);
						traiterAdresseMultiple(reponseDepartTmp, reponseArriveeTmp);
					} else {
						calculItineraire(reponseDepartTmp.getResults().get(item), reponseArriveeTmp == null ? null
								: reponseArriveeTmp.getResults().get(0));
					}
				}
			});
			builder.create().show();
		} else {
			// Choix de l'adresse de destination
			List<String> adresses = new ArrayList<String>(reponseArriveeTmp.getResults().size());
			for (GeocoderResult result : reponseArriveeTmp.getResults()) {
				adresses.add(result.getFormattedAddress());
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.textAdresseArrivee);
			builder.setItems(adresses.toArray(new String[adresses.size()]), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					calculItineraire(reponseDepartTmp == null ? null : reponseDepartTmp.getResults().get(0),
							reponseArriveeTmp.getResults().get(item));
				}
			});
			builder.create().show();
		}
	}

	private void calculItineraire(final GeocoderResult resultDepart, final GeocoderResult resultArrivee) {
		double latitudeDepart;
		double longitudeDepart;
		double latitudeArrivee;
		double longitudeArrivee;
		if (resultDepart != null) {
			latitudeDepart = resultDepart.getGeometry().getLocation().getLat().doubleValue();
			longitudeDepart = resultDepart.getGeometry().getLocation().getLng().doubleValue();
		} else {
			latitudeDepart = locationUtil.getCurrentLocation().getLatitude();
			longitudeDepart = locationUtil.getCurrentLocation().getLongitude();
		}
		if (resultArrivee != null) {
			latitudeArrivee = resultArrivee.getGeometry().getLocation().getLat().doubleValue();
			longitudeArrivee = resultArrivee.getGeometry().getLocation().getLng().doubleValue();
		} else {
			latitudeArrivee = locationUtil.getCurrentLocation().getLatitude();
			longitudeArrivee = locationUtil.getCurrentLocation().getLongitude();
		}
		Request request =
				new Request(latitudeDepart, longitudeDepart, latitudeArrivee, longitudeArrivee, calendar.getTime());
		request.setMaxWalkDistance(1500.0);
		ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.calculItineraire), true);
		calculItineraireBackground(progressDialog, request);

	}

	@Background
	void calculItineraireBackground(ProgressDialog progressDialog, Request request) {
		try {
			traiteReponseCalculItineraire(progressDialog, CalculItineraires.getInstance().getItineraries(request));
		} catch (OpenTripPlannerException e) {
			if (e.getCause() != null
					&& (e.getCause() instanceof SocketException || e.getCause() instanceof FileNotFoundException
							|| e.getCause() instanceof UnknownHostException || e.getCause() instanceof JsonIOException
							|| e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof JsonParseException)) {
				erreurCalculItineraire(progressDialog);
			} else {
				throw new TransportsRennesException(e);
			}
		}
	}

	@UiThread
	void erreurCalculItineraire(ProgressDialog progressDialog) {
		progressDialog.dismiss();
		Toast.makeText(this, getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
	}

	@UiThread
	void traiteReponseCalculItineraire(ProgressDialog progressDialog, Response response) {
		progressDialog.dismiss();
		if (response.getError() != null) {
			LOG_YBO.erreur(response.getError().getMsg());
			int message = R.string.erreur_calculItineraires;
			switch (Message.findEnumById(response.getError().getId())) {
				case OUTSIDE_BOUNDS:
					message = R.string.erreur_outOfBounds;
					break;
				case NO_TRANSIT_TIMES:
					message = R.string.erreur_noTransitTimes;
					break;
				case PATH_NOT_FOUND:
					message = R.string.erreur_pathNotFound;
					break;
			}
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		} else {
			int heureDepart = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
			startActivity(Itineraires_.intent(this)//
					.itineraireReponse(ItineraireReponse.convert(response.getPlan()))//
					.heureDepart(heureDepart).get());
		}
	}

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private void majTextViews() {
		dateItineraire.setText(DATE_FORMAT.format(calendar.getTime()));
		heureItineraire.setText(TIME_FORMAT.format(calendar.getTime()));
	}

	private static final int DATE_DIALOG_ID = 0;
	private static final int TIME_DIALOG_ID = 1;

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			majTextViews();
		}
	};

	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			majTextViews();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DATE_DIALOG_ID) {
			return new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		} else if (id == TIME_DIALOG_ID) {
			return new TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), true);
		}
		return null;
	}

	public void updateLocation(Location location) {
	}

}
