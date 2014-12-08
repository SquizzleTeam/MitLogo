package ch.bbbaden.quizme;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class Starter extends ActionBarActivity {
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLiteDatabase;
	private TableLayout table;
	private Intent frage;
	private Intent neues_thema, starter, online_themen;
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_starter);

		table = (TableLayout) findViewById(R.id.table);

		setIntent();
		setDatabase();
		viewThemen();
		}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void setIntent() {
		frage = new Intent("ch.bbbaden.quizme.FRAGEN_EINES_THEMAS");
		frage.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		frage.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		neues_thema = new Intent("ch.bbbaden.quizme.NEUESTHEMA");
		neues_thema.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		neues_thema.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		starter = new Intent("ch.bbbaden.quizme.STARTER");
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		starter.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		
		online_themen = new Intent("ch.bbbaden.quizme.ONLINETHEMEN");
	}

	public void openNeuesThema(View view) {
		neues_thema.putExtra("bearbeiten", false);
		startActivity(neues_thema);
	}

	private void setDatabase() {
		dbZugriff = new DBZugriff(this);
		sqLiteDatabase = dbZugriff.getWritableDatabase();
	}

	private void viewThemen() {
		ArrayList<String> Themen = dbZugriff.getAllThemen(sqLiteDatabase);
		ArrayList<Integer> ThemenID = dbZugriff.getAllThemenID(sqLiteDatabase);

		for (int i = 0; i < Themen.size(); i++) {
			final String dasThema = Themen.get(i);
			final int dieID = ThemenID.get(i);

			final Button Thema = new Button(this);
			Thema.setText(dasThema);
			Thema.setTextColor(Color.parseColor("#FFFFFF"));
			Thema.setId(dieID);
			Drawable d = getResources().getDrawable(R.drawable.folder);
			Thema.setBackgroundDrawable(d);
			TableRow tr = new TableRow(this);
			tr.addView(Thema);

			Thema.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					frage.putExtra("themenName", dasThema);
					frage.putExtra("themenID", dieID);
					startActivity(frage);
				}
			});
			Thema.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(final View v) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(v
							.getContext());
					dialog.setPositiveButton("Löschen",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									AlertDialog.Builder builder = new AlertDialog.Builder(
											v.getContext());
									builder.setTitle("Löschen")
											.setMessage(
													"Wollen Sie dieses Thema löschen?")
											.setIcon(
													android.R.drawable.ic_dialog_alert)
											.setPositiveButton(
													"Ja",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															dbZugriff
																	.loescheThema(
																			sqLiteDatabase,
																			dieID);
															startActivity(starter);
														}
													})
											.setNegativeButton("Nein", null)
											.show();

								}
							});
					dialog.setNeutralButton("Teilen", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new OnlineConnector().execute(Thema.getText().toString());
							Toast.makeText(context, "Das Thema wurde erfolgreich geuploadet.", Toast.LENGTH_LONG).show();
						}
					});
					
					dialog.setNegativeButton("Bearbeiten",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent bearbeiten = new Intent(
											"ch.bbbaden.quizme.NEUESTHEMA");
									bearbeiten
											.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
									bearbeiten
											.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
									bearbeiten.putExtra("themenID", dieID);
									bearbeiten.putExtra("themenName", dasThema);
									bearbeiten.putExtra("bearbeiten", true);
									startActivity(bearbeiten);
								}

							});

					dialog.show();

					return false;
				}

			});
			table.addView(tr);
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.neuesthema:
			neues_thema.putExtra("bearbeiten", false);
			startActivity(neues_thema);
			break;
		case R.id.Online:
			startActivity(online_themen);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.starter, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	
	private class OnlineConnector extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			OnlineMode on = new OnlineMode(context);
			on.setDatabase();
			on.getJSON(params[0]);
			on.inputThema();
			return null;
		}
		
	}
}
