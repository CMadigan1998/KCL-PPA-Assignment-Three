import java.util.Random;
import java.util.List;

/**
 * A class representing shared characteristics of a organism. Organisms age and die.
 *
 * @author Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public abstract class Organism
{
    // Whether the organism is alive or not.
    protected boolean alive;
    // The organism's age.
    protected int age;
    // The field the organism is located in.
    protected Field field;
    // The organism's location.
    protected Location location;
    // A random number generator.
    protected static final Random rand = Randomizer.getRandom();

    /**
     * Create a new organism at location in field. An organism can be created as a newborn 
     * (age zero) or with a random age.
     * 
     * @param  randomStart  Whether the organism starts 'newborn' or with random stats.
     * @param  field  The field currently occupied.
     * @param  location  The location within the field.
     */
    public Organism(boolean randomStart, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        if (randomStart) {
            age = rand.nextInt(this.getMaxAge());
        }
        else {
            age = 0;        
        }
    }
    
    /**
     * Make this organism act - that is: make it do whatever it wants/needs to do.
     * 
     * @param  newOrganisms  A list to receive newly born organisms.
     * @param  dayTime  Whether it is day-time.
     */
    abstract public void act(List<Organism> newOrganisms, boolean dayTime);
    
    /**
     * @return  The organism's location.
     */
    public Location getLocation()
    {
        return location;
    }    
    
    /**
     * Place the organism at the new location in the given field.
     * @param  newLocation  The organism's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Increments the organism's age. This may kill the organism
     */
    protected void incrementAge()
    {
        age++;
        if(age > this.getMaxAge()) {
            setDead();
            //System.out.println("Killed by age");
        }   
    }
    
    /**
     * @return  The maximum age of the organism.
     */
    abstract protected int getMaxAge();
    
    /**
     * @return  True if the organism is alive, false otherwise.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
}
