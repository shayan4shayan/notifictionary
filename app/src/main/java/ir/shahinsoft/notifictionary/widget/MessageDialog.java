package ir.shahinsoft.notifictionary.widget;

import android.app.AlertDialog;
import android.content.Context;

import ir.shahinsoft.notifictionary.R;


/**
 * Created by shayan on 9/29/2017.
 */

public class MessageDialog extends AlertDialog {
    public MessageDialog(Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dialog_message);
    }
}
