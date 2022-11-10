import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a frog. Frogs age, hunger, move, breed, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public class Frog extends Animal
{
    // Characteristics shared by all frogs (class variables):
    // The maximum age to which a frog can live.
    private static final int MAX_AGE = 30;
    // The number of steps a frog can go without eating before it dies.
    private static final int MAX_FOOD = 10;
    // What types of plants a frog eats.
    private static final ArrayList<Class> PREY = new ArrayList<>();
    // The age at which a frog can start to breed.
    private static final int BREEDING_AGE = 5;
    // How many steps before a frog can give birth again.
    private static final int BIRTH_COOLDOWN = 20;
    // The maximum range a frog will look in for breeding.
    private static final int BREEDING_RANGE = 10;
    // The likelihood of a frog breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of offspring a frog can give birth to in a single event.
    private static final int MAX_BIRTH_SIZE = 3;

    /**
     * Create a new frog. A frog may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param  randomStart  Whether the animal starts 'newborn' or with random stats.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Frog(boolean randomStart, Field field, Location location)
    {
        super(randomStart, field, location);
        PREY.add(Plant.class);
    }

    /**
     * This is what the frog does most of the time - it runs 
     * around eating plants. Sometimes it will breed or die of old age.
     * 
     * @param newFrogs A list to return newly born frogs.
     * @param  dayTime  Whether it is day-time.
     */
    public void act(List<Organism> newFrogs, boolean dayTime)
    {
        incrementHunger();
        incrementAge();
        if(isAlive()) {       
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = field.freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }

            if (isAlive()) {
                birthCoolDown++;
                giveBirth(newFrogs); 
            }
            
            diseaseAction();
        }
    }
    
    /**
     * Returns a new animal of the subclass type. An animal can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param  randomStart  Whether the animal starts 'newborn' or with random stats.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal getNewObject(boolean randomStart, Field field, Location loc)
    {
        return new Frog(randomStart, field, loc);
    }
    
    /**
     * @return  The maximum age to which a frog can live.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * @return  The number of steps a frog can go without eating before it dies.
     */
    public int getMaxFood()
    {
        return MAX_FOOD;
    }
    
    /**
     * @return  What types of plants a frog eats.
     */
    public ArrayList<Class> getPrey()
    {
        return PREY;
    }
    
    /**
     * @return  The age at which a frog can start to breed.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * @return  How many steps before a frog can give birth again.
     */
    public int getBirthCooldown()
    {
        return BIRTH_COOLDOWN;
    }
    
    /**
     * @return  The maximum range a frog will look in for breeding.
     */
    public int getBreedingRange()
    {
        return BREEDING_RANGE;
    }
    
    /**
     * @return  The likelihood of a frog breeding.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * @return  The maximum number of offspring a frog can give birth to in a single event.
     */
    public int getMaxBirthSize()
    {
        return MAX_BIRTH_SIZE;
    }
}
