package nl.han.shared.datastructures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerAttributes {
    private int hp;
    private int maxHp;
    private int x;
    private int y;
    private int z;
    private int stamina;
    private int power;
    private String currentTile;
    private List<String> items;

    public PlayerAttributes(int hp, int maxHp, int x, int y, int z){
        this.hp = hp;
        this.maxHp = maxHp;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public String toString() {
        return "HP: " + hp + "/" + maxHp +
                "\nStamina: " + stamina +
                "\nPower: " + power +
                "\nx: " + x + ", y: " + y + ", z: " + z +
                "\nCurrent position: " + currentTile +
                "\n--------------------------------------";
    }

    /**
     * Returns the inventory of the player.
     *
     * @return the inventory of the player
     *
     * @author Jasper Kooy
     */
    public String getInventory() {
        return items.toString();
    }
}
