import java.util.Random;
import java.util.List;
/**
 * A class representing the characteristics of a plant. Plants can age, spread and die.
 *
 * @author Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public class Plant extends Organism
{
    // The age at which a plant can start to spread.
    private static final int SPREADING_AGE = 6;
    // The maximum age to which a plant can live.
    private static final int MAX_AGE = 11;
    // The range in which a plant can spread
    private static final int SPREAD_RANGE = 15;
    // Maximum new plants a plant can produce when spreading. May produce less.
    private static final int MAX_SEEDS = 2;

    /**
     * Create a new plant at location in field. A plant can be created as a newborn 
     * (age zero) or with a random age.
     * 
     * @param  randomStart  Whether the plant starts 'newborn' or with random stats.
     * @param  field  The field currently occupied.
     * @param  location  The location within the field.
     */
    public Plant(boolean randomStart, Field field, Location location)
    {
        super(randomStart, field, location);
    }

    /**
     * @return  The maximum age to which a plant can live.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Make this plant act - that is: make it do whatever it wants/needs to do.
     * 
     * @param  newPlants  A list to receive newly grown plants.
     * @param  dayTime  Whether it is day-time.
     */
    public void act(List<Organism> newPlants, boolean dayTime)
    {
        incrementAge();
        if (!alive) {
            return;
        }
        if (age < SPREADING_AGE){
            return;
        }
        
        // Determine seed count. Seed count is increased if it is raining.
        int seedCount;
        if (field.getWeatherSystem().isRaining()) {
            seedCount = MAX_SEEDS + 1;
        }
        else {
            seedCount = MAX_SEEDS;
        }
        
        // Spreading procedure
        for (int i = seedCount; i > 0; i--) {
            // Get random offsets to add to the plants posistion. Offsets are such that when added to the plants posistion,
            // all the possible locations are within the spreading range of the plant (and so offsets may be negative).
            int yOffset = rand.nextInt((2 * SPREAD_RANGE) + 1) - SPREAD_RANGE;
            int xOffset = rand.nextInt((2 * SPREAD_RANGE) + 1) - SPREAD_RANGE;
            // Get the coordinates of the random location by adding the offsets to the plants posistion.
            int y = location.getRow() + yOffset; 
            int x = location.getCol() + xOffset;            

            // Check to see if location is outside field. If so, skip location.
            if (field.outOfBounds(y, x)){
                continue;
            }
            
            // If location empty, place new plant.
            if (field.getObjectAt(y, x) == null) {
                Location location = new Location(y, x);
                Plant plant = new Plant(false, field, location);
                field.place(plant, location);
                newPlants.add(plant);
            }
        }
    }
}
