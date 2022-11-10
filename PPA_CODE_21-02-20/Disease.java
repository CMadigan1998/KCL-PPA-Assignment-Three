import java.util.Random;
/**
 * Represents a disease. A disease can infect animals. Once an animal is infected, it may spread the disease, attempt to cure itself of the disease or
 * die from the disease.
 *
 * @author Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public class Disease
{
    // The probability of the disease spreading from one animal to another.
    private static final double PROBABILITY_SPREADING = 0.3;
    // The probability of an animal catching the disease.
    private static final double PROBABILITY_CATCHING = 0.01;
    // The probability of the disease being destroyed by the animals immune system.
    private static final double probabilityOfCuring = 0.2;
    // How many steps the disease takes to kill an animal.
    private int duration;
    // Random number generator.
    private static Random random = new Random();

    // Stat counter
    private static int totalNumberKilled = 0;

    public Disease () {
        // Set the duration to a random number between 5 and 10.
        duration = 5 + random.nextInt(6);
    }

    /**
     * @return  True if the animal catches the disease, false otherwise.
     */
    public static boolean attemptInfect() {
        return (random.nextDouble() < PROBABILITY_CATCHING);
    }

    /**
     * @return  True if the animal spreads from one animal to another, false otherwise.
     */
    public static boolean attemptSpread()
    {
        return (random.nextDouble() < PROBABILITY_SPREADING);
    }

    /**
     * @return  True if the animal becomes cured of the disease, false otherwise.
     */
    public boolean attemptCure()
    {
        if (random.nextDouble() <  probabilityOfCuring) {
            return true;
        }
        return false;
    }

    /**
     * @return  True if the animal dies from the disease, false otherwise.
     */
    public boolean attemptKill()
    {
        duration--;
        if (duration <= 0) {
            totalNumberKilled++; 
            return true;
        }
        return false;
    }
    
    /**
     * @return  The total number killed by disease.
     */
    public static int getTotalNumberKilled() {
        return totalNumberKilled;
    }
}
