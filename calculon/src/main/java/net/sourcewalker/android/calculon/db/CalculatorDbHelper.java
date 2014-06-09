package net.sourcewalker.android.calculon.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.sourcewalker.android.calculon.Constants;

public class CalculatorDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "calculator.db";
    private static final int DB_VERSION = 2;

    public CalculatorDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HistoryTable.SCHEMA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We are not implementing upgrade for now...
        Log.d(Constants.TAG, "Upgrade is deleting old data...");
        db.execSQL("DROP TABLE " + HistoryTable.TABLE);
        onCreate(db);
    }

}
