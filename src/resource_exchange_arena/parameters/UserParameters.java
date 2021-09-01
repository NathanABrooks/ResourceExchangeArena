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
    public static int SELECTED_SINGLE_AGENT_TYPE = SELFISH;

    // Specify whether social capital  should be used by the social agents.
    public static boolean USE_SOCIAL_CAPITAL = false;

    // Days that will have the Agents average satisfaction over the course of the day, and satisfaction distribution at
    // the end of the day visualised.
    public static final double[] DEMAND_CURVE = {375.4,374.6,350.2,334.5,305.0,285.5,277.2,275.1,273.1,268.1,264.4,262.4,255.3,251.0,244.3,242.1,237.1,236.4,230.3,230.7,223.6,222.5,232.9,241.9,251.2,250.6,239.9,231.6,228.2,220.0,224.8,231.1,231.8,232.1,248.1,253.3,262.5,276.8,283.1,289.0,320.0,361.1,394.1,415.2,432.8,449.4,459.5,480.8,496.4,498.9,506.7,497.4,491.4,497.0,494.0,494.8,495.1,498.0,490.6,488.5,482.0,485.3,480.0,476.2,464.6,468.2,468.4,473.6,482.2,471.9,471.2,476.5,490.3,498.8,490.7,496.9,496.1,490.4,474.6,475.4,472.7,464.2,462.4,458.0,449.8,447.0,440.9,445.5,451.3,455.5,463.9,462.1,470.7,472.6,483.3,489.9,502.5,528.9,543.1,568.6,593.5,610.7,643.0,655.4,664.9,683.1,696.4,711.7,716.5,725.5,712.5,707.1,714.1,709.9,713.5,720.1,705.0,709.0,696.0,701.3,691.9,689.3,681.0,675.8,664.7,656.6,652.1,646.8,631.8,622.3,606.0,594.2,579.6,571.0,553.3,538.4,538.1,518.4,500.0,473.9,445.3,429.2,423.7,406.1};

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
    public static int COMPARISON_LEVEL = 1;
}
