package nl.han.shared.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static nl.han.CryptsOfChaos.seededRandom;

/**
 * The Lottery class is used to draw a random object from a list of objects. The chance of drawing a specific object
 * can be set by adding the object multiple times to the list. The more times an object is added, the higher the
 * chance of drawing that object.
 * @param <T> The type of object to draw from the list of objects.
 * @author Djurre Tieman
 */
public class Lottery<T> {

    private final List<T> lotteryList;

    /**
     * Constructor for the Lottery class. This constructor will create a list of objects based on the given map.
     * The map will contain the number of objects equal to their chance value. This will be stored to be able to
     * draw from the same list multiple times.
     *
     * @param map The map of objects and their chances.
     * @author Djurre Tieman
     */
    public Lottery(Map<T, Integer> map) {
        List<T> lotteryItems = new ArrayList<>();
        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                lotteryItems.add(entry.getKey());
            }
        }
        this.lotteryList = lotteryItems;
    }

    /**
     * Draws a random object from the list of objects. Uses the seeded random to make sure the same object is drawn
     * every time the same seed is used.
     * @return The drawn object.
     * @author Djurre Tieman
     */
    public T drawRandom() {
        return lotteryList.get(seededRandom.nextInt(lotteryList.size()));
    }

    /**
     * Draws a specific object from the list of objects.
     * @param number The number of the object to draw. This number is the index of the object in the list.
     * @return The drawn object.
     * @author Djurre Tieman
     */
    public T draw(int number) {
        return lotteryList.get(number);
    }
}