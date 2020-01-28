package resourceexchangearena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

class SocialLearning {
    static void Evolve(ArrayList<Agent> agents, int slotsPerAgent) {
        // EVOLUTION
        int[][] tempAgentFitnessScores = new int[agents.size()][3];
        for (Agent a : agents) {
            tempAgentFitnessScores[a.agentID - 1][0] = a.agentID;
            tempAgentFitnessScores[a.agentID - 1][1] = (int) (Math.round(a.calculateSatisfaction(null) * slotsPerAgent));
            tempAgentFitnessScores[a.agentID - 1][2] = a.getAgentType();
        }
        int[][] agentFitnessScores = tempAgentFitnessScores.clone();
        Arrays.sort(agentFitnessScores, Comparator.comparingInt(o -> o[1]));

        int totalFitness = 0;
        for (int[] agentFitnessScore : agentFitnessScores) {
            totalFitness = totalFitness + agentFitnessScore[1];
        }

        int deathsPerRound = 10;
        ArrayList<Integer> agentsToReplace = new ArrayList<>();

        int lowestFitnessPresent = agentFitnessScores[0][1];
        while (agentsToReplace.size() < deathsPerRound) {
            ArrayList<Integer> currentFitnessAgents = new ArrayList<>();
            for (int[] agentFitnessScore : agentFitnessScores) {
                if (agentFitnessScore[1] == lowestFitnessPresent) {
                    currentFitnessAgents.add(agentFitnessScore[0]);
                }
            }
            int remainingDeaths = deathsPerRound - agentsToReplace.size();
            if (currentFitnessAgents.size() <= remainingDeaths) {
                agentsToReplace.addAll(currentFitnessAgents);
            } else {
                for (int k = 0; k < remainingDeaths; k++) {
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
