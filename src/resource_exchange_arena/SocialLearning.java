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
        ArrayList<Agent> previousResults = new ArrayList<>();
        for(Agent a: agents) {
            Agent newA = new Agent(a.agentID, a.getAgentType(), a.usesSocialCapital(), a.madeInteraction(), a.numberOfTimeSlotsWanted(), a.publishRequestedTimeSlots(), a.publishAllocatedTimeSlots(), a.getFavoursOwed(), a.getFavoursGiven(), a.getExchangeRequestReceived(), a.getExchangeRequestApproved());
            previousResults.add(newA);
        }

        // Copy agents to store all agents that haven't yet been selected for social learning.
        ArrayList<Agent> unselectedAgents = new ArrayList<>(agents);

        for (int i = 0; i < numberOfAgentsToEvolve; i++) {
            // Assign the selected agent another agent to 'retrospectively' observe.
            Agent observedAgent = previousResults.get(ResourceExchangeArena.random.nextInt(previousResults.size()));

            // Select an agent to learn.
            int learningAgentID = unselectedAgents.get(ResourceExchangeArena.random.nextInt(unselectedAgents.size())).agentID;
            Agent learningAgent = observedAgent; // This is just for initialisation (not optimal as not used)
            for (Agent a : agents) {
                if (a.agentID == learningAgentID) {
                    learningAgent = a;
                    break;
                }
            }
            unselectedAgents.remove(learningAgent);

            // Ensure the agent altering its strategy doesnt copy itself.
            while (learningAgent.agentID == observedAgent.agentID) {
                observedAgent = previousResults.get(ResourceExchangeArena.random.nextInt(previousResults.size()));
            }

            // Copy the observed agents strategy if it is better than its own, with likelihood dependent on the
            // difference between the agents satisfaction and the observed satisfaction.
            double learningAgentSatisfaction = learningAgent.calculateSatisfaction(null);
            double observedAgentSatisfaction = observedAgent.calculateSatisfaction(null);
            if (Math.round(learningAgentSatisfaction * slotsPerAgent) < Math.round(observedAgentSatisfaction * slotsPerAgent)) {
                double difference = observedAgentSatisfaction - learningAgentSatisfaction;
                double threshold = ResourceExchangeArena.random.nextDouble();
                if (difference > threshold) {
                    learningAgent.setType(observedAgent.getAgentType());
                }
            }
        }
    }
}
