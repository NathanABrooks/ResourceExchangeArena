package resource_exchange_arena;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

class SocialLearning {
    
    /**
     * To facilitate {@link SocialLearning}, for the number of the {@link Agent}s who are able to consider changing their strategy,
     * an {@link Agent} is selected at random, and then a second {@link Agent} is selected to be observed. The first {@link Agent} selected
     * checks whether their performance was weaker than the {@link Agent} observed, if so they have a chance to copy the
     * strategy used by the observed {@link Agent} in the previous {@link Day}, with the likelihood of copying their strategy
     * proportional to the difference between their individual satisfactions.
     *
     * @param agents {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     * @param slotsPerAgent {@link Integer} value representing the number of time slots each {@link Agent} requires.
     * @param numberOfAgentsToEvolve {@link Integer} value representing the number of {@link Agent}s whose strategy may change at the end of each {@link Day}.
     */
    SocialLearning(@NotNull ArrayList<Agent> agents, int slotsPerAgent, int numberOfAgentsToEvolve) {
        // Copy agents to store previous results, this needs to be a deep copy and so a new cloned agent is made.
        int totalAgents = agents.size();
        double[][] previousPerformances = new double[totalAgents][3];
        for(Agent a: agents) {
            double type = a.getAgentType();
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

            // Ensure the agent altering its strategy doesn't copy itself.
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
                    double beta = 1;
                    double learningChance = 1 / (1 + (Math.exp(-beta * difference)));
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
