
/**
 * This class is responsible for keeping track of the time in terms of minutes, hours and
 * days. It also decides how many minutes each step of the simulation represents. It has 
 * various other functions to do with time.
 *
 * @author Charlie Madigan(K19019003), Kacper Dudzinski (K1921541)
 * @version 2020.02.21
 */
public class TimeKeeper
{
    // The number of minutes each step of the simulation represents.
    private final int MINUTE_PER_STEP = 10;
    // The current number of minutes, hours and days.
    private int minute, hour, day;

    /**
     * Sets the number of minutes per step of the simulation to the default value of 5. 
     * Sets minute, hour and day to 0 (therefore night time).
     */
    public TimeKeeper()
    {
        minute = hour = day = 0;
    }

    /**
     * Increments the number of minutes by the chosen amount per step of the simulation.
     * If minutes reach 60, resets to 0 and calls countHours().
     */
    public void countMinutes()
    {
        for (int i = 0; i < MINUTE_PER_STEP; i++) {
            minute++;
            if (minute >= 60) {
                minute = 0;
                countHours();
            }
        }
    }
    
    /**
     * Increments the number of hours by one.
     * If hours reach 24, resets to 0 and calls countDays(). 
     */
    public void countHours()
    {
        hour++;
        if (hour >= 24) {
            hour = 0;
            countDays();
        }
    }
    
    /**
     * Increments the number of days by one.
     */
    public void countDays()
    {
        day++;
    }
    
    /**
     * @return  A string displaying whether it's day or night as well as 
     * the number of days, hours and minutes.
     */
    public String createTimeString() 
    {
       String tmp = "Time: {Day: " + day + " Hour: " + hour + " Minute: " + minute + "} ";   
       if (isDay()) {
          tmp += "(Day)"; 
       }
       else {
           tmp += "(Night)";
       }
       return tmp;
    }
    
    /**
     * Resets the number of minutes, hours and days to 0.
     */
    public void reset()
    {
        minute = hour = day = 0;
        //hour = 8;  // Uncomment to make simulation start at daytime
    }
    
    /**
     * Returns true if it's day, false if it's night. Nightime is the period of time between
     * 20:00 - 08:00.
     * 
     * @return  True if day, false if night.
     */
    public boolean isDay()
    {
        return (hour > 20 || hour < 8) ?  false : true;
    }
    
    /**
     * @return True if a new day has just began, false otherwise.
     */
    public boolean newDay()
    {
        return (hour == 0);
    }
}
