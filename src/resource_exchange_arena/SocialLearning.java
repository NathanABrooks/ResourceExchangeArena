package resource_exchange_arena;

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
     * @param slotsPerAgent Integer value representing the number of time-slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy may change at the
     *                               end of each day.
     * @param β Double value that increases the the chance that agents will change their strategy.
     */
    SocialLearning(ArrayList<Agent> agents, int slotsPerAgent, int numberOfAgentsToEvolve, double β) {
        // Copy agents to store previous results, this needs to be a deep copy and so a new cloned agent is made.
        int totalAgents = agents.size();
        double[][] previousPerformances = new double[totalAgents][3];
        for(Agent a: agents) {
            double type = (double) a.getAgentType();
            double sat = a.calculateSatisfaction(null);

            previousPerformances[a.agentID-1][0] = type;
            previousPerformances[a.agentID-1][1] = sat;
        }

        // Copy agents to store all agents that haven't yet been selected for social learning.
        ArrayList<Agent> unselectedAgents = new ArrayList<>(agents);

        // Agents who mutated can't do social learning.
        int learningSize = numberOfAgentsToEvolve;
        if (unselectedAgents.size() < learningSize) {
            learningSize = unselectedAgents.size();
        }
        for (int i = 0; i < learningSize; i++) {
            // Assign the selected agent another agents performance to 'retrospectively' observe.
            int observedPerformance = ResourceExchangeArena.random.nextInt(totalAgents);

            // Select an agent to learn.
            Agent learningAgent = unselectedAgents.get(ResourceExchangeArena.random.nextInt(unselectedAgents.size()));

            // Ensure the agent altering its strategy doesnt copy itself.
            while (learningAgent.agentID == observedPerformance) {
                observedPerformance = ResourceExchangeArena.random.nextInt(totalAgents);
            }

            // Copy the observed agents strategy if it is better than its own, with likelihood dependent on the
            // difference between the agents satisfaction and the observed satisfaction.
            double learningAgentSatisfaction = learningAgent.calculateSatisfaction(null);
            double observedAgentSatisfaction = previousPerformances[observedPerformance][1];
            if (Math.round(learningAgentSatisfaction * slotsPerAgent) < Math.round(observedAgentSatisfaction * slotsPerAgent)) {
                double difference = observedAgentSatisfaction - learningAgentSatisfaction;
                if (difference >= 0) {
                    double learningChance = 1 / (1 + (Math.exp(-β * difference)));
                    double normalisedLearningChance = (learningChance * 2) - 1;

                    double threshold = ResourceExchangeArena.random.nextDouble();

                    if (normalisedLearningChance > threshold) {
                        int newType = (int) Math.round(previousPerformances[observedPerformance][0]);
                        learningAgent.setType(newType);
                    }
                }
            }
            unselectedAgents.remove(learningAgent);
        }
    }
}
