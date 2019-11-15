package resourceexchangearena;

import java.io.IOException;
import java.util.*;

/**
 * The arena is the core of the simulation, this is where all the resource exchanges take place.
 */
public class ResourceExchangeArena {
    // The current version of the simulation, used to organise output data.
    static final String RELEASE_VERSION = "v2";

    // Constants defining the scope of the simulation.
    static final int SIMULATION_RUNS = 50;
    static final int DAYS = 50;
    static final int EXCHANGES = 50;
    static final int POPULATION_SIZE = 96;
    static final int MAXIMUM_PEAK_CONSUMPTION = 16;
    static final int UNIQUE_TIME_SLOTS = 24;
    static final int SLOTS_PER_AGENT = 4;

    // Constants representing the available agent types for the simulation.
    static final int SELFISH = 1;
    static final int SOCIAL = 2;

    // Create a single Random object for generating random numerical data for the simulation.
    static Random random = new Random();

    /**
     * This is the main method which runs the entire ResourceExchangeArena simulation.
     *
     * @param args Unused.
     * @exception IOException On input error.
     * @see IOException
     */
    public static void main(String[] args) throws IOException {
        ArenaEnvironment environment = new ArenaEnvironment();
    }
}
