package de.hhn.android.licenseplatedecoder.db;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import de.hhn.android.licenseplatedecoder.decoder.RegionInformation;
/*
 ** CLASS NAME: 
 **	  DBHelper
 **
 ** DESCRIPTION:
 **   This class is used to manage the DB
 **
 **
 ** DEVELOPED BY:
 **   Mario Orozco Borrego (MOB)
 **
 ** SUPERVISED BY:
 **	  Prof. Dr.-Ing. Permantier, Gerald
 **
 **		
 ** NOTES:
 **   
 **
 */

public class DBHelper extends SQLiteOpenHelper{

	@SuppressLint("SdCardPath")
	private static String DB_PATH = "/data/data/de.hhn.android.licenseplatedecoder/databases/";

	private static String DB_NAME = "lpdecoderDB";

	private SQLiteDatabase database;

	private final Context context;

	/** Countries table */
	public static final String COUNTRIES_ID_COL = "_id";
	public static final String COUNTRIES_CODE_COL = "countryLPCode";
	public static final String COUNTRIES_NAME_ENG_COL = "name_en";
	public static final String COUNTRIES_NAME_ES_COL = "name_es";
	
	@SuppressWarnings("unused")
	private static final String[] countries_cols = new String[]{COUNTRIES_ID_COL, COUNTRIES_CODE_COL,COUNTRIES_NAME_ENG_COL,COUNTRIES_NAME_ES_COL};
	private static final String COUNTRIES_TABLE = "countries";
	
	/** Regions table */
	public static final String REGIONS_ID_COL = "_id";
	public static final String REGIONS_COUNTRY_ID_COL = "country_id";
	public static final String REGIONS_LOCATION_NAME_COL = "locationName";
	public static final String REGIONS_REGION_NAME_COL = "regionName";
	public static final String REGIONS_LPCODE_COL = "LPCode";
	
	@SuppressWarnings("unused")
	private static final String[] regions_cols = new String[]{REGIONS_ID_COL,REGIONS_COUNTRY_ID_COL,REGIONS_LOCATION_NAME_COL,REGIONS_REGION_NAME_COL,REGIONS_LPCODE_COL};
	private static final String REGIONS_TABLE = "regions";

	
	/**
	 * Default constructor
	 * @param context the context
	 */
	public DBHelper(Context context){
		super(context,DB_NAME,null,1);
		this.context = context;
	}

	private void createDatabase() throws IOException{
		boolean dbExist = checkDatabase();

		if (!dbExist){
			this.getReadableDatabase();

			try{
				copyDataBase();
			}catch (IOException e){
				throw new Error("Error copying database");
			}
		}
	}

	private boolean checkDatabase(){
		SQLiteDatabase checkDB = null;

		try{
			String path = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){

		}

		if (checkDB != null){
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies the database
	 * @throws IOException
	 */
	private void copyDataBase() throws IOException{

		InputStream input = this.context.getAssets().open(DB_NAME);

		String outFileName = DB_PATH + DB_NAME;

		OutputStream output = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = input.read(buffer)) > 0){
			output.write(buffer,0,length);
		}

		output.flush();
		output.close();
		input.close();
	}

	/**
	 * Opens the database
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException{

		try {
			createDatabase();
		} catch (IOException e) {
			throw new Error("Error creating db");
		}

		String myPath = DB_PATH + DB_NAME;
		database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

	}

	/**
	 * Closes the database
	 */
	@Override
	public synchronized void close() {

		if(database != null)
			database.close();

		super.close();

	}


	@Override
	public void onCreate(SQLiteDatabase arg0) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	public int getCountryCode(String countryCodeStr){
		int countryCodeInt = -1;
		
		if (countryCodeStr.compareTo("DE") == 0){
			countryCodeInt =  1;
		}
		else if (countryCodeStr.compareTo("A") == 0){
			countryCodeInt = 2;
		}
		else if (countryCodeStr.compareTo("F") == 0){
			countryCodeInt =  4;
		}
		else if (countryCodeStr.compareTo("CZ") == 0){
			countryCodeInt = 5;
		}
		else if (countryCodeStr.compareTo("PL") == 0){
			countryCodeInt = 6;
		}
		else if (countryCodeStr.compareTo("UK") == 0 || countryCodeStr.compareTo("GB") == 0){
			countryCodeInt = 7;
		}
		else if (countryCodeStr.compareTo("I") == 0){
			countryCodeInt = 8;
		}
		else if (countryCodeStr.compareTo("IRL") == 0){
			countryCodeInt = 9;
		}
		else if (countryCodeStr.compareTo("SK") == 0){
			countryCodeInt = 10;
		}
		else if (countryCodeStr.compareTo("TR") == 0){
			countryCodeInt = 11;
		}
		else if (countryCodeStr.compareTo("RO") == 0){
			countryCodeInt = 13;
		}
		else
			countryCodeInt = -1; /** Non recognized or (russia 12 or switzerland 3) */	
		
		return countryCodeInt;
	}
	
	public RegionInformation getRegionInfo(String countryCode, String regionCode){
		
		int countryCodeInt = getCountryCode(countryCode);
		
		if (countryCodeInt == -1)
			return null;
		
		return getRegionInfo(countryCodeInt, regionCode);
		
	}
	
	@SuppressLint("UseValueOf")
	public RegionInformation getRegionInfo(int countryCode, String regionCode){
		String[] result = new String[2]; // Location, SuperRegion //
		String countryCodeStr = new Integer(countryCode).toString();
		
		Cursor res = database.query(REGIONS_TABLE, new String[]{REGIONS_LOCATION_NAME_COL,REGIONS_REGION_NAME_COL}, new String(REGIONS_COUNTRY_ID_COL + " = ? AND " + REGIONS_LPCODE_COL + " LIKE " + "'" + regionCode+"%'"), new String[]{countryCodeStr}, null, null, null);
		if (res.getCount() == 0){
			return null;
		}
		else{
			res.moveToFirst();
			// Get the first only (actually it must be only one)
			result[0] = res.getString(res.getColumnIndex(REGIONS_LOCATION_NAME_COL));
			result[1] = res.getString(res.getColumnIndex(REGIONS_REGION_NAME_COL));
		}
		
		Cursor resCountry = database.query(COUNTRIES_TABLE, new String[]{COUNTRIES_NAME_ENG_COL}, new String(COUNTRIES_ID_COL + " = ?"), new String[]{countryCodeStr}, null, null, null, null);
		resCountry.moveToFirst();
		String countryName = resCountry.getString(resCountry.getColumnIndex(COUNTRIES_NAME_ENG_COL));
		if (result[1] == null)
			result[1] = new String("");
		if (result[0] == null)
			result[0] = new String("");
		
		RegionInformation regInfo = new RegionInformation(result[1], result[0], countryName, null);
		
		return regInfo;
	}
	
}
