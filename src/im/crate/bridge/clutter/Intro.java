package im.crate.bridge.clutter;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Intro extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	Button start;
	ImageView logo;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.intro);
	    
	  start = (Button)findViewById(R.id.start);
	  start.setOnClickListener(this);
	  
//	  logo = (ImageView)findViewbyId(R.id.image);
//	  Bitmap bitmap = BitmapFactory.decodeFile("temp" + cnt + ".jpg");
//	  imageView.setImageBitmap(bitmap);

	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(start)){
			Intent intent = new Intent();
			intent.setClass(this, Clutter.class);
			startActivity(intent);
		}
	}
	
}