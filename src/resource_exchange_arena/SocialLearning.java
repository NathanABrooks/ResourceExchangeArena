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
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy may change at the
     *                               end of each day.
     */
    SocialLearning(ArrayList<Agent> agents, int slotsPerAgent, int numberOfAgentsToEvolve) {
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

        // Mutation
        // for (Agent a : agents) {
        //     double mutation = ResourceExchangeArena.random.nextDouble();
        //     if (mutation < 0.01) {
        //         if (a.getAgentType() == ResourceExchangeArena.SOCIAL) {
        //             a.setType(ResourceExchangeArena.SELFISH);
        //         } else if (a.getAgentType() == ResourceExchangeArena.SELFISH) {
        //             a.setType(ResourceExchangeArena.SOCIAL);
        //         }
        //         unselectedAgents.remove(a);
        //     }
        // }

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
                    double beta = 1.0;
                    double learningChance = 1 / (1 + (Math.exp(-beta * difference)));
                    double normalisedLearningChance = (learningChance * 2) - 1;

                    double threshold = ResourceExchangeArena.random.nextDouble();

                    if (normalisedLearningChance > threshold) {
                        int newType = (int) Math.round(previousPerformances[observedPerformance][0]);
                        learningAgent.setType(newType);
                    }

                    // if (newType == ResourceExchangeArena.SELFISH) {
                    //     learningAgent.initializeFavoursStore(agents);

                    //     for (Agent a: agents) {
                    //         for (ArrayList<Integer> favours : a.getFavoursGiven()) {
                    //             if (favours.get(0).equals(learningAgent.agentID)) {
                    //                 favours.set(1,0);
                    //                 break;
                    //             }
                    //         }
                    //         for (ArrayList<Integer> favours : a.getFavoursOwed()) {
                    //             if (favours.get(0).equals(learningAgent.agentID)) {
                    //                 favours.set(1,0);
                    //                 break;
                    //             }
                    //         }
                    //     }
                    // }
                }
            }
            unselectedAgents.remove(learningAgent);
        }
    }
}
