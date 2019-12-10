package resourceexchangearena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class SimulationRun {
    // List of all the Agents that are part of the current simulation.
    ArrayList<Agent> agents = new ArrayList<>();

    SimulationRun(int i, FileWriter averageCSVWriter, FileWriter individualCSVWriter, int numberOfEachAgentType, int[] agentTypes, ArrayList<Integer> uniqueAgentTypes, ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions, ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions, int[] daysOfInterest, ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions) throws IOException {
        // Clear the list of agents before a simulation begins.
        if (!agents.isEmpty()) {
            agents.clear();
        }

        int agentType = 0;
        int agentsOfThisType = 0;

        // Create the Agents for the simulation.
        for (int j = 1; j <= ResourceExchangeArena.POPULATION_SIZE; j++) {
            if (agentsOfThisType >= numberOfEachAgentType) {
                agentType++;
                agentsOfThisType = 0;
            }
            if (agentType < agentTypes.length) {
                new Agent(j, agentTypes[agentType], agents);
                agentsOfThisType++;
            } else {
                j--;
                agentType = 0;
                agentsOfThisType = 0;
                numberOfEachAgentType = 1;
            }
        }

        // Increment the simulations seed each run.
        ArenaEnvironment.seed++;
        ResourceExchangeArena.random.setSeed(ArenaEnvironment.seed);

        // Initialise each Agents relations with each other Agent if trading Agents are being simulated.
        for (Agent a : agents) {
            a.initializeFavoursStore(agents);
        }


        for (int j = 1; j <= ResourceExchangeArena.DAYS; j++) {
            Day currentDay = new Day(i, j, averageCSVWriter, individualCSVWriter, agents, uniqueAgentTypes, endOfDayAverageSatisfactions, endOfDayPopulationDistributions, daysOfInterest, endOfRoundAverageSatisfactions);
        }
    }
}
