package elbadev.com.wordswords;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by romme86 on 11/12/2017.
 */

public class TableClassificaEntry extends TableLayout{

    public TableClassificaEntry(Context context) {
        super(context);
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));// assuming the parent view is a LinearLayout

        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(tableParams);// TableLayout is the parent view

        TextView textView = new TextView(context);
        textView.setText("ehy man");
        textView.setLayoutParams(rowParams);// TableRow is the parent view

        tableRow.addView(textView);

    }

    public TableClassificaEntry(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
