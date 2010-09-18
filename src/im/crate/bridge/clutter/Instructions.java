package im.crate.bridge.clutter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class Instructions extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	ImageButton back; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
	  setContentView(R.layout.instructions);
	    
	  back = (ImageButton)findViewById(R.id.back);
	  back.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		if (v.equals(back)){
			Intent intent = new Intent();
			intent.setClass(this, Intro.class);
			startActivity(intent);
		}
	}
	
}