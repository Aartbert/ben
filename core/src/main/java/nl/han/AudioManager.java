package nl.han;

import lombok.Setter;
import nl.han.shared.datastructures.creature.Bot;
import nl.han.shared.datastructures.creature.Creature;
import nl.han.shared.datastructures.creature.Player;
import nl.han.shared.datastructures.game.Game;
import nl.han.shared.datastructures.world.Coordinate;
import nl.han.shared.enums.BotType;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.InputStream;
import java.util.Random;

import static nl.han.shared.datastructures.world.Chunk.CHUNK_HEIGHT;
import static nl.han.shared.datastructures.world.Chunk.CHUNK_WIDTH;

public class AudioManager {
    @Setter
    private Game game;
    private Random random;
    @Setter
    private Player currentPlayer;

    public AudioManager() {
        random = new Random();
    }

    public void updateAudio(long tickCount) {
        game.getCreatures().stream()
                .filter(creature -> creature.getChunk().equals(currentPlayer.getChunk()) && !creature.equals(currentPlayer))
                .forEach(creature -> {
                    updateAudioPosition(creature);
                    if (creature instanceof Bot monster) attemptAudioPlayback(monster, tickCount);
                });
    }

    public void playAudioAsCreature(Creature creature, AudioInputStream audio, long tickCount) {
        try {
            creature.setLastAudioPlayback(tickCount);
            creature.setActiveAudioPlayback(true);
            Clip clip = creature.getClip();

            clip.open(audio);
            updateAudioPosition(creature);
            clip.start();
        } catch (Exception e) {
            //TODO
        }
    }

    private void attemptAudioPlayback(Bot monster, long tickCount) {
        try {
            BotType botType = monster.getBotType();
            if (random.nextInt(101) <= botType.getAudioChance() && monster.getLastAudioPlayback() <= tickCount - botType.getAudioCooldown()) {
                InputStream inputStream = AudioManager.class.getClassLoader().getResourceAsStream(botType.getAudioPath());
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                playAudioAsCreature(monster, audioInputStream, tickCount);
            }
        } catch (Exception e) {
            //TODO
        }
    }

    private void updateAudioPosition(Creature creature) {
        Clip clip = creature.getClip();
        if (clip.isOpen()) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(calculateVolume(creature));

            FloatControl panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
            System.out.println(calculatePanning(creature));
            panControl.setValue(calculatePanning(creature));
        }
    }

    private float calculateVolume(Creature creature) {
        double distance = currentPlayer.getCoordinate().calculateDistance(creature.getCoordinate());
        double minDistance = 0;
        double maxDistance = new Coordinate(0, 0).calculateDistance(new Coordinate(CHUNK_WIDTH - 1, CHUNK_HEIGHT - 1));
        float volume = (float) mapValue(distance, minDistance, maxDistance, 5, -30);
        return volume;
    }

    private float calculatePanning(Creature creature) {
        int relativeX = creature.getCoordinate().x() - currentPlayer.getCoordinate().x();
        int relativeY = creature.getCoordinate().y() - currentPlayer.getCoordinate().y();

        double angleFromXAxis = Math.atan2(relativeY, relativeX);
        double angleDegrees = Math.toDegrees(angleFromXAxis);

        double normalizedAngle = (angleDegrees + 360) % 360;
        if (normalizedAngle >= 180) normalizedAngle -= 180;
        if (normalizedAngle > 90) normalizedAngle = 180 - normalizedAngle;

        double panning = mapValue(normalizedAngle, 0, 90, 1, 0);
        if (relativeX < 0) return (float) -panning;
        if (relativeX > 0) return (float) panning;
        else return 0;
    }


    public static double mapValue(double value, double originalMin, double originalMax, double newMin, double newMax) {
        if (value < originalMin || value > originalMax) throw new IllegalArgumentException("Value is outside the provided range.");

        double normalizedValue = (value - originalMin) / (originalMax - originalMin);
        double mappedValue = (normalizedValue * (newMax - newMin)) + newMin;

        return mappedValue;
    }
}