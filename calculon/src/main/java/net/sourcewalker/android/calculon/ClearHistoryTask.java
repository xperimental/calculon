package net.sourcewalker.android.calculon;

import android.content.Context;
import android.os.AsyncTask;

import net.sourcewalker.android.calculon.db.CalculatorProvider;

public class ClearHistoryTask extends AsyncTask<Void, Void, Void> {

    private final Context context;

    public ClearHistoryTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        context.getContentResolver().delete(CalculatorProvider.HISTORY_URI, null, null);
        return null;
    }

}
