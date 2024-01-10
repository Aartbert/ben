package nl.han.interfaces;

import nl.han.enums.ProfileType;

public interface IProfile {

    /**
     * Loads a profile from a text file.
     *
     * @param profile the profile to load
     * @return the profile as a string
     */
    String loadProfile(ProfileType profile);

}