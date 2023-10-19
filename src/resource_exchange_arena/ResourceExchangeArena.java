package resource_exchange_arena;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Properties;

public class ResourceExchangeArena {
    // Public seed used for repeatable randomness.
    public static long seed;

    // Constants representing the available agent types for the simulation.
    public static final int SELFISH = 1;
    public static final int SOCIAL = 2;
    public static final int[] ALL_AGENT_TYPES = {SELFISH, SOCIAL};

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

        // Retrieve user parameters from the config file.
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("/home/brooks/code/ResourceExchangeArena/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long userSeed = Long.parseLong(properties.getProperty("seed"));
        if (userSeed == -1) {
            seed = System.currentTimeMillis();
        } else {
            seed = userSeed;
        }

        final String FOLDER_NAME = properties.getProperty("results.folder") + seed;
        final String PYTHON_EXE = properties.getProperty("python.executable");
        final String PYTHON_PATH = properties.getProperty("python.scripts");
        final int POPULATION_SIZE = Integer.parseInt(properties.getProperty("population.size"));
        final int SLOTS_PER_AGENT = Integer.parseInt(properties.getProperty("agent.time-slots"));
        final int UNIQUE_TIME_SLOTS = Integer.parseInt(properties.getProperty("simulation.uniqueTime-slots"));;
        final int ADDITIONAL_DAYS = Integer.parseInt(properties.getProperty("simulation.additionalDays"));
        final int SIMULATION_RUNS = Integer.parseInt(properties.getProperty("simulation.runs"));
        boolean SINGLE_AGENT_TYPE = Boolean.parseBoolean(properties.getProperty("agent.singleType"));
        int SELECTED_SINGLE_AGENT_TYPE=SOCIAL;
        String selectedSingleAgentType = properties.getProperty("agent.selectedSingleType");
        if (selectedSingleAgentType.equalsIgnoreCase("social")) {
            SELECTED_SINGLE_AGENT_TYPE=SOCIAL;
        } else if (selectedSingleAgentType.equalsIgnoreCase("selfish")) {
            SELECTED_SINGLE_AGENT_TYPE=SELFISH;
        }
        boolean USE_SOCIAL_CAPITAL = Boolean.parseBoolean(properties.getProperty("agent.useSocialCapital"));

        final double β = Double.parseDouble(properties.getProperty("agent.beta"));
        final int COMPARISON_LEVEL = Integer.parseInt(properties.getProperty("simulation.comparisonLevel"));

        final double[][] DEMAND_CURVES = StringToDouble2DArray(properties.getProperty("demand.curves"));
        final int[] AVAILABILITY_CURVE = StringToIntArray(properties.getProperty("availability.curve"));
        final int PERCENTAGE_OF_AGENTS_TO_EVOLVE = Integer.parseInt(properties.getProperty("agents.evolvePercentage"));
        final int[] AGENT_TYPES_ARRAY = RatioToIntegerArray(properties.getProperty("agent.typeRatio"));
        final double[] SATISFACTION_CURVE = StringToDoubleArray(properties.getProperty("agent.satisfactionCurve"));
            
        // Automatically adjust parameters and repeat the simulation when comparisons are requested.
        switch (COMPARISON_LEVEL) {
            case 1:
                // Test user parameters with and without social capital for comparison.
                USE_SOCIAL_CAPITAL = false;
                runSimulationSet(seed, FOLDER_NAME, PYTHON_EXE, PYTHON_PATH, POPULATION_SIZE, SLOTS_PER_AGENT, UNIQUE_TIME_SLOTS, ADDITIONAL_DAYS, SIMULATION_RUNS, SINGLE_AGENT_TYPE, SELECTED_SINGLE_AGENT_TYPE, USE_SOCIAL_CAPITAL, β, DEMAND_CURVES, AVAILABILITY_CURVE, PERCENTAGE_OF_AGENTS_TO_EVOLVE, AGENT_TYPES_ARRAY, SATISFACTION_CURVE);
                System.out.println("********** 1 / 2 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = true;
                runSimulationSet(seed, FOLDER_NAME, PYTHON_EXE, PYTHON_PATH, POPULATION_SIZE, SLOTS_PER_AGENT, UNIQUE_TIME_SLOTS, ADDITIONAL_DAYS, SIMULATION_RUNS, SINGLE_AGENT_TYPE, SELECTED_SINGLE_AGENT_TYPE, USE_SOCIAL_CAPITAL, β, DEMAND_CURVES, AVAILABILITY_CURVE, PERCENTAGE_OF_AGENTS_TO_EVOLVE, AGENT_TYPES_ARRAY, SATISFACTION_CURVE);
                System.out.println("********** 2 / 2 ENVIRONMENT VERSIONS COMPLETE **********");
                break;
            case 2:
                // As above but also test single agent type populations for reference.
                USE_SOCIAL_CAPITAL = false;
                SINGLE_AGENT_TYPE = true;
                SELECTED_SINGLE_AGENT_TYPE = SELFISH;
                runSimulationSet(seed, FOLDER_NAME, PYTHON_EXE, PYTHON_PATH, POPULATION_SIZE, SLOTS_PER_AGENT, UNIQUE_TIME_SLOTS, ADDITIONAL_DAYS, SIMULATION_RUNS, SINGLE_AGENT_TYPE, SELECTED_SINGLE_AGENT_TYPE, USE_SOCIAL_CAPITAL, β, DEMAND_CURVES, AVAILABILITY_CURVE, PERCENTAGE_OF_AGENTS_TO_EVOLVE, AGENT_TYPES_ARRAY, SATISFACTION_CURVE);
                System.out.println("********** 1 / 5 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = false;
                SINGLE_AGENT_TYPE = true;
                SELECTED_SINGLE_AGENT_TYPE = SOCIAL;
                runSimulationSet(seed, FOLDER_NAME, PYTHON_EXE, PYTHON_PATH, POPULATION_SIZE, SLOTS_PER_AGENT, UNIQUE_TIME_SLOTS, ADDITIONAL_DAYS, SIMULATION_RUNS, SINGLE_AGENT_TYPE, SELECTED_SINGLE_AGENT_TYPE, USE_SOCIAL_CAPITAL, β, DEMAND_CURVES, AVAILABILITY_CURVE, PERCENTAGE_OF_AGENTS_TO_EVOLVE, AGENT_TYPES_ARRAY, SATISFACTION_CURVE);
                System.out.println("********** 2 / 5 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = true;
                SINGLE_AGENT_TYPE = true;
                SELECTED_SINGLE_AGENT_TYPE = SOCIAL;
                runSimulationSet(seed, FOLDER_NAME, PYTHON_EXE, PYTHON_PATH, POPULATION_SIZE, SLOTS_PER_AGENT, UNIQUE_TIME_SLOTS, ADDITIONAL_DAYS, SIMULATION_RUNS, SINGLE_AGENT_TYPE, SELECTED_SINGLE_AGENT_TYPE, USE_SOCIAL_CAPITAL, β, DEMAND_CURVES, AVAILABILITY_CURVE, PERCENTAGE_OF_AGENTS_TO_EVOLVE, AGENT_TYPES_ARRAY, SATISFACTION_CURVE);
                System.out.println("********** 3 / 5 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = false;
                SINGLE_AGENT_TYPE = false;
                runSimulationSet(seed, FOLDER_NAME, PYTHON_EXE, PYTHON_PATH, POPULATION_SIZE, SLOTS_PER_AGENT, UNIQUE_TIME_SLOTS, ADDITIONAL_DAYS, SIMULATION_RUNS, SINGLE_AGENT_TYPE, SELECTED_SINGLE_AGENT_TYPE, USE_SOCIAL_CAPITAL, β, DEMAND_CURVES, AVAILABILITY_CURVE, PERCENTAGE_OF_AGENTS_TO_EVOLVE, AGENT_TYPES_ARRAY, SATISFACTION_CURVE);
                System.out.println("********** 4 / 5 ENVIRONMENT VERSIONS COMPLETE **********");

                USE_SOCIAL_CAPITAL = true;
                SINGLE_AGENT_TYPE = false;
                runSimulationSet(seed, FOLDER_NAME, PYTHON_EXE, PYTHON_PATH, POPULATION_SIZE, SLOTS_PER_AGENT, UNIQUE_TIME_SLOTS, ADDITIONAL_DAYS, SIMULATION_RUNS, SINGLE_AGENT_TYPE, SELECTED_SINGLE_AGENT_TYPE, USE_SOCIAL_CAPITAL, β, DEMAND_CURVES, AVAILABILITY_CURVE, PERCENTAGE_OF_AGENTS_TO_EVOLVE, AGENT_TYPES_ARRAY, SATISFACTION_CURVE);
                System.out.println("********** 5 / 5 ENVIRONMENT VERSIONS COMPLETE **********");
                break;
            default:
                // Run only the set of parameters defined by the user.
                runSimulationSet(seed, FOLDER_NAME, PYTHON_EXE, PYTHON_PATH, POPULATION_SIZE, SLOTS_PER_AGENT, UNIQUE_TIME_SLOTS, ADDITIONAL_DAYS, SIMULATION_RUNS, SINGLE_AGENT_TYPE, SELECTED_SINGLE_AGENT_TYPE, USE_SOCIAL_CAPITAL, β, DEMAND_CURVES, AVAILABILITY_CURVE, PERCENTAGE_OF_AGENTS_TO_EVOLVE, AGENT_TYPES_ARRAY, SATISFACTION_CURVE);
        }
    }
    
    /**
     * Runs a set of simulations with the parameters given by the user in the config.properties file.
     *
     * @param folderName String representing the output destination folder, used to organise output
     *                   data.
     * @param environmentTag String detailing specifics about the simulation environment.
     * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used
     *                     the agents are split equally between the curves.
     * @param availabilityCurve Integer array of energy availability used by the simulation.
     * @param socialCapital Boolean value that determines whether or not social agents will utilise
     *                      social capital.
     * @param simulationRuns Integer value representing the number of simulations to be ran and
     *                       averaged.
     * @param days Integer value representing the number of days to be simulated.
     * @param populationSize Integer value representing the size of the initial agent population.
     * @param uniqueTimeSlots Integer value representing the number of unique time-slots available in
     *                        the simulation.
     * @param slotsPerAgent Integer value representing the number of time-slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy
     *                               will change at the end of each day.
     * @param agentTypes Integer array containing the agent types that the simulation will begin with.
     *                   The same type can exist multiple times in the array where more agents of one
     *                   type are required.
     * @param singleAgentType Boolean value specifying whether only a single agent type should exist,
     *                        used for establishing baseline results.
     * @param selectedSingleAgentType Integer value representing the single agent type to be modelled
     *                                when singleAgentType is true.
     * @param pythonExe String representing the system path to python environment executable.
     * @param pythonPath String representing the system path to the python data visualiser.
     * @param β Double value that increases the the chance that agents will change their strategy.
     * @param demandCurves
     * @param availabilityCurve
     * @param evolutionPercentage integer value that sets the percentage of agents that have a chance to evolve each day.
     * @param satisfactionCurve Double array that determines the satisfaction fall off for slots received close to the agents preferences.
     * @exception IOException On input error.
     * @see IOException
     */
    private static void runSimulationSet(
        long seed, 
        String folderName, 
        String pythonExe, 
        String pythonPath,
        int populationSize,
        int slotsPerAgent,
        int uniqueTimeSlots,
        int additionalDays,
        int simulationRuns,
        boolean singleAgentType,
        int selectedSingleAgentType,
        boolean useSocialCapital,
        double β,
        double[][] demandCurves,
        int[] availabilityCurve,
        int evolutionPercentage,
        int[] agentTypeArray,
        double[] satisfactionCurve
        ) throws IOException {
        // Set the simulations initial random seed.
        random.setSeed(seed);

        // Create a directory to store the data output by all simulations being run.
        String dataOutputFolder = folderName + "/useSC_" + useSocialCapital + "_AType_";
        if (!singleAgentType) {
            dataOutputFolder += "mixed";
        } else {
            dataOutputFolder += Inflect.getHumanReadableAgentType(selectedSingleAgentType);
        }

        Path dataOutputPath = Paths.get(dataOutputFolder);
        Files.createDirectories(dataOutputPath);

        // Percentage of learning agents converted to actual number of agents that can learn each day.
        float onePercent = populationSize / 100.0f;
        int learningAgents = Math.round(onePercent * evolutionPercentage);

        /*
         * The arena is the environment in which all simulations take place.
         *
         * @param folderName String representing the output destination folder, used to organise output
         *                   data.
         * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used
         *                     the agents are split equally between the curves.
         * @param availabilityCurve Integer array of energy availability used by the simulation.
         * @param socialCapital Boolean value that determines whether or not social agents will utilise
         *                      social capital.
         * @param simulationRuns Integer value representing the number of simulations to be ran and
         *                       averaged.
         * @param days Integer value representing the number of days to be simulated.
         * @param populationSize Integer value representing the size of the initial agent population.
         * @param uniqueTimeSlots Integer value representing the number of unique time-slots available in
         *                        the simulation.
         * @param uniqueTimeSlots Integer value representing the number of unique time-slots available in
                                  the simulation.
         * @param slotsPerAgent Integer value representing the number of time-slots each agent requires.
         * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy
         *                               will change at the end of each day.
         * @param agentTypes Integer array containing the agent types that the simulation will begin with.
         *                   The same type can exist multiple times in the array where more agents of one
         *                   type are required.
         * @param singleAgentType Boolean value specifying whether only a single agent type should exist,
         *                        used for establishing baseline results.
         * @param selectedSingleAgentType Integer value representing the single agent type to be modelled
         *                                when singleAgentType is true.
         * @param pythonExe String representing the system path to python environment executable.
         * @param pythonPath String representing the system path to the python data visualiser.
         * @param β Double value that increases the the chance that agents will change their strategy.
         * @param satisfactionCurve Double array that determines the satisfaction fall off for slots received close to the agents preferences.
         * @exception IOException On input error.
         * @see IOException
         */
        new ArenaEnvironment(
                dataOutputFolder,
                demandCurves,
                availabilityCurve,
                useSocialCapital,
                simulationRuns,
                additionalDays,
                populationSize,
                uniqueTimeSlots,
                slotsPerAgent,
                learningAgents,
                agentTypeArray,
                singleAgentType,
                selectedSingleAgentType,
                pythonExe,
                pythonPath,
                β,
                satisfactionCurve
        );
    }
    
    // Used for formatting availability curve preferences from the config.properties file.
    public static int[] StringToIntArray(String input) {
        // Split the input string by comma
        String[] numberStrings = input.split(",");
        
        // Initialize an integer array to store the result
        int[] result = new int[numberStrings.length];
        
        for (int i = 0; i < numberStrings.length; i++) {
            // Parse each element into an integer
            result[i] = Integer.parseInt(numberStrings[i]);
        }

        return result;
    }

    // Used for formatting satisfaction curve preferences from the config.properties file.
    public static double[] StringToDoubleArray(String input) {
        // Split the input string by comma
        String[] numberStrings = input.split(",");
        
        // Initialize an double array to store the result
        double[] result = new double[numberStrings.length];
        
        for (int i = 0; i < numberStrings.length; i++) {
            // Parse each element into an double
            result[i] = Double.parseDouble(numberStrings[i]);
        }

        return result;
    }

    // Used for formatting demand curve preferences from the config.properties file.
    public static double[][] StringToDouble2DArray(String input) {
        // Split the input string into sets using "||" as the delimiter
        String[] sets = input.split("\\|\\|");

        // Initialize a 2D double array to store the result
        double[][] result = new double[sets.length][];

        for (int i = 0; i < sets.length; i++) {
            // Split each set by comma and convert it to a double array
            String[] numberStrings = sets[i].split(",");
            result[i] = new double[numberStrings.length];

            for (int j = 0; j < numberStrings.length; j++) {
                // Parse each element into a double
                result[i][j] = Double.parseDouble(numberStrings[j]);
            }
        }

        return result;
    }

    // Used for formatting starting type ratio preferences from the config.properties file.
    public static int[] RatioToIntegerArray(String input) {
        // Split the input string by colon
        String[] ratioParts = input.split(":");
        int a = Integer.parseInt(ratioParts[0]);
        int b = Integer.parseInt(ratioParts[1]);

        int[] result = new int[a + b];

        for (int i = 0; i < a; i++) {
            result[i] = SELFISH;
        }

        for (int i = a; i < a + b; i++) {
            result[i] = SOCIAL;
        }

        return result;
    }
}
