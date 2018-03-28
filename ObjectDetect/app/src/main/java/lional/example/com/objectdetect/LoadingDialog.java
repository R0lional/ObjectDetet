package lional.example.com.objectdetect;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by skysoft on 2018/3/6.
 */

public class LoadingDialog extends ProgressDialog {
    private TextView textView;

    public LoadingDialog(Context context) {
        super(context);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setIndeterminate(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_dialog);

        textView = findViewById(R.id.load_process_text);
    }

    public void setDialogMessage(String message) {
        String info = textView.getText().toString();
        message = info + "\n" + message;
        textView.setText(message);
    }
}
