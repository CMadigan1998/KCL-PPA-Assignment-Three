import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal.
 * Posistions are labelled from the top left corner, starting with 0.
 * 
 * @author David J. Barnes, Michael Kölling, Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();

    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals.
    private Object[][] field;
    // The weather system for the field.
    private WeatherSystem weatherSystem;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     * @param  weatherSystem  The weather system for the field.
     */
    public Field(int depth, int width, WeatherSystem weatherSystem)
    {
        this.depth = depth;
        this.width = width;
        this.weatherSystem = weatherSystem;
        field = new Object[depth][width];
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }

    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        field[location.getRow()][location.getCol()] = null;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object animal, int row, int col)
    {
        place(animal, new Location(row, col));
    }

    /**
     * Place an animal or plant at the given location.
     * If there is already an animal or plant at the location it will
     * be lost.
     * @param obj The animal or plant to be placed.
     * @param location Where to place the animal or plant.
     */
    public void place(Object obj, Location location)
    {
        field[location.getRow()][location.getCol()] = obj;
    }

    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col];
    }

    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }

    /**
     * Get a shuffled list of the free adjacent locations. Free locations are those that contain no other animal, but may contain plants.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getObjectAt(next) == null || getObjectAt(next).getClass() == Plant.class) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Try to find a free location that is adjacent to the given location. Free locations are those that contain no other animal, but may contain plants.
     * If the location contains a plant, the plant is destroyed.
     * If there is none, return null. The returned location will be within the valid bounds of the field.
     * 
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(location);
        if(free.size() > 0) {
            if ((getObjectAt(free.get(0)) != null) && (getObjectAt(free.get(0)).getClass() == Plant.class)) {
                Plant plant = (Plant) getObjectAt(free.get(0));
                plant.setDead();
            }
            return free.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }

            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * Looks for a partner to breed with within a certain range. The closest animal of the same species and opposite sex is returned. If none found, returns null.
     * 
     * @param  animal  The animal looking for a partner.
     * @param  maxRange  The maximum range to look in.
     * @return  An Animal object of the same species but opposite sex closest to the animal but within the specified range.
     */
    public Animal findPartner (Animal animal, int maxRange) {
        if (animal == null){
            return null;
        }
        
        int animalRow = animal.getLocation().getRow();
        int animalCol = animal.getLocation().getCol();              

        for (int i = 1; i <= maxRange; i++) {
            int topLeftRow = animalRow - i;
            int topLeftCol = animalCol - i;  
            for(int x = 0; x < (2 * i) + 1; x++){
                for(int y = 0; y < (2 * i) + 1; y++) {
                    // Check to see if current location is the inner part of the grid (i.e. not perimeter). If so, skip.
                    if ((x != 0 && x != (2 * i)) && (y != 0 && y != (2 * i))){
                        continue;
                    }
    
                    // Check to see if current location is outside of the field. If so, skip.
                    if (outOfBounds(topLeftRow + y, topLeftCol + x)) {
                        continue;
                    }
    
                    // Check the validity of location and object.
                    Object obj = getObjectAt(topLeftRow + y, topLeftCol + x);
                    if (obj == null) {
                        continue;
                    }
                    if (obj.getClass() != animal.getClass()){
                        continue;
                    }
                    Animal potentialPartner = (Animal) obj;
                    if (potentialPartner.isMale() != animal.isMale()) {
                        return potentialPartner;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Checks to see if a location is within the field.
     * 
     * @param  x  The index of the column.
     * @param  y  The index of the row.
     * @return  True if outside field, false otherwise.
     */
    public boolean outOfBounds(int y, int x)
    {
        if ((y < 0) || (y >= depth) || (x < 0) || (x >= width)) {return true;}
        return false;
    }
    
    /**
     * @return  The weather system for the field.
     */
    public WeatherSystem getWeatherSystem()
    {
        return weatherSystem;
    }
}
