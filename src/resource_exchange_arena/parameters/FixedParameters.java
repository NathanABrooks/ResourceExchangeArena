package resource_exchange_arena.parameters;

public abstract class FixedParameters {
    /**
     * Contains all system parameters that can't be changed by the user, including agent types simulated and parameters of
     * note to anyone working with or referencing this system.
     */

    // Constants representing the available agent types for the simulation.
    public static final int SELFISH = 1;
    public static final int SOCIAL = 2;
    public static final int[] ALL_AGENT_TYPES = {SELFISH, SOCIAL};

    // Constant parameters for all simulations, this version of the system is not designed for these to be modified.
    public static final int UNIQUE_TIME_SLOTS = 24;

    // In this version maximum peak consumption is calculated to be the number of agents * the number of slots requested per agent.
    // public static final int MAXIMUM_PEAK_CONSUMPTION = 16;
}
