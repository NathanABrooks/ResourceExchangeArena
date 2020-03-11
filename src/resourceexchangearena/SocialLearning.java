package resourceexchangearena;

import java.util.ArrayList;

class SocialLearning {

    /**
     * To facilitate social learning, for the number of the agents who are able to consider changing their strategy,
     * an Agent is selected at random, and then a second agent is selected to be observed. The first agent selected
     * checks whether their performance was weaker than the agent observed, if so they have a chance to copy the
     * strategy used by the observed agent in the previous day, with the likelihood of copying their strategy
     * proportional to the difference between their individual satisfactions.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy may change at
     *                               the end of each day.
     */
    SocialLearning(ArrayList<Agent> agents, int slotsPerAgent, int numberOfAgentsToEvolve) {
        // Generate array containing each agents ID, current satisfaction, and type.
        int[][] previousResults = new int[agents.size()][3];

        // List of all agents that haven't yet been selected for social learning.
        ArrayList<Integer> unselectedAgents = new ArrayList<>();

        for (Agent a : agents) {
            previousResults[a.agentID - 1][0] = a.agentID;
            previousResults[a.agentID - 1][1] =
                    (int) (Math.round(a.calculateSatisfaction(null) * slotsPerAgent));
            previousResults[a.agentID - 1][2] = a.getAgentType();
            unselectedAgents.add(a.agentID);
        }

        for (int i = 0; i < numberOfAgentsToEvolve; i++) {
            Agent agent = agents.get(ResourceExchangeArena.random.nextInt(unselectedAgents.size()));
            int[] agentToCopy = previousResults[ResourceExchangeArena.random.nextInt(agents.size())];

            // Ensure the agent altering its strategy doesnt copy itself.
            while (agent.agentID == agentToCopy[0]) {
                agentToCopy = previousResults[ResourceExchangeArena.random.nextInt(agents.size())];
            }

            // Copy the observed agents strategy if it is better than its own, with likelihood dependent on the
            // difference between the agents satisfaction and the observed satisfaction.
            double agentSatisfaction = agent.calculateSatisfaction(null);
            double observedSatisfaction = (double) agentToCopy[1] / 4;
            if (agentSatisfaction < observedSatisfaction) {
                double difference = observedSatisfaction - agentSatisfaction;
                double threshold = ResourceExchangeArena.random.nextDouble();
                if (difference > threshold) {
                    agent.setType(agentToCopy[2]);
                }
            }
            unselectedAgents.remove(Integer.valueOf(agent.agentID));
        }
    }
}
