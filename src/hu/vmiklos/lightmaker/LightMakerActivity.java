package hu.vmiklos.lightmaker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class LightMakerActivity extends Activity {
	@Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // brightness
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1;

        TextView tv = new TextView(this);
        // argb white
        tv.setBackgroundColor(0xffffffff);
        setContentView(tv);
    }
}
