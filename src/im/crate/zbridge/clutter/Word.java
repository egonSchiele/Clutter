package im.crate.zbridge.clutter;

import com.stickycoding.rokon.Font;
import com.stickycoding.rokon.TextSprite;

public class Word {
	public String name;
	public Word match;
	public TextSprite sprite;
	
	public Word(String name, Font uifont)
	{
		this.name = name;
		sprite = new TextSprite(0, 0, 12, 12);
		sprite.setTexture(uifont.createTexture(name));
		sprite.setText(name);
	}
}
