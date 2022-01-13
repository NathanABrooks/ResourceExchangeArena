package resource_exchange_arena.parameters;

public abstract class UserParameters extends FixedParameters {
    /**
     * Contains all system parameters that can be changed by the user in order to experiment with various scenarios.
     */

    // The seed can be set to replicate previous simulations.
    public static long seed = System.currentTimeMillis();
    // Example: "1599767866160L"
    // Alternatively if no specific seed is required...
    // Example: "System.currentTimeMillis()"

    // Name of the folder that will contain the set of simulations currently being ran.
    public static final String FOLDER_NAME = "Set_" + seed;

    // Conda env. location.
    public static final String PYTHON_EXE =
            "/home/brooks/anaconda3/envs/rea/bin/python3";

    // Location of data visualiser python scripts on your machine.
    public static final String PYTHON_PATH =
            "/home/brooks/code/ResourceExchangeArena/src/data_analysis/";
    // Example: "/home/nathan/IdeaProjects/ResourceExchangeArena/src/data_analysis/"
    
    // Alter the population size.
    public static final int POPULATION_SIZE = 96;

    // Alter the number of timeslots that each agent requests each day.
    public static final int SLOTS_PER_AGENT = 4;

    // Alter the length of time to be simulated.
    public static final int DAYS = 500;
    // Example: "500"

    // Increase the number of simulation runs for more consistent results.
    public static final int SIMULATION_RUNS = 10;
    // Example: "50"

    // Days that will have the Agents average satisfaction over the course of the day, and satisfaction distribution at
    // the end of the day visualised.
    public static final int[] DAYS_OF_INTEREST = {1, 100, 200, 300, 400, 500};
    // Example: "{1, 100, 200, 300, 400, 500}"

    // Specify whether only a single agent type should exist in the simulation, used for establishing baseline results.
    public static boolean SINGLE_AGENT_TYPE = false;

    // Specify the single agent type to be simulated when 'SINGLE_AGENT_TYPE = true', e.g. 'SELFISH' or 'SOCIAL'.
    public static int SELECTED_SINGLE_AGENT_TYPE = SOCIAL;

    // Specify whether social capital  should be used by the social agents.
    public static boolean USE_SOCIAL_CAPITAL = true;

    // Arrays of demand used by the agents, when multiple curves are used the agents are split equally between the curves.
    // The arrays should have 1 value for each 10 minute segment of the day.
    public static final double[][] DEMAND_CURVES = {{31.6,32.9,27.7,24.6,24.0,19.5,16.4,14.6,15.2,11.5,9.3,8.6,7.4,6.4,7.4,10.4,9.9,7.4,6.7,10.1,6.2,7.8,7.0,7.6,5.5,6.7,5.2,4.7,4.9,4.2,5.3,4.3,3.8,4.7,5.5,6.0,7.0,5.4,6.7,13.2,17.9,21.2,23.8,28.7,28.8,36.4,41.3,49.0,55.0,58.1,59.5,66.2,72.7,78.3,79.6,78.7,81.4,82.2,84.0,86.0,82.9,87.4,83.1,88.1,85.1,83.1,82.1,82.9,81.2,81.9,81.2,79.2,77.8,79.1,77.6,76.7,73.8,71.9,72.6,73.0,70.4,69.9,71.7,67.6,65.7,68.2,63.8,68.0,67.1,68.5,68.3,67.7,70.1,67.7,68.2,65.2,64.1,65.8,68.4,64.4,61.0,57.4,64.2,66.1,57.3,55.9,55.7,58.9,61.6,64.4,63.3,61.3,58.4,62.8,70.3,74.3,75.2,76.7,71.9,73.5,73.9,69.3,67.9,68.2,69.2,65.5,61.1,63.3,60.3,53.6,51.3,44.4,45.1,42.3,44.1,44.9,46.3,42.1,41.8,39.7,36.3,37.1,35.7,32.1}};

    // The proportion of energy available for each hour of the of day.
    // The arrays should have 1 value for each 30 minute segment of the day.
    public static final int[] AVAILABILITY_CURVE = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};

    // In order to schedule multiple parameter combinations when performing a parameter sweep, add more items to
    // the following arrays. All possible combinations will be simulated.
    // ################################################################################################################
    // Number of exchange rounds per day.
    public static final int[] EXCHANGES_ARRAY = {1,50,100,150,200};
    // Example: "{1,50,100,150,200};"

    // Percentage of agents that will evolve their strategy per day.
    // CURRENTLY MUST HAVE MORE THAN 1 VALUE
    public static final int[] PERCENTAGE_OF_AGENTS_TO_EVOLVE_ARRAY = {0, 10, 25, 50,100};
    // Example: "{0,50,100}" {0, 10, 25, 50,100}

    // Ratio of starting agent types, i.e. {SELFISH, SELFISH, SOCIAL} would cause the simulation to start with two
    // selfish agents for each social agent.
    // Note that both types of agents need to exist, for testing with a single agent type set 'SINGLE_AGENT_TYPE'
    // to 'true' and set the 'SELECTED_SINGLE_AGENT_TYPE' as required.
    public static final int[][] AGENT_TYPES_ARRAY = {{SELFISH, SOCIAL}};
    // Example: "{{SELFISH, SOCIAL}}"
    // ################################################################################################################

    // Sets the level of comparisons that will be made:
    // Note that this overrides some of the previously set parameters and can result in much longer compute times.
    // 0 = Only the parameter combinations set will be made.
    // 1 = The above combinations will be ran both with and without social capital enabled so that the results can
    //     be compared.
    // 2 = The above combinations will be ran both with and without social capital enabled and with only selfish agents
    //     and with only social agents so that a baseline comparison can be made between the agents types.
    public static int COMPARISON_LEVEL = 0;
}
