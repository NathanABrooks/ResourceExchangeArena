package resource_exchange_arena;

import java.util.ArrayList;

import static java.lang.Math.sqrt;

class CalculateSatisfaction {
    /**
     * Takes all Agents individual satisfactions and calculates the average satisfaction of all Agents in the
     * simulation.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @return Double Returns the average satisfaction between 0 and 1 of all agents in the simulation.
     */
    static double averageAgentSatisfaction(ArrayList<Agent> agents) {
        ArrayList<Double> agentSatisfactions = new ArrayList<>();
        for (Agent a : agents) {
            agentSatisfactions.add(a.calculateSatisfaction(null));
        }
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }

    /**
     * Takes all Agents of a given types individual satisfactions and calculates the average satisfaction of the Agents
     * of that type.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param agentType The type for which to calculate the average satisfaction of all Agents of that type.
     * @return Double Returns the average satisfaction between 0 and 1 of all agents of the given type.
     */
    static double averageAgentSatisfaction(ArrayList<Agent> agents, int agentType) {
        ArrayList<Double> agentSatisfactions = new ArrayList<>();
        for (Agent a : agents) {
            if (a.getAgentType() == agentType) {
                agentSatisfactions.add(a.calculateSatisfaction(null));
            }
        }
        return agentSatisfactions.stream().mapToDouble(val -> val).average().orElse(0.0);
    }
    /**
     * Takes all Agents of a given types individual satisfactions and calculates the variance between the average
     * satisfaction of the Agents of that type.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param agentType The type for which to calculate the variance between the average satisfactions of all Agents of
     *                  that type.
     * @return Double Returns the variance between the average satisfactions of all agents of the given type.
     */
    static double averageSatisfactionStandardDeviation(ArrayList<Agent> agents, int agentType) {
        double sumDiffsSquared = 0.0;
        double averageSatisfaction = averageAgentSatisfaction(agents, agentType);
        int groupSize = 0;
        for (Agent a : agents) {
            if (a.getAgentType() == agentType) {
                double diff = a.calculateSatisfaction(null) - averageSatisfaction;
                diff *= diff;
                sumDiffsSquared += diff;
                groupSize++;
            }
        }
        double populationVariance = sumDiffsSquared / (double)(groupSize);
        return sqrt(populationVariance);
    }

    /**
     * Returns the optimum average satisfaction possible for all agents given the current requests and allocations in
     * the simulation.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @return Double Returns the highest possible average satisfaction between 0 and 1 of all agents in the simulation.
     */
    static double optimumAgentSatisfaction(ArrayList<Agent> agents) {
        ArrayList<Integer> allRequestedSlots = new ArrayList<>();
        ArrayList<Integer> allAllocatedSlots = new ArrayList<>();

        for (Agent a : agents) {
            allRequestedSlots.addAll(a.publishRequestedTimeSlots());
            allAllocatedSlots.addAll(a.publishAllocatedTimeSlots());
        }

        // Stores the number of slots that could potentially be fulfilled with perfect trading.
        double satisfiedSlots = 0;

        // Stores the total number of slots requested by all Agents.
        double totalSlots = allRequestedSlots.size();

        for (Integer slot : allRequestedSlots) {
            if (allAllocatedSlots.contains(slot)) {
                // For each request, if it has been allocated to any agent, increase the number of satisfied slots.
                satisfiedSlots++;

                // Remove the slot from the list of all allocated slots so no slots can be allocated twice.
                allAllocatedSlots.remove(slot);
            }
        }
        return satisfiedSlots / totalSlots;
    }
}
