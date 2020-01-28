package resourceexchangearena;

import java.io.IOException;
import java.util.*;

/**
 * The arena is the core of the simulation, this is where all the resource exchanges take place.
 */
public class ResourceExchangeArena {

    // Constants representing the available agent types for the simulation.
    static final int SELFISH = 1;
    static final int SOCIAL = 2;

    static long seed = System.currentTimeMillis();

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

        // The current version of the simulation, used to organise output data.
        final String RELEASE_VERSION = "v3";

        // Days that will have the Agents average satisfaction over the course of the day,
        // and satisfaction distribution at the end of the day visualised.
        final int[] DAYS_OF_INTEREST = {1, 25, 50};

        // Constants defining the scope of the simulation.
        final int SIMULATION_RUNS = 50;
        final int DAYS = 50;
        final int EXCHANGES = 200;
        final int POPULATION_SIZE = 96;
        final int MAXIMUM_PEAK_CONSUMPTION = 16;
        final int UNIQUE_TIME_SLOTS = 24;
        final int SLOTS_PER_AGENT = 4;

        random.setSeed(seed);

        new ArenaEnvironment(
                RELEASE_VERSION,
                DAYS_OF_INTEREST,
                SIMULATION_RUNS,
                DAYS,
                EXCHANGES,
                POPULATION_SIZE,
                MAXIMUM_PEAK_CONSUMPTION,
                UNIQUE_TIME_SLOTS,
                SLOTS_PER_AGENT
        );
    }
}
