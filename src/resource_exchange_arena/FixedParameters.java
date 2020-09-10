package resource_exchange_arena;

abstract class FixedParameters {
    // Constants representing the available agent types for the simulation.
    static final int SELFISH = 1;
    static final int SOCIAL = 2;
    static final int[] ALL_AGENT_TYPES = {SELFISH, SOCIAL};

    // Constant parameters for all simulations, this version of the system is not designed for these to be modified.
    static final int POPULATION_SIZE = 96;
    static final int MAXIMUM_PEAK_CONSUMPTION = 16;
    static final int UNIQUE_TIME_SLOTS = 24;
    static final int SLOTS_PER_AGENT = 4;
}
