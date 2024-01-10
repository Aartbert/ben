package nl.han;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nl.han.enums.ProfileType;

/**
 * Testing class for ProfileService.
 * @see <a href="https://confluenceasd.aimsites.nl/x/zhjjGQ">Testrapport</a>
 */
class ProfileServiceTest {

    private ProfileService profileService;

    @BeforeEach
    void setup() {
        profileService = new ProfileService();
    }

    /**
     * Test code ACS01
     */
    @Test
    @DisplayName("test if loadProfile loads the correct file for NEUTRAL profile")
    void testProfileLoaderForNeutralProfile() {
        // Arrange
        String expected = """
                Als vijand binnen 15 tegels is en hp is groter dan 50%, dan val speler aan.
                Als vijand binnen 15 tegels is en hp kleiner dan 50%, dan ren weg.
                Als health potion binnen 15 tegels is en hp kleiner is dan 50%, dan pak health potion.
                Als item binnen 25 tegels is, dan pak item op.
                Als hp onder 50% is, dan gebruik health potion.
                Als hp boven 50% is en stamina boven 20%, dan loop rond.
                Als stamina onder 20% is en health onder 70%, dan gebruik health potion.""";

        expected = expected.replaceAll("\\R", System.lineSeparator());

         // Act
        String actual = profileService.loadProfile(ProfileType.NEUTRAL);
        actual = actual.replaceAll("\\R", System.lineSeparator());

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code ACS02
     */
    @Test
    @DisplayName("test if loadProfile loads the correct file for OFFENSIVE profile")
    void testProfileLoaderForOffensiveProfile() {
        // Arrange
        String expected = """
                Als vijand binnen 25 tegels is en hp is groter dan 35%, dan val vijand aan.
                Als vijand binnen 25 tegels is en hp is kleiner dan 35%, dan ren weg.
                Als hp onder 15% is, dan drink health potion.
                Als hp kleiner is dan 25% en health potion binnen 25 tegels is, dan pak health potion op.
                Als stamina boven 25% is en als hp onder 35% is, dan pak health potion op.
                Als stamina boven 25% is en item binnen 25 tegels is, dan pak item op.
                Als stamina boven 25% is, dan loop rond.
                Als stamina onder 25% is en hp onder 35% is, dan drink health potion.""";

        expected = expected.replaceAll("\\R", System.lineSeparator());

        // Act
        String actual = profileService.loadProfile(ProfileType.OFFENSIVE);
        actual = actual.replaceAll("\\R", System.lineSeparator());

        // Assert
        assertEquals(expected, actual);
    }

    /**
     * Test code ACS03
     */
    @Test
    @DisplayName("test if loadProfile loads the correct file for DEFENSIVE profile")
    void testProfileLoaderForDefensiveProfile() {
        // Arrange
        String expected = """
                Als vijand binnen 20 tegels is en hp is groter dan 70%, dan val monster aan.
                Als vijand binnen 20 tegels is en hp is kleiner dan 70%, dan ren weg.
                Als hp minder is dan 70% en health potion binnen 15 tegels is, dan pak health potion op.
                Als hp onder 70% is, dan gebruik health potion.
                Als item binnen 15 tegels is, dan pak item op.
                Als stamina boven 35% is, dan loop rond.
                Als stamina onder 35% is en hp minder is dan 90%, dan gebruik health potion
                Als stamina onder 35% is en als item binnen 5 tegels is, dan pak item op.""";

        expected = expected.replaceAll("\\R", System.lineSeparator());

        // Act
        String actual = profileService.loadProfile(ProfileType.DEFENSIVE);
        actual = actual.replaceAll("\\R", System.lineSeparator());

        // Assert
        assertEquals(expected, actual);
    }
}
