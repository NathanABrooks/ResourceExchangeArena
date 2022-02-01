package resource_exchange_arena;

import resource_exchange_arena.parameters.UserParameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        switch (COMPARISON_LEVEL) {
            case 1:
                // Test user parameters with and without social capital for comparison.
                USE_SOCIAL_CAPITAL = false;
                runSimulationSet();
                System.out.println("********** 1 / 2 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = true;
                runSimulationSet();
                System.out.println("********** 2 / 2 ENVIRONMENT VERSIONS COMPLETE **********");
                break;
            case 2:
                // As above but also test single agent type populations for reference.
                USE_SOCIAL_CAPITAL = false;
                SINGLE_AGENT_TYPE = true;
                SELECTED_SINGLE_AGENT_TYPE = SELFISH;
                runSimulationSet();
                System.out.println("********** 1 / 5 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = false;
                SINGLE_AGENT_TYPE = true;
                SELECTED_SINGLE_AGENT_TYPE = SOCIAL;
                runSimulationSet();
                System.out.println("********** 2 / 5 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = true;
                SINGLE_AGENT_TYPE = true;
                SELECTED_SINGLE_AGENT_TYPE = SOCIAL;
                runSimulationSet();
                System.out.println("********** 3 / 5 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = false;
                SINGLE_AGENT_TYPE = false;
                runSimulationSet();
                System.out.println("********** 4 / 5 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = true;
                SINGLE_AGENT_TYPE = false;
                runSimulationSet();
                System.out.println("********** 5 / 5 ENVIRONMENT VERSIONS COMPLETE **********");
                break;
            default:
                // Run only the set of parameters defined by the user.
                runSimulationSet();
        }

        // String version of starting ratios for file names.
        ArrayList<String> startingRatiosArray = new ArrayList<String>();

        for (int[] AGENT_TYPES : AGENT_TYPES_ARRAY) {
            String ratio = "";
            int typesListed = 0;
            for (int type : AGENT_TYPES) {
                if (typesListed != 0) {
                    ratio += "_";
                }
                typesListed++;
                ratio += Inflect.getHumanReadableAgentType(type);
            }
            startingRatiosArray.add(ratio);          
        }

        /*
         * Begins python code that generates heatmaps to compare various simulations.
         *
         * @param pythonExe String representing the system path to python environment executable.
         * @param pythonPath String representing the system path to the python data visualiser.
         * @param folderName String representing the output destination folder, used to organise output data.
         * @param comparisonLevel Integer unique to quickly identify which heat maps should be generated.
         * @param socialCapital Boolean representing whether social capital was enabled if the comparison level was set to 0.
         * @param learningPercentages Integer array of the percentage of Agents that possibly used social Learning per day.
         * @param exchangesArray Integer array of the various number of exchanges per day that were simulated.
         * @param startingRatiosArray String arraylist of the various starting ratios between agent types that were simulated.
         * @param daysOfInterest Integer array containing the days to be analysed.
         * @exception IOException On input error.
         * @see IOException
         */
        new HeatMapsInitiator(
                PYTHON_EXE,
                PYTHON_PATH,
                FOLDER_NAME,
                COMPARISON_LEVEL,
                USE_SOCIAL_CAPITAL,
                PERCENTAGE_OF_AGENTS_TO_EVOLVE_ARRAY,
                EXCHANGES_ARRAY,
                startingRatiosArray,
                DAYS_OF_INTEREST
        );

        if (COMPARISON_LEVEL != 0) {
            /*
            * Begins python code that runs statistical significance test on the simulation results.
            *
            * @param pythonExe String representing the system path to python environment executable.
            * @param pythonPath String representing the system path to the python data visualiser.
            * @param folderName String representing the output destination folder, used to organise output data.
            * @param learningPercentages Integer array of the percentage of Agents that possibly used social Learning per day.
            * @param exchangesArray Integer array of the various number of exchanges per day that were simulated.
            * @param startingRatiosArray String arraylist of the various starting ratios between agent types that were simulated.
            * @param daysOfInterest Integer array containing the days to be analysed.
            * @exception IOException On input error.
            * @see IOException
            */
            new significanceTestInitiator(
                    PYTHON_EXE,
                    PYTHON_PATH,
                    FOLDER_NAME,
                    PERCENTAGE_OF_AGENTS_TO_EVOLVE_ARRAY,
                    EXCHANGES_ARRAY,
                    startingRatiosArray,
                    DAYS_OF_INTEREST
            );
        }
    }
    
    private static void runSimulationSet() throws IOException {
        // Set the simulations initial random seed.
        random.setSeed(seed);

        // Create a directory to store the data output by all simulations being run.
        String dataOutputFolder = FOLDER_NAME + "/useSC_" + USE_SOCIAL_CAPITAL + "_AType_";
        if (!SINGLE_AGENT_TYPE) {
            dataOutputFolder += "mixed";
        } else {
            dataOutputFolder += Inflect.getHumanReadableAgentType(SELECTED_SINGLE_AGENT_TYPE);
        }

        Path dataOutputPath = Paths.get(dataOutputFolder);
        Files.createDirectories(dataOutputPath);

        // Create a directory to store the data output by the summary graphs.
        String summaryDataOutputFolder = dataOutputFolder + "/comparative/data";
        Path summaryDataOutputPath = Paths.get(summaryDataOutputFolder);
        Files.createDirectories(summaryDataOutputPath);

        // Stores the key data about all simulations for organisational purposes.
        File allSimulationsData = new File(dataOutputFolder, "allSimulationsData.txt");

        FileWriter allSimulationsDataWriter = new FileWriter(allSimulationsData);

        allSimulationsDataWriter.append("Simulation Information (all runs): \n\n");
        allSimulationsDataWriter.append("Single agent type: ").append(String.valueOf(SINGLE_AGENT_TYPE)).append("\n");
        if (SINGLE_AGENT_TYPE) {
            allSimulationsDataWriter.append("Agent type: ")
                    .append(String.valueOf(SELECTED_SINGLE_AGENT_TYPE)).append("\n");
        }
        allSimulationsDataWriter.append("Use social capital: ").append(String.valueOf(USE_SOCIAL_CAPITAL)).append("\n");
        allSimulationsDataWriter.append("Simulation runs: ").append(String.valueOf(SIMULATION_RUNS)).append("\n");
        allSimulationsDataWriter.append("Days: ").append(String.valueOf(DAYS)).append("\n");
        allSimulationsDataWriter.append("Days of interest: ").append(Arrays.toString(DAYS_OF_INTEREST)).append("\n");
        allSimulationsDataWriter.append("Population size: ").append(String.valueOf(POPULATION_SIZE)).append("\n");
        allSimulationsDataWriter.append("Unique time slots: ").append(String.valueOf(UNIQUE_TIME_SLOTS)).append("\n");
        allSimulationsDataWriter.append("Slots per agent: ").append(String.valueOf(SLOTS_PER_AGENT)).append("\n");
        allSimulationsDataWriter.append("Simulation Information (specific run details): \n\n");

        // Create directories to organise summary graphs data.
        String satisfactionComparisonFolder = summaryDataOutputFolder + "/SaE";
        Path satisfactionComparisonPath = Paths.get(satisfactionComparisonFolder);
        Files.createDirectories(satisfactionComparisonPath);

        String popDistComparisonFolder = summaryDataOutputFolder + "/PDaE";
        Path popDistComparisonPath = Paths.get(popDistComparisonFolder);
        Files.createDirectories(popDistComparisonPath);

        // Percentage of learning agents converted to actual number of agents that can learn each day.
        int[] numberOfLearningAgents = new int[PERCENTAGE_OF_AGENTS_TO_EVOLVE_ARRAY.length];
        float onePercent = POPULATION_SIZE / 100.0f;
        for (int i = 0; i < PERCENTAGE_OF_AGENTS_TO_EVOLVE_ARRAY.length; i++) {
            int learningAgents = Math.round(onePercent * PERCENTAGE_OF_AGENTS_TO_EVOLVE_ARRAY[i]);
            numberOfLearningAgents[i] = learningAgents;
        }

        // Perform a parameter sweep for the key parameters being tested.
        int simVersionsCompleted = 0;
        parameterSweep:
        for (int[] AGENT_TYPES : AGENT_TYPES_ARRAY) {
            for (int i = 0; i < numberOfLearningAgents.length; i++) {

                String fileName;

                if (!SINGLE_AGENT_TYPE) {
                    fileName = "AE_" + PERCENTAGE_OF_AGENTS_TO_EVOLVE_ARRAY[i];

                    StringBuilder typeRatio = new StringBuilder("_SR_");
                    int typesListed = 0;
                    for (int type : AGENT_TYPES) {
                        if (typesListed != 0) {
                            typeRatio.append("_");
                        }
                        typesListed++;
                        typeRatio.append(Inflect.getHumanReadableAgentType(type));
                    }
                    fileName += typeRatio;
                } else {
                    fileName = "SR_" + Inflect.getHumanReadableAgentType(SELECTED_SINGLE_AGENT_TYPE);
                }

                // For differing numbers of exchange rounds per day, data is stored so that summary graphs can be made
                // comparing the results of the simulation of the number of exchange rounds varies, as well as how
                // the population distribution changes.
                File comparingExchangesFile = new File(
                        satisfactionComparisonFolder,
                        fileName + ".csv");

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
                        popDistComparisonFolder,
                        fileName + ".csv");

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

                for (int EXCHANGES : EXCHANGES_ARRAY) {

                    String initialSeed = seed + "L";

                    // The parameters about to be tested are stored so that it is clear what they were when looking at
                    // the results.
                    allSimulationsDataWriter.append("Seed: ").append(initialSeed).append("\n");
                    allSimulationsDataWriter.append("Exchanges: ").append(String.valueOf(EXCHANGES)).append("\n");
                    allSimulationsDataWriter.append("Number of agents to evolve: ")
                            .append(String.valueOf(numberOfLearningAgents[i]))
                            .append("\n");
                    if (!SINGLE_AGENT_TYPE) {
                        allSimulationsDataWriter.append("Starting ratio of agent types: ");
                        int typesListed = 0;
                        for (int type : AGENT_TYPES) {
                            if (typesListed != 0) {
                                allSimulationsDataWriter.append(" : ");
                            }
                            typesListed++;
                            allSimulationsDataWriter.append(Inflect.getHumanReadableAgentType(type));
                        }
                    }
                    allSimulationsDataWriter.append("\n\n");

                    // Details specifics about the simulation environment.
                    String environmentTag = "EX_" + EXCHANGES + "_" + fileName;

                    /*
                     * The arena is the environment in which all simulations take place.
                     *
                     * @param folderName String representing the output destination folder, used to organise output
                     *                   data.
                     * @param environmentTag String detailing specifics about the simulation environment.
                     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the
                     *                       simulation.
                     * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used
                     *                     the agents are split equally between the curves.
                     * @param availabilityCurve Integer array of energy availability used by the simulation.
                     * @param socialCapital Boolean value that determines whether or not social agents will utilise
                     *                      social capital.
                     * @param simulationRuns Integer value representing the number of simulations to be ran and
                     *                       averaged.
                     * @param days Integer value representing the number of days to be simulated.
                     * @param exchanges Integer value representing the number of times all agents perform pairwise
                     *                  exchanges per day.
                     * @param populationSize Integer value representing the size of the initial agent population.
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
                     * @param comparingExchangesCSVWriter FileWriter used to add data to summaryGraphs file.
                     * @param comparingPopulationDistributionsCSVWriter FileWriter used to add data to population
                     *                                                  distributions summary file.
                     * @param pythonExe String representing the system path to python environment executable.
                     * @param pythonPath String representing the system path to the python data visualiser.
                     * @exception IOException On input error.
                     * @see IOException
                     */
                    new ArenaEnvironment(
                            dataOutputFolder,
                            environmentTag,
                            DAYS_OF_INTEREST,
                            DEMAND_CURVES,
                            AVAILABILITY_CURVE,
                            USE_SOCIAL_CAPITAL,
                            SIMULATION_RUNS,
                            DAYS,
                            EXCHANGES,
                            POPULATION_SIZE,
                            UNIQUE_TIME_SLOTS,
                            SLOTS_PER_AGENT,
                            numberOfLearningAgents[i],
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
                 * Begins python code that visualises comparisons of the various environments being simulated.
                 *
                 * @param pythonExe String representing the system path to python environment executable.
                 * @param pythonPath String representing the system path to the python data visualiser.
                 * @param folderName String representing the output destination folder, used to organise output data.
                 * @param identityNumber Integer unique tag so that generated graphs can easily be associated with
                 *                       their corresponding data sets.
                 * @param exchangesFile The data set required for generating the graphs comparing exchanges and
                 *                      performance.
                 * @param populationDistributionsFile The data set required for generating the graphs showing the
                 *                                    average population distributions.
                 * @param maximumExchangesSimulated Integer representing the total number of exchanges that have been
                 *                                  simulated, determines graphs axis dimensions.
                 * @param daysToVisualise Integer array containing the days be shown in graphs produced after the
                 *                        simulation.
                 * @param populationSize Integer value representing the size of the initial agent population.
                 * @exception IOException On input error.
                 * @see IOException
                 */
                new ComparativeVisualiserInitiator(
                        PYTHON_EXE,
                        PYTHON_PATH,
                        dataOutputFolder,
                        simVersionsCompleted,
                        comparingExchangesFile,
                        comparingPopulationDistributionsFile,
                        maxExchanges,
                        DAYS_OF_INTEREST,
                        POPULATION_SIZE
                );

                if (SINGLE_AGENT_TYPE) {
                    break parameterSweep;
                }
            }
        }
        allSimulationsDataWriter.close();
    }
}
