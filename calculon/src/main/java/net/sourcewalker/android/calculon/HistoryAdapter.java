package net.sourcewalker.android.calculon;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HistoryAdapter extends CursorAdapter {

    private final Context context;

    public HistoryAdapter(Context context) {
        super(context, null, 0);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.history_list_item, parent, false);
        HistoryHolder holder = new HistoryHolder();
        holder.operandOne = (TextView) rootView.findViewById(R.id.operand_one);
        holder.operandTwo = (TextView) rootView.findViewById(R.id.operand_two);
        holder.operation = (TextView) rootView.findViewById(R.id.operation);
        holder.result = (TextView) rootView.findViewById(R.id.result);
        rootView.setTag(holder);
        return rootView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        HistoryHolder holder = (HistoryHolder) view.getTag();
        // See indexes in HistoryTable#DEFAULT_PROJECTION
        holder.operation.setText(getOperationString(cursor.getInt(2)));
        holder.operandOne.setText(Integer.toString(cursor.getInt(3)));
        holder.operandTwo.setText(Integer.toString(cursor.getInt(4)));
        holder.result.setText(Integer.toString(cursor.getInt(5)));
    }

    private String getOperationString(int operation) {
        switch (operation) {
            case Constants.OPERATOR_PLUS:
                return context.getString(R.string.calculator_button_plus);
            case Constants.OPERATOR_MINUS:
                return context.getString(R.string.calculator_button_minus);
            case Constants.OPERATOR_MULTIPLY:
                return context.getString(R.string.calculator_button_mult);
            case Constants.OPERATOR_DIVIDE:
                return context.getString(R.string.calculator_button_div);
            default:
                return context.getString(R.string.unknown_operation);
        }
    }

    private static final class HistoryHolder {
        public TextView operandOne;
        public TextView operandTwo;
        public TextView operation;
        public TextView result;
    }

}
