package resourceexchangearena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class SocialLearning {

    /**
     * Uses roulette wheel selection to allow each agent in the population to adapt its strategy based on the success
     * of it's peers.
     *
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change at the
     *                               end of each day.
     */
    SocialLearning(ArrayList<Agent> agents, int slotsPerAgent, int numberOfAgentsToEvolve) {

        // NOTE THAT WHILE ROULETTE WHEEL SELECTION IS USED, NO MUTATION HAS BEEN IMPLEMENTED.

        // First, all agents fitness scores are calculated, and added to a multi-dimensional array that associates
        // each individual with its fitness and type. This array is cloned so that it can be re-ordered according to
        // each agents fitness.
        int[][] tempAgentFitnessScores = new int[agents.size()][3];
        for (Agent a : agents) {
            tempAgentFitnessScores[a.agentID - 1][0] = a.agentID;
            tempAgentFitnessScores[a.agentID - 1][1] =
                    (int) (Math.round(a.calculateSatisfaction(null) * slotsPerAgent));
            tempAgentFitnessScores[a.agentID - 1][2] = a.getAgentType();
        }
        int[][] agentFitnessScores = tempAgentFitnessScores.clone();
        Arrays.sort(agentFitnessScores, Comparator.comparingInt(o -> o[1]));

        // The total fitness in the system is calculated by summing each agent's fitness.
        int totalFitness = 0;
        for (int[] agentFitnessScore : agentFitnessScores) {
            totalFitness = totalFitness + agentFitnessScore[1];
        }

        // The lowest fitness individuals are gathered, if all individuals with the lowest fitness have been identified
        // however the number of agents to evolve hasn't been met, agents from the next lowest fitness will be selected
        // at random until the quota has been met.
        ArrayList<Integer> agentsToReplace = new ArrayList<>();
        int lowestFitnessPresent = agentFitnessScores[0][1];
        while (agentsToReplace.size() < numberOfAgentsToEvolve) {
            ArrayList<Integer> currentFitnessAgents = new ArrayList<>();
            for (int[] agentFitnessScore : agentFitnessScores) {
                if (agentFitnessScore[1] == lowestFitnessPresent) {
                    currentFitnessAgents.add(agentFitnessScore[0]);
                }
            }
            int remainingEvolutions = numberOfAgentsToEvolve - agentsToReplace.size();
            if (currentFitnessAgents.size() <= remainingEvolutions) {
                agentsToReplace.addAll(currentFitnessAgents);
            } else {
                for (int minimumFitAgents = 0; minimumFitAgents < remainingEvolutions; minimumFitAgents++) {
                    int selector = ResourceExchangeArena.random.nextInt(currentFitnessAgents.size());
                    int selectedAgent = currentFitnessAgents.get(selector);
                    agentsToReplace.add(selectedAgent);
                    currentFitnessAgents.remove(Integer.valueOf(selectedAgent));
                    if (currentFitnessAgents.size() == 0) {
                        break;
                    }
                }
            }
            lowestFitnessPresent++;
        }

        // The identified low fitness individuals spin the roulette wheel and adopt the social strategy of the agent
        // that controls the segment of the wheel which they land on.
        for (Integer agentID : agentsToReplace) {
            for (Agent a : agents) {
                if (a.agentID == agentID) {
                    int rouletteOutcome = ResourceExchangeArena.random.nextInt(totalFitness) + 1;
                    int rouletteSegment = 0;
                    for (int[] agentFitnessScore : agentFitnessScores) {
                        rouletteSegment = rouletteSegment + agentFitnessScore[1];
                        if (rouletteSegment >= rouletteOutcome) {
                            a.setType(agentFitnessScore[2]);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }
}
