package ch.bbbaden.quizme;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class OnlineMode {
	private DBZugriff dbZugriff;
	private SQLiteDatabase sqLitedatabase;
	private Context context;
	private String thema;
	private JSONArray fragen, antworten;

	OnlineMode(Context context) {
		this.context = context;
	}

	public void setDatabase() {
		dbZugriff = new DBZugriff(context);
		sqLitedatabase = dbZugriff.getWritableDatabase();
	}

	public void getJSON(String thema) {
		this.thema = thema;
		int localthemenID = dbZugriff.getThemenIDByThemaName(sqLitedatabase,
				thema);
		fragen = dbZugriff
				.getAllFragenByThemenID(sqLitedatabase, localthemenID);
		int themenID = dbZugriff.getThemenIDByThemaName(sqLitedatabase, thema);
		ArrayList<Integer> fragenIDlist = dbZugriff
				.getAllFragenIDIntTABLE_FRAGEbyThema(sqLitedatabase, themenID);
		antworten = dbZugriff.getAllAntwortenJSONbyFID_Frage(sqLitedatabase, fragenIDlist);
	}

	public void inputThema() {
		String urlParameterThema = null;
		try {
			urlParameterThema = "Thema=" + URLEncoder.encode(thema, "UTF-8")
					+ "&Fragen="
					+ URLEncoder.encode(fragen.toString(), "UTF-8") + "&Antworten=" + URLEncoder.encode(antworten.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL("http://192.168.4.108/Squizzle/inputThema.php");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameterThema);
			wr.flush();
			wr.close();

			
			
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (connection != null) {
				connection.disconnect();
			}
			
			int themenID = dbZugriff.getThemenIDByThemaName(sqLitedatabase, thema);
			dbZugriff.setGeuploadetByThema(sqLitedatabase, String.valueOf(themenID));
		}
	}

	public ArrayList<String> getAllThemen() {
		ArrayList<String> themen = new ArrayList<String>(); 
		URL url;
		HttpURLConnection connection = null;
		try {
			url = new URL("http://192.168.4.108/Squizzle/getThemen.php");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.flush();
			wr.close();

			
			
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			
			while ((line = rd.readLine()) != null) {
				themen.add(line);
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
		return themen;
	}

}
