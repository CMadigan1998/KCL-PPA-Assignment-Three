import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.HashMap;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing monkeys and jaguars.
 * 
 * @author David J. Barnes, Michael Kölling, Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 150;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 100;
    // Hashmap storing the probabilities of animals being created during initial stimulation creation.
    private static final HashMap<String, Double> CREATION_PROBABILITIES = new HashMap<>();

    // List of plants in the field.
    private List<Organism> organisms;
    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    private int stepDelay;
    // A graphical view of the simulation.
    private SimulatorView view;
    // The current time for the simulation.
    private TimeKeeper time = new TimeKeeper();
    // The current weather for the simulation
    private WeatherSystem weatherSystem  = new WeatherSystem ();
    // Keeps track of day and night.
    private boolean isDay;
    // Random number generator
    Random rand = Randomizer.getRandom();

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        stepDelay = 300;
        CREATION_PROBABILITIES.put("Monkey", 0.04);
        CREATION_PROBABILITIES.put("Jaguar", 0.02);
        CREATION_PROBABILITIES.put("Gazelle", 0.06);
        CREATION_PROBABILITIES.put("Aligator", 0.03);
        CREATION_PROBABILITIES.put("Frog", 0.05);
        CREATION_PROBABILITIES.put("Plant", 0.09);

        organisms = new ArrayList<>();
        field = new Field(depth, width, weatherSystem);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, this);
        view.setColor(Monkey.class, Color.PINK);
        view.setColor(Jaguar.class, Color.YELLOW);
        view.setColor(Gazelle.class, Color.BLUE);
        view.setColor(Aligator.class, Color.RED);
        view.setColor(Frog.class, Color.MAGENTA);
        view.setColor(Plant.class, Color.GREEN);
        view.setTimeWeatherLabel(time.createTimeString() + weatherSystem.createWeatherString());
        view.setInfoText("Killed by - Disease: " + Disease.getTotalNumberKilled() + "; Thunder Storm: " + WeatherSystem.getKilledThunder());

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(stepDelay);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal.
     */
    public void simulateOneStep()
    {
        step++;
        time.countMinutes();
        weatherSystem.changeWeather();
        view.setTimeWeatherLabel(time.createTimeString() + weatherSystem.createWeatherString());
        view.setInfoText("Killed by - Disease: " + Disease.getTotalNumberKilled() + "; Thunder Storm: " + WeatherSystem.getKilledThunder());
        isDay = time.isDay();
        view.setEmptyColour(isDay);

        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();        
        // Let all organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms, isDay);
            if(!organism.isAlive()) {
                it.remove();
            }
        }

        // Add the new organisms to the main list.
        organisms.addAll(newOrganisms);

        // Check for thunder
        if (weatherSystem.isThunderStorm()) {
            for (int i = WeatherSystem.weatherTypes.getThunderIntensity(); i > 0; i--) {
                int y = rand.nextInt(field.getDepth()); 
                int x = rand.nextInt(field.getWidth());            

                // Check to see if location is outside field. If so, skip location.
                if (field.outOfBounds(y, x)){
                    continue;
                }

                // If location is not empty and contains an organism, destroy the organism.               
                if ((field.getObjectAt(y, x) != null) && (field.getObjectAt(y, x) instanceof Organism)) {
                    Organism toKill = (Organism) field.getObjectAt(y, x);
                    toKill.setDead();
                    WeatherSystem.killByThunder();
                }
            }
        }

        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        time.reset();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with animals and plants.
     */
    private void populate()
    {
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= CREATION_PROBABILITIES.get("Plant")) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(true, field, location);
                    organisms.add(plant);
                }
                else if(rand.nextDouble() <= CREATION_PROBABILITIES.get("Jaguar")) {
                    Location location = new Location(row, col);
                    Jaguar jaguar = new Jaguar(true, field, location);
                    organisms.add(jaguar);
                }
                else if(rand.nextDouble() <= CREATION_PROBABILITIES.get("Monkey")) {
                    Location location = new Location(row, col);
                    Monkey monkey = new Monkey(true, field, location);
                    organisms.add(monkey);
                }
                else if(rand.nextDouble() <= CREATION_PROBABILITIES.get("Gazelle")) {
                    Location location = new Location(row, col);
                    Gazelle gazelle = new Gazelle(true, field, location);
                    organisms.add(gazelle);
                }
                else if(rand.nextDouble() <= CREATION_PROBABILITIES.get("Aligator")) {
                    Location location = new Location(row, col);
                    Aligator aligator = new Aligator(true, field, location);
                    organisms.add(aligator);
                }
                else if(rand.nextDouble() <= CREATION_PROBABILITIES.get("Frog")) {
                    Location location = new Location(row, col);
                    Frog frog = new Frog(true, field, location);
                    organisms.add(frog);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private static void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
    
    public void speedUpSim() {
        if ( stepDelay > 0) {
            stepDelay -= 100;
        }
    }
    
    public void slowDownSim() {
        if (stepDelay < 500) {
            stepDelay += 100;
        }
    }
}
