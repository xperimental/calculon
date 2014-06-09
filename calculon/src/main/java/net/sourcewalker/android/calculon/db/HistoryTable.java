package net.sourcewalker.android.calculon.db;

import android.provider.BaseColumns;

public class HistoryTable implements BaseColumns {

    public static final String TABLE = "history";

    public static final String TIMESTAMP = "timestamp";
    public static final String OPERATION = "operation";
    public static final String OPERAND_ONE = "operandOne";
    public static final String OPERAND_TWO = "operandTwo";
    public static final String RESULT = "result";

    public static final String[] DEFAULT_PROJECTION = new String[]{
            _ID,
            TIMESTAMP,
            OPERATION,
            OPERAND_ONE,
            OPERAND_TWO,
            RESULT
    };

    public static final String DEFAULT_SORT = TIMESTAMP + " DESC, " + _ID + " DESC";

    public static final String SCHEMA = "CREATE TABLE " + TABLE + " (" +
            _ID + " INTEGER PRIMARY KEY, " +
            TIMESTAMP + " INTEGER NOT NULL, " +
            OPERATION + " INTEGER NOT NULL, " +
            OPERAND_ONE + " INTEGER NOT NULL, " +
            OPERAND_TWO + " INTEGER NOT NULL, " +
            RESULT + " INTEGER NOT NULL)";

}
