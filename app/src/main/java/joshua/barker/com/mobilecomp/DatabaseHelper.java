package joshua.barker.com.mobilecomp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String DB_NAME = "mydb.db";
    private static final int VERSION = 1;

    private static final String TABLE_NAME = "bldc_driver";
    public static final String ID = "id";
    public static final String DATE_TIME = "date_time";
    public static final String TEMP = "temp";
    public static final String CURRENT = "current";
    public static final String PWM = "pwm_value";

    private SQLiteDatabase myDB;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: called");

        String queryTable = "CREATE TABLE " + TABLE_NAME +
                " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE_TIME + " TEXT NOT NULL, " +
                TEMP + "TEXT NOT NULL, " +
                CURRENT + " TEXT NOT NULL, " +
                PWM + " REAL NOT NULL" +
                ")";

        db.execSQL(queryTable);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: called");

    }

    public void openDB() {
        Log.d(TAG, "openDB: called");

        myDB = getWritableDatabase();
    }

    public void closeDB() {
        Log.d(TAG, "closeDB: called");

        if (myDB != null && myDB.isOpen()) {
            myDB.close();
        }
    }

    public long insert(int id, String fName, String lName, String address, Double salary) {
        Log.d(TAG, "insert: called");

        ContentValues values = new ContentValues();
        if (id != -1)
            values.put(ID, id);
        values.put(DATE_TIME, fName);
        values.put(TEMP, lName);
        values.put(CURRENT, address);
        values.put(PWM, salary);

        return myDB.insert(TABLE_NAME, null, values);
    }

    public long update(int id, String fName, String lName, String address, Double salary) {
        Log.d(TAG, "insert: called");

        ContentValues values = new ContentValues();

        values.put(ID, id);
        values.put(DATE_TIME, fName);
        values.put(TEMP, lName);
        values.put(CURRENT, address);
        values.put(PWM, salary);

        String where = ID + " = " + id;

        return myDB.update(TABLE_NAME, values, where,null);
    }

    public long delete(int id) {
        Log.d(TAG, "insert: called");

        String where = ID + " = " + id;

        return myDB.delete(TABLE_NAME, where,null);
    }

    public Cursor getAllRecords(){

       // myDB.query(TABLE_NAME, null,null,null,null,null,null);

        String query = "SELECT * FROM " + TABLE_NAME;
        return myDB.rawQuery(query, null);
    }

}
