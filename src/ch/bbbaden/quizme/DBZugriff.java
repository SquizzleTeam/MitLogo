package ch.bbbaden.quizme;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBZugriff extends SQLiteOpenHelper {
	private static int DATABASE_VERSION = 1;
	private static String DATABASE_NAME = "QuizMe";
	private static String THEMEN_TABLE = "Themen";
	private static String TYPEN_TABLE = "Typen";
	private static String FRAGEN_TABLE = "Fragen";
	private static String ANTWORTEN_TABLE = "Antworten";
	private static String CREATE_THEMEN = "CREATE TABLE " + THEMEN_TABLE
			+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, Thema VARCHAR(255));";
	private static String CREATE_TYPEN = "CREATE TABLE " + TYPEN_TABLE
			+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, Typ VARCHAR(50))";
	private static String CREATE_FRAGEN = "CREATE TABLE "
			+ FRAGEN_TABLE
			+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, FID_Typ INTEGER, FID_Thema INTEGER, Frage VARCHAR(255), Gekonnt VARCHAR(20) , Geuploadet INTEGER(3), FOREIGN KEY (FID_Typ) REFERENCES "
			+ TYPEN_TABLE + " (id), FOREIGN KEY (FID_Thema) REFERENCES "
			+ THEMEN_TABLE + " (id));";
	private static String CREATE_ANTWORTEN = "CREATE TABLE "
			+ ANTWORTEN_TABLE
			+ " (id INTEGER PRIMARY KEY AUTOINCREMENT, FID_Frage INTEGER, Antwort VARCHAR(255), Korrekt INTEGER, FOREIGN KEY (FID_Frage) REFERENCES "
			+ FRAGEN_TABLE + " (id));";
	private Context context;

	public DBZugriff(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(CREATE_THEMEN);
			db.execSQL(CREATE_TYPEN);
			db.execSQL(CREATE_FRAGEN);
			db.execSQL(CREATE_ANTWORTEN);
			fülleTypTabelle(db);
		} catch (SQLException e) {
			Toast.makeText(context, "Es ist leider ein Fehler aufgetreten",
					Toast.LENGTH_LONG).show();
		}

	}

	private void fülleTypTabelle(SQLiteDatabase db) {
		String[] typen = { "Schreiben", "Multiple Choice", "A, B, C, D",
				"Wahr / Falsch" };

		for (int i = 0; i < 4; i++) {
			ContentValues schreiben = new ContentValues();
			schreiben.put("Typ", typen[i]);
			db.insert(TYPEN_TABLE, null, schreiben);
		}
	}

	public void insertNewThema(SQLiteDatabase db, String neuesThemaName) {
		ContentValues daten = new ContentValues();
		daten.put("Thema", neuesThemaName);
		db.insert(THEMEN_TABLE, null, daten);
		Toast.makeText(context, "Das Thema wurde eingefügt", Toast.LENGTH_LONG)
				.show();
	}

	public ArrayList<String> getAllThemen(SQLiteDatabase db) {
		String[] spalten = { "id", "Thema" };
		Cursor cursor = db.query(THEMEN_TABLE, spalten, null, null, null, null,
				null);

		ArrayList<String> list = new ArrayList<String>();
		while (cursor.moveToNext()) {
			String dasThema = cursor.getString(1);
			list.add(dasThema);
		}
		return list;
	}

	public ArrayList<Integer> getAllThemenID(SQLiteDatabase db) {
		String[] spalten = { "id", "Thema" };
		Cursor cursor = db.query(THEMEN_TABLE, spalten, null, null, null, null,
				null);

		ArrayList<Integer> list = new ArrayList<Integer>();
		while (cursor.moveToNext()) {
			int ThemaID = cursor.getInt(0);
			list.add(ThemaID);
		}
		return list;
	}

	public int getThemenIDByThemaName(SQLiteDatabase db, String thema) {
		String[] spalten = { "id", "Thema" };
		Cursor cursor = db.query(THEMEN_TABLE, spalten, null, null, null, null,
				null);
		int themaID = 0;
		while (cursor.moveToNext()) {
			themaID = cursor.getInt(0);
		}
		return themaID;
	}

	public void loescheThema(SQLiteDatabase db, int themenID) {
		try {
			db.execSQL("DELETE FROM " + THEMEN_TABLE + " WHERE id = '"
					+ themenID + "'");
			db.execSQL("DELETE FROM " + FRAGEN_TABLE + " WHERE FID_Thema = '"
					+ themenID + "'");
			ArrayList<Integer> fragenID = getAllFragenIDIntTABLE_FRAGEbyThema(
					db, themenID);

			for (int i = 0; i < fragenID.size(); i++) {
				db.execSQL("DELETE FROM " + ANTWORTEN_TABLE
						+ " WHERE FID_Frage = '" + fragenID.get(i) + "'");
			}
		} catch (SQLException e) {
			Toast.makeText(context, "Es ist leider ein Fehler aufgetreten.",
					Toast.LENGTH_LONG).show();
		}
	}

	public int getTypID(SQLiteDatabase db, String typ) {
		int typenID = 0;
		String[] spalten = { "id", "Typ" };
		Cursor cursor = db.query(TYPEN_TABLE, spalten, "Typ = '" + typ + "'",
				null, null, null, null);
		while (cursor.moveToNext()) {
			typenID = cursor.getInt(0);
		}
		return typenID;
	}

	public String getTypNamebyID(SQLiteDatabase db, int typID) {
		String typ = null;
		String[] spalten = { "id", "Typ" };
		Cursor cursor = db.query(TYPEN_TABLE, spalten, "id = '" + typID + "'",
				null, null, null, null);
		while (cursor.moveToNext()) {
			typ = cursor.getString(1);
		}
		return typ;
	}

	public long setFrage(SQLiteDatabase db, int typenID, int themenID,
			String frage, int gekonnt) {
		ContentValues daten = new ContentValues();
		daten.put("FID_Typ", typenID);
		daten.put("FID_Thema", themenID);
		daten.put("Frage", frage);
		daten.put("Gekonnt", gekonnt);
		daten.put("Geuploadet", 0);

		long frageID = db.insert(FRAGEN_TABLE, null, daten);

		return frageID;
	}

	public void setAntwort(SQLiteDatabase db, long frageID, String antwort,
			int korrekt) {
		ContentValues daten = new ContentValues();
		daten.put("FID_Frage", frageID);
		daten.put("Antwort", antwort);
		daten.put("Korrekt", korrekt);
		db.insert(ANTWORTEN_TABLE, null, daten);
	}

	public ArrayList<String> getAllFragenInTABLE_FRAGEbyThema(
			SQLiteDatabase db, int themenID) {
		String[] columns = { "id", "FID_Typ", "FID_Thema", "Frage", "Gekonnt" };
		Cursor cursor = db.query(FRAGEN_TABLE, columns, "FID_Thema = '"
				+ themenID + "'", null, null, null, null);
		ArrayList<String> fragelist = new ArrayList<String>();

		while (cursor.moveToNext()) {
			String frage = cursor.getString(3);
			fragelist.add(frage);
		}

		return fragelist;

	}

	public ArrayList<Integer> getAllFragenIDIntTABLE_FRAGEbyThema(
			SQLiteDatabase db, int themenID) {
		String[] columns = { "id", "FID_Typ", "FID_Thema", "Frage", "Gekonnt" };
		Cursor cursor = db.query(FRAGEN_TABLE, columns, "FID_Thema = '"
				+ themenID + "'", null, null, null, null);
		ArrayList<Integer> fragelist = new ArrayList<Integer>();

		while (cursor.moveToNext()) {
			int fragenID = cursor.getInt(0);
			fragelist.add(fragenID);
		}

		return fragelist;
	}

	public ArrayList<Integer> getAllFragenIDIntTABLE_FRAGEbyThemaANDGekonnt(
			SQLiteDatabase db, int themenID) {
		String[] columns = { "id", "FID_Typ", "FID_Thema", "Frage", "Gekonnt" };
		Cursor cursor = db.query(FRAGEN_TABLE, columns, "FID_Thema = '"
				+ themenID + "' AND Gekonnt = 0", null, null, null, null);
		ArrayList<Integer> fragelist = new ArrayList<Integer>();

		while (cursor.moveToNext()) {
			int fragenID = cursor.getInt(0);
			fragelist.add(fragenID);
		}

		return fragelist;
	}

	public ArrayList<Integer> getAllFID_TypInFRAGEN_TABLE(SQLiteDatabase db,
			int themenID) {
		String[] columns = { "id", "FID_Typ", "FID_Thema", "Frage", "Gekonnt" };
		Cursor cursor = db.query(FRAGEN_TABLE, columns, "FID_Thema = '"
				+ themenID + "'", null, null, null, null);
		ArrayList<Integer> typenlist = new ArrayList<Integer>();

		while (cursor.moveToNext()) {
			int typenID = cursor.getInt(1);
			typenlist.add(typenID);
		}

		return typenlist;
	}

	public String getFrageByFrageID(SQLiteDatabase db, int fragenID) {
		String[] columns = { "id", "FID_Typ", "FID_Thema", "Frage", "Gekonnt" };
		Cursor cursor = db.query(FRAGEN_TABLE, columns, "id = '" + fragenID
				+ "'", null, null, null, null);
		String frage = null;

		while (cursor.moveToNext()) {
			frage = cursor.getString(3);
		}
		return frage;
	}

	public int getFID_TypByFrageID(SQLiteDatabase db, int fragenID) {
		String[] columns = { "id", "FID_Typ", "FID_Thema", "Frage", "Gekonnt" };
		Cursor cursor = db.query(FRAGEN_TABLE, columns, "id = '" + fragenID
				+ "'", null, null, null, null);
		int FID_Typ = 0;

		while (cursor.moveToNext()) {
			FID_Typ = cursor.getInt(1);
		}
		return FID_Typ;
	}

	public ArrayList<String> getAntwortenByFID_Fragen(SQLiteDatabase db,
			int fragenID, boolean ergebnis) {
		String[] columns = { "id", "FID_Frage", "Antwort", "Korrekt" };
		Cursor cursor;
		if (ergebnis == true) {
			cursor = db.query(ANTWORTEN_TABLE, columns, "FID_Frage = '"
					+ fragenID + "' AND Korrekt ='1'", null, null, null, null);
		} else {
			cursor = db.query(ANTWORTEN_TABLE, columns, "FID_Frage = '"
					+ fragenID + "'", null, null, null, null);
		}
		ArrayList<String> antwortenList = new ArrayList<String>();

		while (cursor.moveToNext()) {
			String antwort = cursor.getString(2);
			antwortenList.add(antwort);
		}

		return antwortenList;
	}

	public ArrayList<String> getKorrektheitByFID_Fragen(SQLiteDatabase db,
			int fragenID) {
		String[] columns = { "id", "FID_Frage", "Antwort", "Korrekt" };
		Cursor cursor;
		cursor = db.query(ANTWORTEN_TABLE, columns, "FID_Frage = '" + fragenID
				+ "'", null, null, null, null);
		ArrayList<String> antwortenList = new ArrayList<>();

		while (cursor.moveToNext()) {
			String antwort = cursor.getString(3);
			antwortenList.add(antwort);
		}

		return antwortenList;
	}

	public void updateFrageZuGekonnt(SQLiteDatabase db, int fragenID) {
		String sql = "UPDATE " + FRAGEN_TABLE + " SET Gekonnt = 1 WHERE id = '"
				+ fragenID + "'";
		db.execSQL(sql);
	}

	public long updateFrage(SQLiteDatabase db, int themenID, int typenID,
			String frage, int gekonnt, int fragenID) {
		db.execSQL("DELETE FROM " + FRAGEN_TABLE + " WHERE id = '" + fragenID
				+ "'");

		ContentValues daten = new ContentValues();
		daten.put("FID_Typ", typenID);
		daten.put("FID_Thema", themenID);
		daten.put("Frage", frage);
		daten.put("Gekonnt", gekonnt);
		daten.put("Geuploadet", 0);

		long frageID = db.insert(FRAGEN_TABLE, null, daten);

		return frageID;
	}

	public void setAllKorrektToFalse(SQLiteDatabase db, int themenID) {
		String sql = "UPDATE " + FRAGEN_TABLE
				+ " SET Gekonnt = 0 WHERE FID_Thema = '" + themenID + "'";
		db.execSQL(sql);
	}

	public void deleteFrage(SQLiteDatabase db, int fragenID) {
		String sql = "DELETE FROM " + FRAGEN_TABLE + " WHERE id = '" + fragenID
				+ "'";
		db.execSQL(sql);
		String sql2 = "DELETE FROM " + ANTWORTEN_TABLE + " WHERE FID_Frage = '"
				+ fragenID + "'";
		db.execSQL(sql2);
	}

	public void updateThema(SQLiteDatabase db, String themenName,
			String neuesThema) {
		String sql = "UPDATE " + THEMEN_TABLE + " SET Thema='" + neuesThema
				+ "' WHERE Thema = '" + themenName + "'";
		db.execSQL(sql);
	}

	public void updateAntwort(SQLiteDatabase db, int FID_Frage, String antwort,
			int korrekt) {
		String del = "DELETE FROM " + ANTWORTEN_TABLE + " WHERE FID_Frage = '"
				+ FID_Frage + "'";
		db.execSQL(del);

		ContentValues daten = new ContentValues();
		daten.put("FID_Frage", FID_Frage);
		daten.put("Antwort", antwort);
		daten.put("Korrekt", korrekt);

		db.insert(ANTWORTEN_TABLE, null, daten);

	}

	public JSONArray getAllFragenByThemenID(SQLiteDatabase db, int themenID) {
		String[] columns = { "id","FID_Typ", "Frage" };
		JSONArray jsonArray = new JSONArray();

		Cursor cursor = db.query(FRAGEN_TABLE, columns, "FID_Thema = '"
				+ themenID + "' AND Geuploadet = '0'", null, null, null, null);
		while (cursor.moveToNext()) {
			JSONObject ob = new JSONObject();
			try {
				ob.put("ID", cursor.getString(0));
				ob.put("FID_Typ", cursor.getString(1));
				ob.put("Frage", cursor.getString(2));
				jsonArray.put(ob);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonArray;
	}

	public JSONArray getAllAntwortenJSONbyFID_Frage(SQLiteDatabase db,
			ArrayList<Integer> fid_frage) {
		JSONArray antworten = new JSONArray();
		String[] columns = { "FID_Frage", "Antwort", "Korrekt" };

		for (int i = 0; i < fid_frage.size(); i++) {
				Cursor cursor = db.query(ANTWORTEN_TABLE, columns,
						"FID_Frage = '" + fid_frage.get(i) + "'", null, null,
						null, null);
				while (cursor.moveToNext()) {
					JSONObject ant = new JSONObject();
					try {
						ant.put("Antwort", cursor.getString(1));
						ant.put("Korrekt", cursor.getString(2));
						ant.put("FragenID", cursor.getString(0));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					antworten.put(ant);
				}
			}
		
		return antworten;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void setGeuploadetByThema(SQLiteDatabase db,
			String themenID) {
		String sql = "UPDATE " + FRAGEN_TABLE + " SET Geuploadet='1' WHERE FID_Thema = '" + themenID + "'";
		db.execSQL(sql);
	}
	

}
