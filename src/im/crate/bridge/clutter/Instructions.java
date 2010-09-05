package im.crate.bridge.clutter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Instructions extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	Button back; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.instructions);
	    
	  back = (Button)findViewById(R.id.back);
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