package ch.bbbaden.quizme;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class OnlineThemen extends ActionBarActivity {
	private  Context context;
	private static ArrayList<String> themen;
	private TableLayout tb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = this;
		super.onCreate(savedInstanceState);
		setLayout();
		new OnlineConnector().execute();
		}
	
	private void setLayout(){
		setContentView(R.layout.activity_online_themen);
		tb = (TableLayout) findViewById(R.id.tl);
	}
	
	private void setTable(){
		for(int i = 0; i < themen.size(); i++){
			TableRow tr = new TableRow(context);
			Button btn = new Button(context);
			btn.setText(themen.get(i));
			Drawable d = getResources().getDrawable(R.drawable.folder);
			btn.setBackgroundDrawable(d);
			btn.setTextColor(Color.parseColor("#FFFFFF"));
			btn.setOnLongClickListener(new OnLongClickListener(){

				@Override
				public boolean onLongClick(final View v) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(v
							.getContext());
					dialog.setPositiveButton("Download",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									AlertDialog.Builder builder = new AlertDialog.Builder(
											v.getContext());
									builder.setTitle("Download")
											.setMessage(
													"Wollen Sie dieses Thema downloaden?")
											.setIcon(
													android.R.drawable.ic_dialog_alert)
											.setPositiveButton(
													"Ja",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
														new DownloadManager().execute();
														}
													})
											.setNegativeButton("Nein", null)
											.show();

								}
							});
					return false;
				}
				
				
			});
			tr.addView(btn);
			tb.addView(tr);
		}
	}
	

	
	
	private class OnlineConnector extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			OnlineMode on = new OnlineMode(context);
			themen = on.getAllThemen();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			setTable();
			super.onPostExecute(result);
		}
		
		
	}
	
	private class DownloadManager extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
