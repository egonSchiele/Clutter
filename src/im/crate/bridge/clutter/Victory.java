package im.crate.bridge.clutter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class Victory extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	Button restart; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.victory);
	    
	  restart = (Button)findViewById(R.id.restart);
	  restart.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(restart)){
			Intent intent = new Intent();
			intent.setClass(this, Clutter.class);
			startActivity(intent);
		}
	}
	
}