package net.sourcewalker.android.calculon;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import net.sourcewalker.android.calculon.db.CalculatorProvider;
import net.sourcewalker.android.calculon.db.HistoryTable;

public class AppendHistoryTask extends AsyncTask<Integer, Void, Void> {

    private final Context context;

    public AppendHistoryTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.OPERAND_ONE, params[0]);
        values.put(HistoryTable.OPERAND_TWO, params[1]);
        values.put(HistoryTable.OPERATION, params[2]);
        values.put(HistoryTable.RESULT, params[3]);
        context.getContentResolver().insert(CalculatorProvider.HISTORY_URI, values);
        return null;
    }

}
