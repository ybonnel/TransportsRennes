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
package fr.ybo.transportsbordeaux.activity.itineraires;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.ads.Ad;
import com.google.ads.AdRequest;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.code.geocoder.model.LatLng;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import fr.ybo.opentripplanner.client.OpenTripPlannerException;
import fr.ybo.opentripplanner.client.modele.Message;
import fr.ybo.opentripplanner.client.modele.Request;
import fr.ybo.opentripplanner.client.modele.Response;
import fr.ybo.opentripplanner.client.modele.TraverseMode;
import fr.ybo.opentripplanner.client.modele.TraverseModeSet;
import fr.ybo.transportsbordeaux.R;
import fr.ybo.transportsbordeaux.application.TransportsBordeauxApplication;
import fr.ybo.transportsbordeaux.itineraires.ItineraireReponse;
import fr.ybo.transportsbordeaux.tbcapi.TcbException;
import fr.ybo.transportsbordeaux.util.AdresseAdapter;
import fr.ybo.transportsbordeaux.util.CalculItineraires;
import fr.ybo.transportscommun.AbstractTransportsApplication;
import fr.ybo.transportscommun.activity.commun.BaseActivity.BaseSimpleActivity;
import fr.ybo.transportscommun.donnees.modele.Arret;
import fr.ybo.transportscommun.util.GeocodeUtil;
import fr.ybo.transportscommun.util.LocationUtil;
import fr.ybo.transportscommun.util.LocationUtil.UpdateLocationListenner;
import fr.ybo.transportscommun.util.LogYbo;
import fr.ybo.transportscommun.util.StringOperation;

public class ItineraireRequete extends BaseSimpleActivity implements UpdateLocationListenner {

    private static final LogYbo LOG_YBO = new LogYbo(ItineraireRequete.class);

    private LocationUtil locationUtil;

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

    private TextView dateItineraire;
    private TextView heureItineraire;

    private boolean bus = true;
    private boolean tram = true;

	private final List<Arret> arrets = new ArrayList<Arret>();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itinerairerequete);
		getActivityHelper().setupActionBar(R.menu.default_menu_items, R.menu.holo_default_menu_items);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        locationUtil = new LocationUtil(this, this);
        calendar = Calendar.getInstance();
        dateItineraire = (TextView) findViewById(R.id.dateItineraire);
        heureItineraire = (TextView) findViewById(R.id.heureItineraire);
        final AutoCompleteTextView adresseDepart = (AutoCompleteTextView) findViewById(R.id.adresseDepart);
		final AdresseAdapter adapterDepart = new AdresseAdapter(this, arrets);
        adresseDepart.setAdapter(adapterDepart);
		adresseDepart.setTextColor(AbstractTransportsApplication.getTextColor(this));
        final AutoCompleteTextView adresseArrivee = (AutoCompleteTextView) findViewById(R.id.adresseArrivee);
		final AdresseAdapter adapterArrivee = new AdresseAdapter(this, arrets);
        adresseArrivee.setAdapter(adapterArrivee);
		adresseArrivee.setTextColor(AbstractTransportsApplication.getTextColor(this));
        adresseArrivee.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    terminer();
                    return true;
                }
                return false;
            }
        });
        majTextViews();
        dateItineraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        heureItineraire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        final View boutonTerminer = findViewById(R.id.itineraireTermine);
        boutonTerminer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                terminer();
            }
        });
        if (!locationUtil.activeGps()) {
            Toast.makeText(getApplicationContext(), getString(R.string.activeGps), Toast.LENGTH_SHORT).show();
        }
        final CompoundButton busCheckBox = (CompoundButton) findViewById(R.id.busCheckBox);
        busCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (!tram && !isChecked) {
                    buttonView.setChecked(true);
                } else {
                    bus = isChecked;
                }
            }
        });
        final CompoundButton tramCheckBox = (CompoundButton) findViewById(R.id.tramCheckBox);
        tramCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (!bus && !isChecked) {
                    buttonView.setChecked(true);
                } else {
                    tram = isChecked;
                }
            }
        });

		// Look up the AdView as a resource and load a request.
		((Ad) findViewById(R.id.adView)).loadAd(new AdRequest());

		new AsyncTask<Void, Void, Void>() {

			ProgressDialog myProgressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				myProgressDialog =
						ProgressDialog.show(ItineraireRequete.this, "", getString(R.string.rechercheArrets), true);
			}

			@Override
			protected Void doInBackground(final Void... voids) {
				construireListeArrets();
				return null;
			}

			@Override
			protected void onPostExecute(final Void result) {
				try {
					myProgressDialog.dismiss();
				} catch (final IllegalArgumentException ignore) {
				}
				super.onPostExecute(result);
			}
		}.execute();
    }

	private void construireListeArrets() {
		arrets.clear();

		final Map<String, Arret> mapArrets = new HashMap<String, Arret>();
		for (final Arret arret : TransportsBordeauxApplication.getDataBaseHelper().selectAll(Arret.class)) {
			arret.nom = StringOperation.sansAccents(arret.nom.toUpperCase());
			if (!mapArrets.containsKey(arret.nom)) {
				mapArrets.put(arret.nom, arret);
			}
		}

		arrets.addAll(mapArrets.values());
	}

    private void terminer() {
        String adresseDepart = null;
        final Editable textDepart = ((EditText) findViewById(R.id.adresseDepart)).getText();
        if (textDepart.length() > 0) {
            adresseDepart = textDepart.toString();
        }
        final Editable textArrivee = ((EditText) findViewById(R.id.adresseArrivee)).getText();
        String adresseArrivee = null;
        if (textArrivee.length() > 0) {
            adresseArrivee = textArrivee.toString();
        }
        if ((adresseDepart == null || adresseArrivee == null)
                && (locationUtil.getCurrentBestLocation() == null || locationUtil.getCurrentBestLocation().getAccuracy() > 50)) {
            Toast.makeText(this, R.string.erreur_gpsPasPret, Toast.LENGTH_LONG).show();
        } else {
            geoCoderAdresse(adresseDepart, adresseArrivee);
        }
    }

    private void geoCoderAdresse(final String adresseDepart, final String adresseArrivee) {
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog progressDialog;
            private boolean erreur;
            private GeocodeResponse reponseDepart;
            private GeocodeResponse reponseArrivee;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(ItineraireRequete.this, "",
                        getString(R.string.geocodageAdresseDepart), true);
            }

			private GeocodeResponse arretToGeocodeResponse(final Arret arret) {
				final GeocodeResponse response = new GeocodeResponse();
				response.setStatus(GeocoderStatus.OK);
				response.setResults(new ArrayList<GeocoderResult>());
				final GeocoderResult result = new GeocoderResult();
				result.setGeometry(new GeocoderGeometry());
				result.getGeometry().setLocation(
						new LatLng(BigDecimal.valueOf(arret.getLatitude()), BigDecimal.valueOf(arret.getLongitude())));
				response.getResults().add(result);
				return response;
			}

            @Override
            protected Void doInBackground(final Void... voids) {
                if (adresseDepart != null) {
					// Recherche arrêts
					reponseDepart = null;
					final String adresseDepartUpper = StringOperation.sansAccents(adresseDepart.toUpperCase());
					for (final Arret arret : arrets) {
						if (arret.nom.equals(adresseDepartUpper)) {
							reponseDepart = arretToGeocodeResponse(arret);
							break;
						}
					}
					if (reponseDepart == null) {
						final GeocoderRequest geocoderRequest =
								new GeocoderRequestBuilder().setAddress(adresseDepart).setLanguage("fr")
										.setBounds(TransportsBordeauxApplication.getBounds()).getGeocoderRequest();
						reponseDepart = GeocodeUtil.geocode(geocoderRequest);
						if (reponseDepart == null || reponseDepart.getStatus() != GeocoderStatus.OK) {
							erreur = true;
							return null;
						}
					}
                }
                if (adresseArrivee != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage(getString(R.string.geocodageAdresseArrivee));
                        }
                    });
					reponseArrivee = null;
					final String adresseArriveeUpper = StringOperation.sansAccents(adresseArrivee.toUpperCase());
					for (final Arret arret : arrets) {
						if (arret.nom.equals(adresseArriveeUpper)) {
							reponseArrivee = arretToGeocodeResponse(arret);
							break;
						}
					}
					if (reponseArrivee == null) {
						final GeocoderRequest geocoderRequest =
								new GeocoderRequestBuilder().setAddress(adresseArrivee).setLanguage("fr")
										.setBounds(TransportsBordeauxApplication.getBounds()).getGeocoderRequest();
						reponseArrivee = GeocodeUtil.geocode(geocoderRequest);
						if (reponseArrivee == null || reponseArrivee.getStatus() != GeocoderStatus.OK) {
							erreur = true;
							return null;
						}
					}
                }
                return null;
            }

            @Override
            protected void onPostExecute(final Void result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
                if (erreur) {
                    Toast.makeText(ItineraireRequete.this, R.string.erreur_geocodage, Toast.LENGTH_LONG).show();
                } else {
                    traiterReponseGeoCodage(reponseDepart, reponseArrivee);
                }
            }
        }.execute((Void) null);
    }

	private void traiterReponseGeoCodage(final GeocodeResponse reponseDepart, final GeocodeResponse reponseArrivee) {
        final StringBuilder stringBuilder = new StringBuilder();
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

    private void traiterAdresseMultiple(final GeocodeResponse reponseDepart, final GeocodeResponse reponseArrivee) {
        if (reponseDepart != null && reponseDepart.getResults().size() > 1) {
            // Choix de l'adresse de départ
            final List<String> adresses = new ArrayList<String>(reponseDepart.getResults().size());
            for (final GeocoderResult result : reponseDepart.getResults()) {
                adresses.add(result.getFormattedAddress());
            }
            new AlertDialog.Builder(this).setTitle(R.string.textAdresseArrivee).setItems(adresses.toArray(new String[adresses.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int item) {
                    if (reponseArrivee != null && reponseArrivee.getResults().size() > 1) {
                        final GeocoderResult result = reponseDepart.getResults().get(item);
                        reponseDepart.getResults().clear();
                        reponseDepart.getResults().add(result);
                        traiterAdresseMultiple(reponseDepart, reponseArrivee);
                    } else {
                        calculItineraire(reponseDepart.getResults().get(item), reponseArrivee == null ? null
                                : reponseArrivee.getResults().get(0));
                    }
                }
            }).show();
        } else {
            // Choix de l'adresse de destination
            final List<String> adresses = new ArrayList<String>(reponseArrivee.getResults().size());
            for (final GeocoderResult result : reponseArrivee.getResults()) {
                adresses.add(result.getFormattedAddress());
            }
            new AlertDialog.Builder(this).setTitle(R.string.textAdresseArrivee).setItems(adresses.toArray(new String[adresses.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int item) {
                    calculItineraire(reponseDepart == null ? null : reponseDepart.getResults().get(0),
                            reponseArrivee.getResults().get(item));
                }
            }).show();
        }
    }

    private void calculItineraire(final GeocoderResult resultDepart, final GeocoderResult resultArrivee) {
        final double latitudeDepart;
        final double longitudeDepart;
        final double latitudeArrivee;
        final double longitudeArrivee;
        if (resultDepart != null) {
            latitudeDepart = resultDepart.getGeometry().getLocation().getLat().doubleValue();
            longitudeDepart = resultDepart.getGeometry().getLocation().getLng().doubleValue();
        } else {
            latitudeDepart = locationUtil.getCurrentBestLocation().getLatitude();
            longitudeDepart = locationUtil.getCurrentBestLocation().getLongitude();
        }
        if (resultArrivee != null) {
            latitudeArrivee = resultArrivee.getGeometry().getLocation().getLat().doubleValue();
            longitudeArrivee = resultArrivee.getGeometry().getLocation().getLng().doubleValue();
        } else {
            latitudeArrivee = locationUtil.getCurrentBestLocation().getLatitude();
            longitudeArrivee = locationUtil.getCurrentBestLocation().getLongitude();
        }
        final Request request = new Request(latitudeDepart, longitudeDepart, latitudeArrivee, longitudeArrivee,
                calendar.getTime());
        request.setMaxWalkDistance(1500.0);
        final List<TraverseMode> modes = new ArrayList<TraverseMode>();
        modes.add(TraverseMode.WALK);
        if (bus && tram) {
            modes.add(TraverseMode.TRANSIT);
        } else if (bus) {
            modes.add(TraverseMode.BUS);
        } else if (tram) {
            modes.add(TraverseMode.TRAM);
        }
        request.setModes(new TraverseModeSet(modes));
        LOG_YBO.debug(request.constructUrl("http://transports-rennes.ic-s.org/bordeaux-api-webapp"));
        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog progressDialog;
            private Response reponse;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(ItineraireRequete.this, "", getString(R.string.calculItineraire),
                        true);
            }

            @Override
            protected Void doInBackground(final Void... voids) {
                try {
                    reponse = CalculItineraires.INSTANCE.getItineraries(request);
                } catch (final OpenTripPlannerException e) {
                    if (e.getCause() != null
                            && (e.getCause() instanceof SocketException
                            || e.getCause() instanceof FileNotFoundException
                            || e.getCause() instanceof UnknownHostException
                            || e.getCause() instanceof JsonIOException
                            || e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof JsonParseException)) {
                        LOG_YBO.erreur("Erreur de connextion", e.getCause());
                        return null;
                    } else {
                        throw new TcbException(e);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(final Void result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
                if (reponse == null) {
                    Toast.makeText(ItineraireRequete.this, getString(R.string.erreurReseau), Toast.LENGTH_LONG).show();
                } else if (reponse.getError() != null) {
                    LOG_YBO.erreur(reponse.getError().getMsg());
                    int message = R.string.erreur_calculItineraires;
                    switch (Message.findEnumById(reponse.getError().getId())) {
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
                    Toast.makeText(ItineraireRequete.this, message, Toast.LENGTH_LONG).show();
                } else {
                    final int heureDepart = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
                    final Intent intent = new Intent(ItineraireRequete.this, Itineraires.class).putExtra("itineraireReponse", ItineraireReponse.convert(reponse.getPlan())).putExtra("heureDepart", heureDepart);
                    startActivity(intent);
                }
            }
        }.execute((Void) null);
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private void majTextViews() {
        dateItineraire.setText(DATE_FORMAT.format(calendar.getTime()));
        heureItineraire.setText(TIME_FORMAT.format(calendar.getTime()));
    }

    private static final int DATE_DIALOG_ID = 0;
    private static final int TIME_DIALOG_ID = 1;

    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(final DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            majTextViews();
        }
    };

    private final TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(final TimePicker timePicker, final int hourOfDay, final int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            majTextViews();
        }
    };

    @Override
    protected Dialog onCreateDialog(final int id) {
        if (id == DATE_DIALOG_ID) {
            return new DatePickerDialog(this, mDateSetListener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        if (id == TIME_DIALOG_ID) {
            return new TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), true);
        }
        return null;
    }

    @Override
    public void updateLocation(final Location location) {
    }

}
