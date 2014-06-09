package net.sourcewalker.android.calculon.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class CalculatorProvider extends ContentProvider {

    private static final String AUTHORITY = "net.sourcewalker.android.calculon";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri HISTORY_URI = Uri.withAppendedPath(CONTENT_URI, "history");

    private static final Map<String, String> PROJECTION_MAP;
    private static final UriMatcher MATCHER;
    private static final int MATCH_HISTORY_LIST = 1;
    private static final int MATCH_HISTORY_ITEM = 2;

    static {
        // Project column names directly onto themselves
        PROJECTION_MAP = new HashMap<String, String>();
        for (String column : HistoryTable.DEFAULT_PROJECTION) {
            PROJECTION_MAP.put(column, column);
        }

        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(AUTHORITY, "history", MATCH_HISTORY_LIST);
        MATCHER.addURI(AUTHORITY, "history/#", MATCH_HISTORY_ITEM);
    }

    private CalculatorDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new CalculatorDbHelper(getContext());
        return true;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (MATCHER.match(uri)) {
            case MATCH_HISTORY_ITEM:
                selection = HistoryTable._ID + " = ?";
                selectionArgs = new String[] { uri.getLastPathSegment() };
            case MATCH_HISTORY_LIST:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int count = db.update(HistoryTable.TABLE, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (MATCHER.match(uri)) {
            case MATCH_HISTORY_ITEM:
                selection = HistoryTable._ID + " = ?";
                selectionArgs = new String[] { uri.getLastPathSegment() };
            case MATCH_HISTORY_LIST:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int count = db.delete(HistoryTable.TABLE, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (MATCHER.match(uri)) {
            case MATCH_HISTORY_LIST:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (values.containsKey(HistoryTable._ID)) {
                    values.remove(HistoryTable._ID);
                }
                values.put(HistoryTable.TIMESTAMP, System.currentTimeMillis());
                long id = db.insert(HistoryTable.TABLE, HistoryTable.TIMESTAMP, values);
                Uri itemUri = ContentUris.withAppendedId(HISTORY_URI, id);
                getContext().getContentResolver().notifyChange(itemUri, null);
                return itemUri;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case MATCH_HISTORY_ITEM:
                return "vnd.android.cursor.dir/net.sourcewalker.android.calculon.history";
            case MATCH_HISTORY_LIST:
                return "vnd.android.cursor.item/net.sourcewalker.android.calculon.history";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        switch (MATCHER.match(uri)) {
            case MATCH_HISTORY_ITEM:
                query.appendWhere(HistoryTable._ID + " = " + uri.getLastPathSegment());
            case MATCH_HISTORY_LIST:
                query.setTables(HistoryTable.TABLE);
                query.setProjectionMap(PROJECTION_MAP);
                if (projection == null) {
                    projection = HistoryTable.DEFAULT_PROJECTION;
                }
                if (sortOrder == null) {
                    sortOrder = HistoryTable.DEFAULT_SORT;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = query.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

}
