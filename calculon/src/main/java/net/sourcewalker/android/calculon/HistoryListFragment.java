package net.sourcewalker.android.calculon;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.sourcewalker.android.calculon.db.CalculatorProvider;

public class HistoryListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private HistoryAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        listAdapter = new HistoryAdapter(getActivity());
        setListAdapter(listAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_clear) {
            ClearHistoryTask task = new ClearHistoryTask(getActivity());
            task.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
