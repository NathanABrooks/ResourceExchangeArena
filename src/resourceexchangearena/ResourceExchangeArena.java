package resourceexchangearena;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


//
// Planned changes:
// chance of changing based on difference
// run for 500 for direct comparison
// Add graph of total social capital in social, + average + median + standard deviation (overall and split for types)
// then run for 2k days to allow time to flatten
// Finally param sweep starting ratio changes
//


/**
 * Overarching parent class containing parameters that alter the scope of the simulation.
 */
public class ResourceExchangeArena {
    // REQUIRED SYSTEM PATHS, SET THESE BEFORE RUNNING THE SIMULATION.
    // Conda env. location.
    static final String pythonExe = "/home/nathan/anaconda3/envs/ResourceExchangeArena/bin/python";
    // Data visualiser location, most users will only need to change the username here.
    static final String pythonPath = "/home/nathan/code/ResourceExchangeArena/src/datahandler/DataVisualiser.py";
    // Summary d visualiser location, most users will only need to change the username here.
    static final String summaryPythonPath =
            "/home/nathan/code/ResourceExchangeArena/src/datahandler/DataVisualiserSummaryGraphs.py";

    // Constants representing the available agent types for the simulation.
    static final int SELFISH = 1;
    static final int SOCIAL = 2;
    static final int[] ALL_AGENT_TYPES = {SELFISH, SOCIAL};

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

        // Name of the folder that will contain all of the simulations currently being ran.
        final String FOLDER_NAME = "dataForPaper_changingStrategyBasedOnDifference";

        //#############################################################################################################
        // ALTER THESE PARAMETERS IN ORDER TO SIMULATE VARIOUS SCENARIOS.
        // In order to schedule multiple parameter combinations when performing a parameter sweep, add more items to
        // the following arrays. All possible combinations will be simulated.

        // Number of exchange rounds per day.
        final int[] EXCHANGES_ARRAY = {10,25,50,75,100,150,175,200};

        // Number of agents that will evolve their strategy per day.
        final int[] NUMBER_OF_AGENTS_TO_EVOLVE_ARRAY = {0,10,19,29,38,48,58,67,77,86,96};

        // Ratio of starting agent types, i.e. {SELFISH, SELFISH, SOCIAL} would cause the simulation to start with two
        // selfish agents for each social agent.
        final int[][] AGENT_TYPES_ARRAY = {{SELFISH, SOCIAL}};
        //#############################################################################################################

        // Alter the length of time to be simulated.
        final int DAYS = 500;

        // Increase the number of simulation runs for more consistent results.
        final int SIMULATION_RUNS = 50;

        // Alter the number of Agents and their requirements. Note that the simulation has not been designed in order
        // to support this and so some combinations may cause errors.
        final int POPULATION_SIZE = 96;
        final int MAXIMUM_PEAK_CONSUMPTION = 16;
        final int UNIQUE_TIME_SLOTS = 24;
        final int SLOTS_PER_AGENT = 4;

        // Days that will have the Agents average satisfaction over the course of the day,
        // and satisfaction distribution at the end of the day visualised.
        final int[] DAYS_OF_INTEREST = {1, 25, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500};

        // Configures the simulation to output the state of each agent after each exchange and at the end of each day.
        // DUE TO THE POTENTIAL VOLUME OF DATA THIS CAN GENERATE, IT IS HIGHLY RECOMMENDED THAT THIS REMAINS SET TO
        // 'false' OUTSIDE OF STATISTICAL TESTING OR WHERE OTHERWISE REQUIRED.
        final boolean ADDITIONAL_DATA = false;

        // The seed can be set to replicate previous simulations.
        seed = System.currentTimeMillis();

        // Set the simulations initial random seed.
        random.setSeed(seed);

        /*
         * The arena is the environment in which all simulations take place.
         *
         * @param folderName String representing the output destination folder, used to organise output data.
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
         * @param agentTypes Integer array containing the agent types that the simulation will begin with. The same type
         *                   can exist multiple times in the array where more agents of one type are required.
         * @param comparingExchangesCSVWriter FileWriter used to add data to summaryGraphs file.
         * @exception IOException On input error.
         * @see IOException
         */

        // Create a directory to store the data output by all simulations being run.
        String dataOutputFolder = "results/" + FOLDER_NAME;
        Path dataOutputPath = Paths.get(dataOutputFolder);
        Files.createDirectories(dataOutputPath);

        // Create a directory to store the data output by the summary graphs.
        String summaryDataOutputFolder = dataOutputFolder + "/summaryGraphs/data";
        Path summaryDataOutputPath = Paths.get(summaryDataOutputFolder);
        Files.createDirectories(summaryDataOutputPath);

        // Stores the key data about all simulations for organisational purposes.
        File allSimulationsData = new File(
                "results/" + FOLDER_NAME,"allSimulationsData.txt");

        FileWriter allSimulationsDataWriter = new FileWriter(allSimulationsData);

        allSimulationsDataWriter.append("Simulation Information (all runs): \n\n");
        allSimulationsDataWriter.append("Days of interest: ").append(Arrays.toString(DAYS_OF_INTEREST)).append("\n");
        allSimulationsDataWriter.append("Additional data: ").append(String.valueOf(ADDITIONAL_DATA)).append("\n");
        allSimulationsDataWriter.append("Simulation runs: ").append(String.valueOf(SIMULATION_RUNS)).append("\n");
        allSimulationsDataWriter.append("Days: ").append(String.valueOf(DAYS)).append("\n");
        allSimulationsDataWriter.append("Population size: ").append(String.valueOf(POPULATION_SIZE)).append("\n");
        allSimulationsDataWriter.append("Maximum peak consumption: ").append(String.valueOf(MAXIMUM_PEAK_CONSUMPTION))
                .append("\n");
        allSimulationsDataWriter.append("Unique time slots: ").append(String.valueOf(UNIQUE_TIME_SLOTS)).append("\n");
        allSimulationsDataWriter.append("Slots per agent: ").append(String.valueOf(SLOTS_PER_AGENT)).append("\n\n\n");
        allSimulationsDataWriter.append("Simulation Information (specific run details): \n\n");

        // Perform a parameter sweep for the key parameters being tested.
        int simVersionsCompleted = 0;
        int summaryGraphsMade = 0;
        for (int[] AGENT_TYPES : AGENT_TYPES_ARRAY) {
            for (int NUMBER_OF_AGENTS_TO_EVOLVE : NUMBER_OF_AGENTS_TO_EVOLVE_ARRAY) {

                // For differing numbers of exchange rounds per day, data is stored so that summary graphs can be made
                // comparing the results of the simulation of the number of exchange rounds varies.
                File comparingExchangesFile = new File(
                        summaryDataOutputFolder,
                        "exchangesComparisonGraphData_" + summaryGraphsMade + ".csv");

                FileWriter comparingExchangesCSVWriter = new FileWriter(comparingExchangesFile);

                comparingExchangesCSVWriter.append("Exchanges");
                comparingExchangesCSVWriter.append(",");
                comparingExchangesCSVWriter.append("Day");
                for (Integer type : ALL_AGENT_TYPES) {
                    comparingExchangesCSVWriter.append(",");
                    comparingExchangesCSVWriter.append(Inflect.getHumanReadableAgentType(type));
                }
                for (Integer type : ALL_AGENT_TYPES) {
                    comparingExchangesCSVWriter.append(",");
                    comparingExchangesCSVWriter.append(Inflect.getHumanReadableAgentType(type))
                            .append(" Standard Deviation");
                }
                comparingExchangesCSVWriter.append("\n");

                summaryGraphsMade++;

                for (int EXCHANGES : EXCHANGES_ARRAY) {
                    new ArenaEnvironment(
                            FOLDER_NAME,
                            DAYS_OF_INTEREST,
                            ADDITIONAL_DATA,
                            SIMULATION_RUNS,
                            DAYS,
                            EXCHANGES,
                            POPULATION_SIZE,
                            MAXIMUM_PEAK_CONSUMPTION,
                            UNIQUE_TIME_SLOTS,
                            SLOTS_PER_AGENT,
                            NUMBER_OF_AGENTS_TO_EVOLVE,
                            AGENT_TYPES,
                            comparingExchangesCSVWriter
                    );

                    simVersionsCompleted++;
                    System.out.println("Simulation versions completed: " + simVersionsCompleted);

                    // The parameters that have finished being tested are stored so that it is clear what they were
                    // when looking at the results.
                    allSimulationsDataWriter.append("Seed: ").append(String.valueOf(seed)).append("\n");
                    allSimulationsDataWriter.append("Exchanges: ").append(String.valueOf(EXCHANGES)).append("\n");
                    allSimulationsDataWriter.append("Number of agents to evolve: ")
                            .append(String.valueOf(NUMBER_OF_AGENTS_TO_EVOLVE))
                            .append("\n");
                    allSimulationsDataWriter.append("Starting ratio of agent types: ");
                    int typesListed = 0;
                    for (int type : AGENT_TYPES) {
                        if(typesListed != 0){
                            allSimulationsDataWriter.append(" : ");
                        }
                        typesListed++;
                        allSimulationsDataWriter.append(Inflect.getHumanReadableAgentType(type));
                    }
                    allSimulationsDataWriter.append("\n\n");
                }
                comparingExchangesCSVWriter.close();

                // Collect the required data and pass it to the Python data visualiser to produce summary graphs
                // showing how a differing number of exchanges per day effects the simulation.
                List<String> pythonArgs = new ArrayList<>();

                int maxExchanges = 0;
                for (int EXCHANGES : EXCHANGES_ARRAY) {
                    if (EXCHANGES > maxExchanges) {
                        maxExchanges = EXCHANGES;
                    }
                }

                pythonArgs.add(pythonExe);
                pythonArgs.add(summaryPythonPath);

                // The output destination folder, used to organise output data.
                pythonArgs.add(FOLDER_NAME);

                // A unique tag so that generated graphs can easily be associated with their corresponding data sets.
                pythonArgs.add(String.valueOf(simVersionsCompleted));

                // The absolute path of the data set required for generating the graphs.
                pythonArgs.add(comparingExchangesFile.getAbsolutePath());

                // The total number of exchanges that have been simulated, determines graphs axis dimensions.
                pythonArgs.add(String.valueOf(maxExchanges));

                // The specific days that will have a line graph showing the satisfaction of each agent type generated.
                pythonArgs.add(Arrays.toString(DAYS_OF_INTEREST));

                ProcessBuilder builder = new ProcessBuilder(pythonArgs);

                // IO from the Python is shared with the same terminal as the Java code.
                builder.inheritIO();
                builder.redirectErrorStream(true);

                Process process = builder.start();
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
        allSimulationsDataWriter.close();
    }
}
