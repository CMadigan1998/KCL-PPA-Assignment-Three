import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a jaguar. Jaguars age, hunger, move, eat prey, and die.
 * 
 * @author David J. Barnes, Michael Kölling, Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public class Jaguar extends Animal
{
    // Characteristics shared by all jaguars (class variables):
    // The maximum age to which a jaguar can live.
    private static final int MAX_AGE = 50;
    // The number of steps a jaguar can go without eating before it dies.
    private static final int MAX_FOOD = 15;
    // What types of animals a jaguar eats.
    private static final ArrayList<Class> PREY = new ArrayList<>();
    // The age at which a jaguar can start to breed.
    private static final int BREEDING_AGE = 5;
    // How many steps before a jaguar can give birth again.
    private static final int BIRTH_COOLDOWN = 5;
    // The maximum range a jaguar will look in for breeding.
    private static final int BREEDING_RANGE = 10;
    // The likelihood of a jaguar breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of offspring a jaguar can give birth to in a single event.
    private static final int MAX_BIRTH_SIZE = 2;

    /**
     * Create a jaguar. A jaguar can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param  randomStart  Whether the animal starts 'newborn' or with random stats.
     * @param  field  The field currently occupied.
     * @param  location  The location within the field.
     */
    public Jaguar(boolean randomStart, Field field, Location location)
    {
        super(randomStart, field, location);
        PREY.add(Monkey.class);
        PREY.add(Gazelle.class);
    }

    /**
     * This is what the jaguar does most of the time: it hunts for
     * prey. In the process, it might breed, die of hunger,
     * or die of old age.
     * 
     * @param newJaguars A list to return newly born jaguars.
     * @param  dayTime  Whether it is day-time.
     */
    public void act(List<Organism> newJaguars, boolean dayTime)
    {
        if (dayTime) {
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
                    giveBirth(newJaguars); 
                }
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
        return new Jaguar(randomStart, field, loc);
    }
    
    /**
     * @return  The maximum age to which a jaguar can live.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * @return  The number of steps a jaguar can go without eating before it dies.
     */
    public int getMaxFood()
    {
        return MAX_FOOD;
    }
    
    /**
     * @return  What types of animals a jaguar eats.
     */
    public ArrayList<Class> getPrey()
    {
        return PREY;
    }
    
    /**
     * @return  The age at which a jaguar can start to breed.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * @return  How many steps before a jaguar can give birth again.
     */
    public int getBirthCooldown()
    {
        return BIRTH_COOLDOWN;
    }
    
    /**
     * @return  The maximum range a jaguar will look in for breeding.
     */
    public int getBreedingRange()
    {
        return BREEDING_RANGE;
    }
    
    /**
     * @return  The likelihood of a jaguar breeding.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * @return  The maximum number of offspring a jaguar can give birth to in a single event.
     */
    public int getMaxBirthSize()
    {
        return MAX_BIRTH_SIZE;
    }
}
