package resourceexchangearena;

import java.io.IOException;
import java.util.*;

/**
 * Overarching parent class containing parameters that alter the scope of the simulation.
 */
public class ResourceExchangeArena {
    // REQUIRED SYSTEM PATHS, SET THESE BEFORE RUNNING THE SIMULATION.
    // Conda env. location.
    static final String pythonExe = "/home/nathan/anaconda3/envs/ResourceExchangeArena/bin/python";
    // Data visualiser location, most users will only need to change the username here.
    static final String pythonPath = "/home/nathan/code/ResourceExchangeArena/src/datahandler/DataVisualiser.py";

    // Constants representing the available agent types for the simulation.
    static final int SELFISH = 1;
    static final int SOCIAL = 2;

    // Create a single Random object for generating random numerical data for the simulation.
    static Random random = new Random();
    static long seed;

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

        // The seed can be set to replicate previous simulations.
        seed = System.currentTimeMillis();

        // Set the simulations initial random seed.
        random.setSeed(seed);

        // Days that will have the Agents average satisfaction over the course of the day,
        // and satisfaction distribution at the end of the day visualised.
        final int[] DAYS_OF_INTEREST = {1, 25, 50};

        // Configures the simulation to output the state of each agent after each exchange and at the end of each day.
        // DUE TO THE POTENTIAL VOLUME OF DATA THIS CAN GENERATE, IT IS HIGHLY RECOMMENDED THAT THIS REMAINS SET TO
        // 'false' OUTSIDE OF TESTING OR WHERE OTHERWISE REQUIRED.
        final boolean ADDITIONAL_DATA = false;

        //#############################################################################################################
        // ALTER THESE PARAMETERS IN ORDER TO SIMULATE VARIOUS SCENARIOS.
        // In order to schedule multiple parameter combinations when performing a parameter sweep, manually add further
        // arena environments as shown on line 104.

        // Constants defining the scope of the simulation.
        final int SIMULATION_RUNS = 50;
        final int DAYS = 50;
        final int EXCHANGES = 50;
        final int POPULATION_SIZE = 96;
        final int MAXIMUM_PEAK_CONSUMPTION = 16;
        final int UNIQUE_TIME_SLOTS = 24;
        final int SLOTS_PER_AGENT = 4;
        final int NUMBER_OF_AGENTS_TO_EVOLVE = 10;
        //#############################################################################################################

        /*
         * The arena is the environment in which all simulations take place.
         *
         * @param releaseVersion String representing the current version of the simulation, used to organise output
         *                       data.
         * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
         * @param additionalData Boolean value that configures the simulation to output the state of each agent after
         *                       each exchange and at the end of each day.
         * @param simulationRuns Integer value representing the number of simulations to be ran and averaged.
         * @param days Integer value representing the number of days to be simulated.
         * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per
         *                  day.
         * @param populationSize Integer value representing the size of the initial agent population.
         * @param maximumPeakConsumption Integer value representing how many agents can be allocated to each time slot.
         * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the
         *                        simulation.
         * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
         * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change at
         *                               the end of each day.
         * @exception IOException On input error.
         * @see IOException
         */
        new ArenaEnvironment(
                RELEASE_VERSION,
                DAYS_OF_INTEREST,
                ADDITIONAL_DATA,
                SIMULATION_RUNS,
                DAYS,
                EXCHANGES,
                POPULATION_SIZE,
                MAXIMUM_PEAK_CONSUMPTION,
                UNIQUE_TIME_SLOTS,
                SLOTS_PER_AGENT,
                NUMBER_OF_AGENTS_TO_EVOLVE
        );

/*      EXAMPLES OF SCHEDULING MULTIPLE PARAMETER COMBINATIONS, simply alter the desired parameter...
                new ArenaEnvironment(
                        RELEASE_VERSION,
                        DAYS_OF_INTEREST,
                        ADDITIONAL_DATA,
                        SIMULATION_RUNS,
                        DAYS,
                        EXCHANGES,
                        POPULATION_SIZE,
                        MAXIMUM_PEAK_CONSUMPTION,
                        UNIQUE_TIME_SLOTS,
                        SLOTS_PER_AGENT,
                        20
                );
                new ArenaEnvironment(
                        RELEASE_VERSION,
                        DAYS_OF_INTEREST,
                        ADDITIONAL_DATA,
                        SIMULATION_RUNS,
                        DAYS,
                        EXCHANGES,
                        POPULATION_SIZE,
                        MAXIMUM_PEAK_CONSUMPTION,
                        UNIQUE_TIME_SLOTS,
                        SLOTS_PER_AGENT,
                        30
                );
                new ArenaEnvironment(
                        RELEASE_VERSION,
                        DAYS_OF_INTEREST,
                        ADDITIONAL_DATA,
                        SIMULATION_RUNS,
                        DAYS,
                        EXCHANGES,
                        POPULATION_SIZE,
                        MAXIMUM_PEAK_CONSUMPTION,
                        UNIQUE_TIME_SLOTS,
                        SLOTS_PER_AGENT,
                        40
                );
 */
    }
}
