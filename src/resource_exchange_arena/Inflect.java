package resource_exchange_arena;

class Inflect {

  /**
   * Takes an agentType and converts it from its {@link Integer} format to a descriptive {@link String} name to organise the data output by the simulation.
   *
   * @param agentType {@link Integer} value representing the type of the {@link Agent} as a potentially unclear number.
   * @return The given agentType as a descriptive {@link String}.
   */
  static String getHumanReadableAgentType(int agentType) {
    String name = switch (agentType) {
      case ResourceExchangeArena.SOCIAL -> "Social";
      case ResourceExchangeArena.SELFISH -> "Selfish";
      default ->
        // If a human-readable name doesn't exist, return the {@link Integer} agentType as a string.
              String.valueOf(agentType);
    };
    // Names match types specified by constant integers for the ExchangeArena.
    return name;
  }
}
