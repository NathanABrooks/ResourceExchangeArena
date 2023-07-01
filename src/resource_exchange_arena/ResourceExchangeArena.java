package resource_exchange_arena;

import resource_exchange_arena.parameters.UserParameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class ResourceExchangeArena extends UserParameters {

    // Create a single Random object for generating random numerical data for the simulation, a single object exists to
    // allow for result replication given a specific user seed.
    static Random random = new Random();

    /**
     * This is the main method which runs the entire {@link ResourceExchangeArena} simulation.
     *
     * @param args Unused.
     * @throws IOException On input error.
     * @see IOException
     */
    public static void main(String[] args) throws IOException {
        switch (COMPARISON_LEVEL) {
            case 1 -> {
                // Test user parameters with and without social capital for comparison.
                USE_SOCIAL_CAPITAL = false;
                runSimulationSet();
                System.out.println("********** 1 / 2 ENVIRONMENT VERSIONS COMPLETE **********");
                USE_SOCIAL_CAPITAL = true;
                runSimulationSet();
                System.out.println("********** 2 / 2 ENVIRONMENT VERSIONS COMPLETE **********");
            }
            case 2 -> {
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
            }
            default ->
                // Run only the set of parameters defined by the user.
                    runSimulationSet();
        }

        // String version of starting ratios for file names.
        ArrayList<String> startingRatiosArray = new ArrayList<>();

        for (int[] AGENT_TYPES : AGENT_TYPES_ARRAY) {
            StringBuilder ratio = new StringBuilder();
            int typesListed = 0;
            for (int type : AGENT_TYPES) {
                if (typesListed != 0) {
                    ratio.append("_");
                }
                typesListed++;
                ratio.append(Inflect.getHumanReadableAgentType(type));
            }
            startingRatiosArray.add(ratio.toString());
        }
    }

    private static void runSimulationSet() throws IOException {
        // Set the simulations initial random seed.
        random.setSeed(seed);

        // Create a directory to store the data output by all simulations being run.
        String dataOutputFolder = FOLDER_NAME + "/useSC_" + USE_SOCIAL_CAPITAL + "_AType_";
        dataOutputFolder += !SINGLE_AGENT_TYPE ? "mixed" : Inflect.getHumanReadableAgentType(SELECTED_SINGLE_AGENT_TYPE);

        Path dataOutputPath = Paths.get(dataOutputFolder);
        Files.createDirectories(dataOutputPath);

        // Stores the key data about all simulations for organisational purposes.
        File allSimulationsData = new File(dataOutputFolder, "allSimulationsData.txt");

        FileWriter allSimulationsDataWriter = new FileWriter(allSimulationsData);

        Utilities.write(allSimulationsDataWriter, "Simulation Information (all runs): \n\n",
                "Single agent type: ", String.valueOf(SINGLE_AGENT_TYPE),
                "\n", "Use social capital: ", String.valueOf(USE_SOCIAL_CAPITAL),
                "\n", "Simulation runs: ", String.valueOf(SIMULATION_RUNS),
                "\n", "Additional Days: ", String.valueOf(DAYS),
                "\n", "Population size: ", String.valueOf(POPULATION_SIZE),
                "\n", "Unique time slots: ", String.valueOf(UNIQUE_TIME_SLOTS),
                "\n", "Slots per agent: ", String.valueOf(SLOTS_PER_AGENT), "\n"
        );

        if (SINGLE_AGENT_TYPE) {
            allSimulationsDataWriter.append("Agent type: ")
                    .append(String.valueOf(SELECTED_SINGLE_AGENT_TYPE)).append("\n");
        }
        allSimulationsDataWriter.append("Simulation Information (specific run details): \n\n");


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

                String initialSeed = seed + "L";

                // The parameters about to be tested are stored so that it is clear what they were when looking at
                // the results.
                allSimulationsDataWriter.append("Seed: ").append(initialSeed).append("\n");
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

                /*
                 * The arena is the environment in which all simulations take place.
                 */
                new ArenaEnvironment(
                        dataOutputFolder,
                        fileName,
                        DEMAND_CURVES,
                        AVAILABILITY_CURVE,
                        USE_SOCIAL_CAPITAL,
                        SIMULATION_RUNS,
                        DAYS,
                        POPULATION_SIZE,
                        UNIQUE_TIME_SLOTS,
                        SLOTS_PER_AGENT,
                        numberOfLearningAgents[i],
                        AGENT_TYPES,
                        SINGLE_AGENT_TYPE,
                        SELECTED_SINGLE_AGENT_TYPE,
                        PYTHON_EXE,
                        PYTHON_PATH
                );

                simVersionsCompleted++;
                System.out.println("Simulation versions completed: " + simVersionsCompleted);

                if (SINGLE_AGENT_TYPE) {
                    break parameterSweep;
                }
            }
        }
        allSimulationsDataWriter.close();
    }
}
