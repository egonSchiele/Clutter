package im.crate.bridge.clutter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class Intro extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	Button start, instructions;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
	  setContentView(R.layout.intro);

	  start = (Button)findViewById(R.id.start);
	  start.setOnClickListener(this);
	  
	  instructions = (Button)findViewById(R.id.instructions);
	  instructions.setOnClickListener(this);
	 
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(start)){
			Intent intent = new Intent();
			intent.setClass(this, Clutter.class);
			startActivity(intent);
		}else if (v.equals(instructions)){
			Intent intent = new Intent();
			intent.setClass(this, Instructions.class);
			startActivity(intent);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.settings, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    if(item.getItemId() == R.id.settings)
	    {
	        startActivity(new Intent(this, Prefs.class));
	        return true;
	    }
	    return false;
	}
}