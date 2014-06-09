package net.sourcewalker.android.calculon;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import net.sourcewalker.android.calculon.db.CalculatorProvider;

public class HistoryListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private HistoryAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listAdapter = new HistoryAdapter(getActivity());
        setListAdapter(listAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), CalculatorProvider.HISTORY_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        listAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // clear data from adapter
        listAdapter.swapCursor(null);
    }

}
