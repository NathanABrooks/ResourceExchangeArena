package resourceexchangearena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class SimulationRun {

    // Each Simulation run with the same parameters runs as an isolated instance although data is recorded in a single location.
    SimulationRun(
            int populationSize,
            int numberOfEachAgentType,
            int days,
            int exchanges,
            int[] daysOfInterest,
            int maximumPeakConsumption,
            int uniqueTimeSlots,
            int slotsPerAgent,
            int[] agentTypes,
            ArrayList<Integer> uniqueAgentTypes,
            ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions,
            ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions,
            ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions,
            FileWriter averageCSVWriter,
            FileWriter individualCSVWriter
    ) throws IOException {

        // List of all the Agents that are part of the current simulation.
        ArrayList<Agent> agents = new ArrayList<>();

        int agentType = 0;
        int agentsOfThisType = 0;

        // Create the Agents for the simulation.
        for (int agentNumber = 1; agentNumber <= populationSize; agentNumber++) {
            if (agentsOfThisType >= numberOfEachAgentType) {
                agentType++;
                agentsOfThisType = 0;
            }
            if (agentType < agentTypes.length) {
                new Agent(agentNumber, agentTypes[agentType], slotsPerAgent, agents);
                agentsOfThisType++;
            } else {
                agentNumber--;
                agentType = 0;
                agentsOfThisType = 0;
                numberOfEachAgentType = 1;
            }
        }

        // Increment the simulations seed each run.
        ResourceExchangeArena.seed++;
        ResourceExchangeArena.random.setSeed(ResourceExchangeArena.seed);

        // Initialise each Agents relations with each other Agent if trading Agents are being simulated.
        for (Agent a : agents) {
            a.initializeFavoursStore(agents);
        }


        for (int day = 1; day <= days; day++) {
            new Day(
                    day,
                    maximumPeakConsumption,
                    uniqueTimeSlots,
                    exchanges,
                    slotsPerAgent,
                    averageCSVWriter,
                    individualCSVWriter,
                    agents,
                    uniqueAgentTypes,
                    endOfDayAverageSatisfactions,
                    endOfDayPopulationDistributions,
                    daysOfInterest,
                    endOfRoundAverageSatisfactions
            );
        }
    }
}
