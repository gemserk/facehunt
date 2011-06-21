package com.gemserk.games.facehunt.gamestates;

import java.util.Set;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.gemserk.datastore.profiles.Profile;
import com.gemserk.datastore.profiles.ProfileJsonSerializer;

/**
 * Abstracts Profiles persistence and retrieval
 */
public class GamePreferences {

	private final ProfileJsonSerializer profileJsonSerializer = new ProfileJsonSerializer();

	private final Preferences preferences;

	public GamePreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	/**
	 * Updates the current profile with values of specified profile, it also updates saved profile list.
	 */
	public void updateProfile(Profile profile) {
		String profilesListJson = preferences.getString("profiles", "[]");
		Set<Profile> profileList = profileJsonSerializer.parseList(profilesListJson);
		profileList.remove(profile);
		profileList.add(profile);
		preferences.putString("profiles", profileJsonSerializer.serialize(profileList));
		preferences.putString("profile", profileJsonSerializer.serialize(profile));
		preferences.flush();
	}

	/**
	 * Returns current profile, if it doesn't exists then returns a new guest profile.
	 */
	public Profile getCurrentProfile() {
		String profileJson = preferences.getString("profile", "");

		if (profileJson != null && !"".equals(profileJson))
			return profileJsonSerializer.parse(profileJson);

		Profile profile = new Profile("guest-" + MathUtils.random(10000, 99999), true);

		preferences.putString("profile", profileJsonSerializer.serialize(profile));
		preferences.flush();

		return profile;
	}
	
	/**
	 * Returns locally saved profiles.
	 */
	public Set<Profile> getSavedProfiles() {
		String profilesListJson = preferences.getString("profiles", "[]");
		return profileJsonSerializer.parseList(profilesListJson);
	}

}