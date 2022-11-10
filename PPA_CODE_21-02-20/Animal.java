
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals. Animals have a gender and can age, hunger, move, eat prey and die.
 * 
 * @author David J. Barnes, Michael Kölling, Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public abstract class Animal extends Organism
{
    // The animal's sex.
    protected boolean male;
    // The animals's food level, which is increased by eating animals or plants.
    protected int foodLevel;
    // How many steps before an animal can give birth again.
    protected int birthCoolDown;
    // A disease infecting the animal. Maybe be null (no disease).
    protected Disease disease;

    /**
     * Create a new animal at location in field. An animal can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param  randomStart  Whether the animal starts 'newborn' or with random stats.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomStart, Field field, Location location)
    {
        super(randomStart, field, location);
        // The animal starts off being able to breed (unless age too low).
        birthCoolDown = this.getBirthCooldown();

        // Set the food level.
        if (randomStart) {
            // Animals start with at least 1 point in their food level.
            foodLevel = rand.nextInt(this.getMaxFood()) + 1;
        }
        else {
            foodLevel = this.getMaxFood();
        }

        // Set the gender.
        if (rand.nextInt(2) == 0) {
            male = true;
        } 
        else {
            male = false;
        }
    }

    /**
     * Make this animal act - that is: make it do whatever it wants/needs to do.
     * 
     * @param  newAnimals  A list to receive newly born animals.
     * @param  dayTime  Whether it is day-time.
     */
    abstract public void act(List<Organism> newAnimals, boolean dayTime);

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param  newAnimals  A list to return newly born animals.
     */
    protected void giveBirth(List<Organism> newAnimals)
    {
        // If the weather is foggy, the breeding range is decreased.        
        int range = this.getBreedingRange();
        if (field.getWeatherSystem().isFog()) {
            range--;
        }
        
        // Look for a partner
        Animal partner = field.findPartner(this, range);
        if(partner == null) {
            return;
        }

        // If female and recently given birth, cannot give birth again currently.
        if (!isMale() && birthCoolDown < this.getBirthCooldown()) {
            return;
        }

        // Breeding
        birthCoolDown = 0;
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newAnimals.add(this.getNewObject(false, field, loc));
        }
        
        // Check if the animals involved in breeding have and spread a disease.
        if ((this.disease != null) || (partner.disease != null)) {
            if (Disease.attemptSpread()) {
                if (this.disease == null) {
                    disease = new Disease();
                }
                if (partner.disease == null) {
                    partner.disease = new Disease();
                }
            }
        }
    }

    /**
     * Generate a number representing the number of births, if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= this.getBreedingProbability()) {
            births = rand.nextInt(this.getMaxBirthSize()) + 1;
        }
        return births;
    }

    /**
     * A jaguar can breed if it has reached the breeding age.
     */
    public boolean canBreed()
    {
        return age >= this.getBreedingAge();
    }

    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if (object instanceof Organism) {
                Organism organism = (Organism) object;            
                if(this.getPrey().contains(organism.getClass())) {
                    if(organism.isAlive()) { 
                        organism.setDead();
                        foodLevel = this.getMaxFood();
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns a new animal of the subclass type. An animal can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param  randomStart  Whether the animal starts 'newborn' or with random stats.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    abstract public Animal getNewObject(boolean randomStart, Field field, Location loc);

    /**
     *@return  True if male, false if female. 
     */
    public boolean isMale(){
        return male;
    } 

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
            //System.out.println("Killed by hunger");
        }
    }

    /**
     * Carries out all actions to do with disease.
     * This involves the animal attempting to cure itself of the disease, succumbing to the disease and catching the disease.
     */
    public void diseaseAction()
    {
        // Animal has a disease: check if it can cure itself or if it dies.
        if (disease != null) {
            // See if animal suceeds in curing itself of the disease.
            if (disease.attemptCure()) {
                disease = null;
            }
            // See if the disease kills the animal.
            else {
                if (disease.attemptKill()) {
                    setDead();
                }
            }
        }
        // Animal does not have disease: check if it catches it.
        else {
            if ((disease == null) && (Disease.attemptInfect())) {
                disease = new Disease();
            }
        }
    }

    /**
     * @return  The maximum age to which an animal can live.
     */
    abstract public int getMaxAge();

    /**
     * @return  The number of steps an animal can go without eating before it dies.
     */
    abstract public int getMaxFood();

    /**
     * @return  What types of animals or plants an animal eats.
     */
    abstract public ArrayList<Class> getPrey();

    /**
     * @return  The age at which an animal can start to breed.
     */
    abstract public int getBreedingAge();

    /**
     * @return  How many steps before an animal can give birth again.
     */
    abstract public int getBirthCooldown();

    /**
     * @return  The maximum range an animal will look in for breeding.
     */
    abstract public int getBreedingRange();

    /**
     * @return  The likelihood of an animal breeding.
     */
    abstract public double getBreedingProbability();

    /**
     * @return  The maximum number of offspring an animal can give birth to in a single event.
     */
    abstract public int getMaxBirthSize();
}
