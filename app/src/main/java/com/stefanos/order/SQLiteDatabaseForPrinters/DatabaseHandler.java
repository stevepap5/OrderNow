package com.stefanos.order.SQLiteDatabaseForPrinters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class DatabaseHandler extends SQLiteOpenHelper {

    private Context ctx;
    public DatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IP_ADDRESS_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY," + Constants.KEY_IP_ADDRESS + " TEXT,"
                + Constants.KEY_PORT + " TEXT);";

        db.execSQL(CREATE_IP_ADDRESS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        onCreate(db);

    }

    /**
     *  CRUD OPERATIONS: Create, Read, Update, Delete Methods
     */

    //Add ipaddress
    public void addIpAddress(IpAdressPrinter ipAdressPrinter) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_IP_ADDRESS, ipAdressPrinter.getIpAdress());
        values.put(Constants.KEY_PORT, ipAdressPrinter.getPort());


        //Insert the row
        db.insert(Constants.TABLE_NAME, null, values);

        Log.d("Saved!!", "Saved to DB");

    }


    //Get a ipadress
    public IpAdressPrinter getIpAddress(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {Constants.KEY_ID,
                        Constants.KEY_IP_ADDRESS, Constants.KEY_PORT},
                Constants.KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();


        IpAdressPrinter ipAdressPrinter = new IpAdressPrinter();
        ipAdressPrinter.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
        ipAdressPrinter.setIpAdress(cursor.getString(cursor.getColumnIndex(Constants.KEY_IP_ADDRESS)));
        ipAdressPrinter.setPort(cursor.getString(cursor.getColumnIndex(Constants.KEY_PORT)));

        //convert timestamp to something readable


        return ipAdressPrinter;
    }


    //Get all ipaddresses
    public List<IpAdressPrinter> getAllIpAddresses() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<IpAdressPrinter> ipAdressPrinterList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[] {
                Constants.KEY_ID, Constants.KEY_IP_ADDRESS,
                Constants.KEY_PORT}, null, null, null, null, Constants.KEY_PORT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                IpAdressPrinter ipAdressPrinter = new IpAdressPrinter();
                ipAdressPrinter.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                ipAdressPrinter.setIpAdress(cursor.getString(cursor.getColumnIndex(Constants.KEY_IP_ADDRESS)));
                ipAdressPrinter.setPort(cursor.getString(cursor.getColumnIndex(Constants.KEY_PORT)));



                // Add to the ipList
                ipAdressPrinterList.add(ipAdressPrinter);

            }while (cursor.moveToNext());
        }

        return ipAdressPrinterList;
    }


    public void deleteAllIpAddresses(){
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("delete from "+ Constants.TABLE_NAME);
    }
    //Updated Grocery
    public int updateIpAddress(IpAdressPrinter ipAdressPrinter) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_IP_ADDRESS, ipAdressPrinter.getIpAdress());
        values.put(Constants.KEY_PORT, ipAdressPrinter.getPort());



        //update row
        return db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + "=?", new String[] { String.valueOf(ipAdressPrinter.getId())} );
    }


    //Delete ipaddress
    public void deleteIpAddress(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + " = ?",
                new String[] {String.valueOf(id)});

        db.close();

    }



    //Get count
    public int getIpAddressCount() {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
}
