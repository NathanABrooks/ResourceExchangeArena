package resource_exchange_arena;

import resource_exchange_arena.parameters.UserParameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

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
        String summaryDataOutputFolder = dataOutputFolder + "/comparativeGraphs/data";
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
                     * Begins python code that visualises comparisons of the various environments being simulated.
                     *
                     * @param pythonExe String representing the system path to python environment executable.
                     * @param pythonPath String representing the system path to the python data visualiser.
                     * @param folderName String representing the output destination folder, used to organise
                     *                   output data.
                     * @param identityNumber Integer unique tag so that generated graphs can easily be associated with
                     *                       their corresponding data sets.
                     * @param exchangesFile The data set required for generating the graphs comparing exchanges and
                     *                      performance.
                     * @param populationDistributionsFile The data set required for generating the graphs showing the
                     *                                    average population distributions.
                     * @param maximumExchangesSimulated Integer representing the total number of exchanges that have
                     *                                  been simulated, determines graphs axis dimensions.
                     * @param daysToVisualise Integer array containing the days be shown in graphs produced after the
                     *                        simulation.
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
                            comparingPopulationDistributionsCSVWriter,
                            PYTHON_EXE,
                            PYTHON_PATH
                    );

                    simVersionsCompleted++;
                    System.out.println("Simulation versions completed: " + simVersionsCompleted);
                }
                comparingExchangesCSVWriter.close();
                comparingPopulationDistributionsCSVWriter.close();

                int maxExchanges = 0;
                for (int EXCHANGES : EXCHANGES_ARRAY) {
                    if (EXCHANGES > maxExchanges) {
                        maxExchanges = EXCHANGES;
                    }
                }

                /*
                 * Begins python code that visualises the gathered data from the current environment being simulated.
                 *
                 * @param pythonExe String representing the system path to python environment executable.
                 * @param pythonPath String representing the system path to the python data visualiser.
                 * @param folderName String representing the output destination folder, used to organise output data.
                 * @param initialSeed String representing the seed of the first simulation run included in the results, this string
                 *                    added to the results file names so that they can be easily replicated.
                 * @param averageSatisfactionsFile Stores the average satisfaction of each Agent type at the end of each day, as
                 *                                 well as the optimum average satisfaction and the satisfaction if allocations
                 *                                 remained random.
                 * @param individualsDataFile Stores the satisfaction of each individual Agent at the end of every round throughout
                 *                            the simulation.
                 * @param populationDistributionsFile Shows how the population of each Agent type varies throughout the simulation,
                 *                                    influenced by social learning.
                 * @param endOfDaySatisfactionsFile Stores the satisfaction of each agent at the end of days of interest.
                 * @param days Integer value representing the number of days to be simulated.
                 * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per day.
                 * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
                 * @exception IOException On input error.
                 * @see IOException
                 */
                new ComparativeVisualiserInitiator(
                        PYTHON_EXE,
                        PYTHON_PATH,
                        FOLDER_NAME,
                        simVersionsCompleted,
                        comparingExchangesFile,
                        comparingPopulationDistributionsFile,
                        maxExchanges,
                        DAYS_OF_INTEREST
                );
            }
        }
        allSimulationsDataWriter.close();
    }
}
