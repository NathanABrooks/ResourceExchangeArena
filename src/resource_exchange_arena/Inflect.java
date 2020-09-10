package resource_exchange_arena;

class Inflect {
    /**
     * Takes an agentType and converts it from it's integer format to a descriptive string name to organise the data
     * output by the simulation.
     *
     * @param agentType Integer value representing the type of the agent as a potentially unclear number.
     * @return String Returns the given agentType as a descriptive string.
     */
    static String getHumanReadableAgentType(int agentType) {
        String name;
        // Names match types specified by constant integers for the ExchangeArena.
        switch(agentType) {
            case ResourceExchangeArena.SOCIAL:
                name = "Social";
                break;
            case ResourceExchangeArena.SELFISH:
                name = "Selfish";
                break;
            default:
                // If a human readable name doesnt exist, return the integer agentType as a string.
                name = String.valueOf(agentType);
        }
        return name;
    }
}
