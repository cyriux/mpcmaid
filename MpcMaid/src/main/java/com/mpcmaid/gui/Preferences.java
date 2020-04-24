package com.mpcmaid.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.mpcmaid.pgm.Profile;

/**
 * Application-scope settings
 * 
 * @pattern Singleton
 * 
 * @author cyrille martraire
 */
public final class Preferences {

	private static final String MPCMAID_SETTINGS_DIR = "mpcmaid";

	private final static Preferences INSTANCE = new Preferences();

	public final static Preferences getInstance() {
		return INSTANCE;
	}

	private Profile profile = Profile.MPC500;

	private String openPath = System.getProperty("user.home");

	private String savePath = System.getProperty("user.home");

	private int auditionSamples = 1;

	public String toString() {
		return "Preferences";
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
		save();
	}

	public void toggleProfile() {
		final Profile newProfile = (profile == Profile.MPC500) ? Profile.MPC1000 : Profile.MPC500;
		setProfile(newProfile);
	}

	public String getOpenPath() {
		return openPath;
	}

	public void setOpenPath(String openPath) {
		this.openPath = openPath;
		save();
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
		save();
	}

	public int getAuditionSamples() {
		return auditionSamples;
	}

	public void setAuditionSamples(int auditionSamples) {
		this.auditionSamples = auditionSamples;
		save();
	}

	public static final boolean isMacOsX() {
		return System.getProperty("mrj.version") != null;
	}

	private static File getUserPreferencesDirectory() {
		if (isMacOsX()) {
			return new File(System.getProperty("user.home"), "Library/Preferences");
		}
		return new File(System.getProperty("user.home"));
	}

	private final static File getSettingsDirectory() {
		final File home = getUserPreferencesDirectory();
		final File settingsDirectory = new File(home, MPCMAID_SETTINGS_DIR);
		if (!settingsDirectory.exists()) {
			if (!settingsDirectory.mkdir()) {
				throw new IllegalStateException("Could not create settings directory!\n" + settingsDirectory.toString());
			}
		}
		return settingsDirectory;
	}

	private final static Properties getProperties() {
		Properties properties = new Properties();
		final File home = getSettingsDirectory();
		final File file = new File(home, "mpcmaid.properties");
		try {
			file.createNewFile();
			properties.load(new FileInputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return properties;
	}

	public final void load() {
		final Properties properties = getProperties();
		if (properties == null) {
			return;
		}
		final String home = System.getProperty("user.home");
		this.profile = Profile.getProfile(properties.getProperty("profile", Profile.MPC1000.getName()));
		this.openPath = properties.getProperty("lastOpenPath", home);
		this.savePath = properties.getProperty("lastSavePath", home);
		this.auditionSamples = Integer.parseInt(properties.getProperty("auditionSamples", "1"));
	}

	public final void save() {
		try {
			final Properties properties = getProperties();
			if (properties == null) {
				return;
			}
			properties.setProperty("profile", profile.getName());
			properties.setProperty("lastOpenPath", openPath);
			properties.setProperty("lastSavePath", savePath);
			properties.setProperty("auditionSamples", String.valueOf(auditionSamples));

			final File home = getSettingsDirectory();
			final File file = new File(home, "mpcmaid.properties");
			properties.store(new FileOutputStream(file), null);
		} catch (Exception ignore) {
			ignore.printStackTrace();
		}
	}
}
