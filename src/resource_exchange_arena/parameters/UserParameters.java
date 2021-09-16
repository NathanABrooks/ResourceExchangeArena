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
            "/home/nathan/python_environments/ResourceExchangeArena/bin/python";

    // Location of data visualiser python scripts on your machine.
    public static final String PYTHON_PATH =
            "/home/nathan/code/ResourceExchangeArena/src/data_analysis/";
    // Example: "/home/nathan/IdeaProjects/ResourceExchangeArena/src/data_analysis/"
    
    // Alter the population size.
    public static final int POPULATION_SIZE = 48;

    // Alter the number of timeslots that each agent requests each day.
    public static final int SLOTS_PER_AGENT = 4;

    // Alter the length of time to be simulated.
    public static final int DAYS = 500;
    // Example: "500"

    // Increase the number of simulation runs for more consistent results.
    public static final int SIMULATION_RUNS = 50;
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
    public static final double[][] DEMAND_CURVES = {{61.0,64.7,48.6,40.9,36.8,30.2,27.3,23.9,25.2,18.7,15.4,15.6,12.9,10.7,8.5,8.4,9.0,5.7,6.6,6.2,2.3,6.5,3.6,5.5,2.3,3.2,3.5,3.4,3.1,2.2,2.9,2.2,3.4,3.9,5.6,9.1,7.6,6.4,9.8,19.8,30.7,34.5,38.9,45.9,45.5,63.4,73.6,83.7,88.2,96.5,95.6,103.0,112.1,122.7,120.5,117.5,117.8,115.9,118.0,118.8,117.9,126.4,113.8,116.4,106.4,113.9,119.7,114.7,115.4,113.5,116.7,110.9,115.2,118.0,110.3,106.3,100.7,101.3,109.8,112.3,104.3,99.8,98.6,96.0,86.8,87.9,82.9,88.7,88.4,85.7,87.7,88.6,101.7,106.1,105.4,90.3,90.8,89.0,87.6,84.5,82.5,79.8,103.2,100.8,86.4,79.6,82.8,87.7,91.4,98.6,97.3,89.1,84.1,87.8,103.4,120.1,119.2,124.2,119.4,111.7,108.4,105.2,100.4,106.1,110.5,112.0,110.7,107.5,100.3,84.0,86.8,79.6,79.3,75.3,78.9,83.6,89.2,87.8,86.3,80.8,72.9,75.0,70.3,66.0},
                                                    {8.1,10.2,7.8,7.0,13.3,14.9,12.4,11.0,10.7,8.8,6.7,7.8,10.0,8.2,20.3,36.0,29.9,26.8,21.6,46.0,23.7,19.5,13.7,7.5,6.1,5.6,7.3,8.0,5.3,4.0,3.4,4.5,2.8,6.6,8.0,3.2,8.1,7.2,3.7,5.2,7.9,18.0,16.8,22.4,19.3,18.3,17.2,13.8,21.3,22.9,19.0,32.4,31.6,25.7,23.0,22.6,29.9,30.4,27.3,33.7,27.2,29.0,28.1,31.7,35.5,27.9,21.1,22.3,24.0,21.4,16.7,14.4,21.1,20.3,21.6,22.4,16.7,20.3,12.1,8.6,19.7,24.3,20.3,17.4,13.2,21.3,21.6,16.7,14.4,18.7,20.9,16.6,10.0,7.2,7.3,9.1,10.8,14.3,19.2,18.4,25.8,16.3,14.0,18.2,12.7,17.2,20.6,17.8,24.2,30.3,32.2,24.4,15.6,15.9,17.5,19.5,29.5,24.6,16.3,26.0,20.8,19.2,21.1,27.6,21.5,27.0,24.9,37.4,25.7,29.0,21.1,14.1,21.0,17.6,21.1,15.0,9.7,6.7,9.3,6.8,11.0,11.2,11.2,6.9}};

    // In order to schedule multiple parameter combinations when performing a parameter sweep, add more items to
    // the following arrays. All possible combinations will be simulated.
    // ################################################################################################################
    // Number of exchange rounds per day.
    public static final int[] EXCHANGES_ARRAY = {1,50,100,150,200};
    // Example: "{1,50,100,150,200};"

    // Percentage of agents that will evolve their strategy per day.
    public static final int[] PERCENTAGE_OF_AGENTS_TO_EVOLVE_ARRAY = {0,50,100};
    // Example: "{0,50,100}"

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
    public static int COMPARISON_LEVEL = 2;
}
