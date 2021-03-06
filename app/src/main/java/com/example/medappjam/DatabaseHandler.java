package com.example.medappjam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "patientProviderInfo";

    //Patient table: username & password
    private static final String TABLE_PATIENT = "patient";
    private static final String PATIENT_USERNAME = "username";
    private static final String PATIENT_PASSWORD = "password";

    //Provider table: name & phoneNumber
    private static final String TABLE_PROVIDER = "provider";
    private static final String PROVIDER_NAME = "name";
    private static final String PROVIDER_PHONE = "phoneNumber";

    //Profiles table: patient username(PATIENT_USERNAME) & condition
    private static final String TABLE_PROFILE = "condition";
    private static final String PROFILE = "condition";

    //Date table(for filling out "How are you doing today?" form): patient username(PATIENT_USERNAME), day, year
    private static final String TABLE_INPUT_DATE = "dayOfLastInput";
    private static final String DAY = "day";
    private static final String YEAR = "year";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("tag", "onCreate database");

        String CREATE_PATIENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PATIENT + "("
                + PATIENT_USERNAME + " TEXT UNIQUE,"
                + PATIENT_PASSWORD + " TEXT,"
                + "PRIMARY KEY(" + PATIENT_USERNAME +")"
                + ");";

        String CREATE_PROVIDER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PROVIDER + "("
                + PROVIDER_NAME + " TEXT,"
                + PROVIDER_PHONE + " TEXT,"
                + PATIENT_USERNAME + " TEXT,"
                + "FOREIGN KEY(" + PATIENT_USERNAME + ") REFERENCES " + TABLE_PATIENT + "(" + PATIENT_USERNAME + ") ON DELETE CASCADE"
                + ");";

        String CREATE_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PROFILE + "("
                + PROFILE + " TEXT,"
                + PATIENT_USERNAME + " TEXT,"
                + "FOREIGN KEY(" + PATIENT_USERNAME + ") REFERENCES " + TABLE_PATIENT + "(" + PATIENT_USERNAME + ") ON DELETE CASCADE"
                + ");";

        String CREATE_INPUT_DATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_INPUT_DATE + "("
                + DAY + " INTEGER,"
                + YEAR + " INTEGER,"
                + PATIENT_USERNAME + " TEXT,"
                + "FOREIGN KEY(" + PATIENT_USERNAME + ") REFERENCES " + TABLE_PATIENT + "(" + PATIENT_USERNAME + ") ON DELETE CASCADE"
                + ");";

        db.execSQL(CREATE_PATIENT_TABLE);
        Log.d("tag", "created patient table");
        db.execSQL(CREATE_PROVIDER_TABLE);
        Log.d("tag", "created provider table");
        db.execSQL(CREATE_PROFILE_TABLE);
        Log.d("tag", "created profile table");
        db.execSQL(CREATE_INPUT_DATE_TABLE);
        Log.d("databaseHandler", "created all tables");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROVIDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }


    public void addPatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PATIENT_USERNAME, patient.getUsername());
        values.put(PATIENT_PASSWORD, patient.getPassword());

        db.insert(TABLE_PATIENT, null, values);
        db.close();
    }


    public Patient getPatient(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                "ROWID",
                PATIENT_USERNAME,
                PATIENT_PASSWORD
        };

        //where username = username
        String selection = PATIENT_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_PATIENT, projection, selection, selectionArgs, null, null, null);

        //move cursor to first row if found
        if(cursor.moveToFirst()) {
            //cursor.moveToFirst();
            Patient patient = new Patient(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
            return patient;
        }
        else {
            return null;
        }
    }


    public ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> patients = new ArrayList<>();

        String selectQuery = "SELECT ROWID, * FROM " + TABLE_PATIENT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do{
                Patient patient = new Patient(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
                patients.add(patient);
            } while(cursor.moveToNext());
        }

        return patients;
    }

    //update patient info; returns # of rows affected
    public int updatePatient(Patient patient) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PATIENT_USERNAME, patient.getUsername());
        values.put(PATIENT_PASSWORD, patient.getPassword());

        String whereClause = PATIENT_USERNAME + " = ?";
        String[] whereArgs = {patient.getUsername()};

        return db.update(TABLE_PATIENT, values, whereClause, whereArgs);
    }

    //delete patient; returns # of rows affected
    public int deletePatient(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = PATIENT_USERNAME + " = ?";
        String[] whereArgs = {username};

        return db.delete(TABLE_PATIENT, whereClause, whereArgs);
    }

    public void addProvider(Provider provider, String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROVIDER_NAME, provider.getName());
        values.put(PROVIDER_PHONE, provider.getPhoneNumber());
        values.put(PATIENT_USERNAME, username);

        db.insert(TABLE_PROVIDER, null, values);
        db.close();
    }

    //Get all providers of the patient
    public ArrayList<Provider> getAllProviders(String username) {
        ArrayList<Provider> providers = new ArrayList<>();

        String selectQuery = "SELECT " + PROVIDER_NAME + ", " + PROVIDER_PHONE
                + " FROM " + TABLE_PROVIDER
                + " WHERE " + PATIENT_USERNAME + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {username});

        if(cursor.moveToFirst()) {
            do{
                Provider provider = new Provider(cursor.getString(0), cursor.getString(1));
                providers.add(provider);
            } while(cursor.moveToNext());
        }

        return providers;
    }

    //Delete the provider related to the patient
    public int deleteProvider(Provider provider, String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = PATIENT_USERNAME + " = ? AND " + PROVIDER_NAME + " = ? AND " + PROVIDER_PHONE + " = ?";
        String[] whereArgs = {username, provider.getName(), provider.getPhoneNumber()};

        return db.delete(TABLE_PROVIDER, whereClause, whereArgs);
    }

    public void addDate(String username, InputDate date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DAY, date.getDay());
        values.put(YEAR, date.getYear());
        values.put(PATIENT_USERNAME, username);

        db.insert(TABLE_INPUT_DATE, null, values);
        db.close();
    }

    public InputDate getDate(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DAY,
                YEAR,
                PATIENT_USERNAME
        };

        String selection = PATIENT_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_INPUT_DATE, projection, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()) {
            InputDate date = new InputDate(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)));
            return date;
        }
        else {
            return null;
        }
    }

    public int updateDate(String username, InputDate date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DAY, date.getDay());
        values.put(YEAR, date.getYear());

        String whereClause = PATIENT_USERNAME + " = ?";
        String[] whereArgs = {username};

        return db.update(TABLE_INPUT_DATE, values, whereClause, whereArgs);
    }

}
