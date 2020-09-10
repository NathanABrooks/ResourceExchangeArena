package resource_exchange_arena;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Overarching parent class containing parameters that alter the scope of the simulation.
 */
public class ResourceExchangeArena extends UserParameters {

    // Create a single Random object for generating random numerical data for the simulation, a single object exists to
    // allow for result replication given a specific user seed.
    static Random random = new Random();

    /**
     * This is the main method which runs the entire ResourceExchangeArena simulation.
     *
     * @param args Unused.
     * @exception IOException On input error.
     * @see IOException
     */
    public static void main(String[] args) throws IOException {

        // Set the simulations initial random seed.
        random.setSeed(seed);

        // Create a directory to store the data output by all simulations being run.
        String dataOutputFolder = "results/" + FOLDER_NAME;
        Path dataOutputPath = Paths.get(dataOutputFolder);
        Files.createDirectories(dataOutputPath);

        // Create a directory to store the data output by the summary graphs.
        String summaryDataOutputFolder = dataOutputFolder + "/summaryGraphs/data";
        Path summaryDataOutputPath = Paths.get(summaryDataOutputFolder);
        Files.createDirectories(summaryDataOutputPath);

        // Stores the key data about all simulations for organisational purposes.
        File allSimulationsData = new File("results/" + FOLDER_NAME,"allSimulationsData.txt");

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
                // comparing the results of the simulation of the number of exchange rounds varies, as well as how
                // the population distribution changes.
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

                File comparingPopulationDistributionsFile = new File(
                        summaryDataOutputFolder,
                        "exchangesPopulationDistributionsGraphData_" + summaryGraphsMade + ".csv");

                FileWriter comparingPopulationDistributionsCSVWriter =
                        new FileWriter(comparingPopulationDistributionsFile);

                comparingPopulationDistributionsCSVWriter.append("Exchanges");
                comparingPopulationDistributionsCSVWriter.append(",");
                comparingPopulationDistributionsCSVWriter.append("Day");
                for (Integer type : ALL_AGENT_TYPES) {
                    comparingPopulationDistributionsCSVWriter.append(",");
                    comparingPopulationDistributionsCSVWriter.append(Inflect.getHumanReadableAgentType(type));
                }
                comparingPopulationDistributionsCSVWriter.append("\n");

                summaryGraphsMade++;

                for (int EXCHANGES : EXCHANGES_ARRAY) {

                    String initialSeed = seed + "L";

                    // The parameters about to be tested are stored so that it is clear what they were when looking at
                    // the results.
                    allSimulationsDataWriter.append("Seed: ").append(initialSeed).append("\n");
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

                    /*
                     * The arena is the environment in which all simulations take place.
                     *
                     * @param folderName String representing the output destination folder, used to organise output
                     *                   data.
                     * @param initialSeed String representing the seed of the first simulation run included in the
                     *                    results, added to the results file names so that they can be replicated.
                     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the
                     *                       simulation.
                     * @param additionalData Boolean value that configures the simulation to output the state of each
                     *                       agent after each exchange and at the end of each day.
                     * @param simulationRuns Integer value representing the number of simulations to be ran and
                     *                       averaged.
                     * @param days Integer value representing the number of days to be simulated.
                     * @param exchanges Integer value representing the number of times all agents perform pairwise
                     *                  exchanges per day.
                     * @param populationSize Integer value representing the size of the initial agent population.
                     * @param maximumPeakConsumption Integer value representing how many agents can be allocated to
                     *                               each time slot.
                     * @param uniqueTimeSlots Integer value representing the number of unique time slots available in
                     *                        the simulation.
                     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
                     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy
                     *                               will change at the end of each day.
                     * @param agentTypes Integer array containing the agent types that the simulation will begin with.
                     *                   The same type can exist multiple times in the array where more agents of one
                     *                   type are required.
                     * @param singleAgentType Boolean value specifying whether only a single agent type should exist,
                     *                        used for establishing baseline results.
                     * @param selectedSingleAgentType Integer value representing the single agent type to be modelled
                     *                                when singleAgentType is true.
                     * @param comparingExchangesCSVWriter FileWriter used to add data to satisfactions summary file.
                     * @param comparingPopulationDistributionsCSVWriter FileWriter used to add data to population
                     *                                                  distributions summary file.
                     * @exception IOException On input error.
                     * @see IOException
                     */
                    new ArenaEnvironment(
                            FOLDER_NAME,
                            initialSeed,
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
                            SINGLE_AGENT_TYPE,
                            SELECTED_SINGLE_AGENT_TYPE,
                            comparingExchangesCSVWriter,
                            comparingPopulationDistributionsCSVWriter
                    );

                    simVersionsCompleted++;
                    System.out.println("Simulation versions completed: " + simVersionsCompleted);
                }
                comparingExchangesCSVWriter.close();
                comparingPopulationDistributionsCSVWriter.close();

                // Collect the required data and pass it to the Python data visualiser to produce summary graphs
                // showing how a differing number of exchanges per day effects the simulation.
                List<String> pythonArgs = new ArrayList<>();

                int maxExchanges = 0;
                for (int EXCHANGES : EXCHANGES_ARRAY) {
                    if (EXCHANGES > maxExchanges) {
                        maxExchanges = EXCHANGES;
                    }
                }

                pythonArgs.add(PYTHON_EXE);
                pythonArgs.add(SUMMARY_PYTHON_PATH);

                // The output destination folder, used to organise output data.
                pythonArgs.add(FOLDER_NAME);

                // A unique tag so that generated graphs can easily be associated with their corresponding data sets.
                pythonArgs.add(String.valueOf(simVersionsCompleted));

                // The absolute path of the data set required for generating the performance graphs.
                pythonArgs.add(comparingExchangesFile.getAbsolutePath());

                // The absolute path of the data set required for generating the population distribution graphs.
                pythonArgs.add(comparingPopulationDistributionsFile.getAbsolutePath());

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
