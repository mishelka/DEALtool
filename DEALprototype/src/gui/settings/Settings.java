package gui.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Settings {
	private static final String SETTING_FILE = System.getProperty("user.home") 
			+ File.separator + "deal.settings";

	
	public void save(Setting setting) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SETTING_FILE));
			out.writeObject(setting);
			out.close();
		} catch (FileNotFoundException e) {
			//do nothing
		} catch (IOException e) {
			//do nothing
		}
	}
	
	public Setting load() {
		Setting s = new Setting(true);
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(SETTING_FILE));
			s = (Setting) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			//do nothing, return default settings
		} catch (IOException e) {
			//do nothing, return default settings
		} catch (ClassNotFoundException e) {
			//do nothing, return default settings
		}
		return s;
	}
}
