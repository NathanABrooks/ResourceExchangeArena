package resourceexchangearena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class SimulationRun {
    // List of all the Agents that are part of the current simulation.
    static ArrayList<Agent> agents = new ArrayList<>();

    static void simulateVariedAgents(int i, FileWriter averageCSVWriter, FileWriter individualCSVWriter) throws IOException {
        // Clear the list of agents before a simulation begins.
        if (!agents.isEmpty()) {
            agents.clear();
        }

        int agentType = 0;
        int agentsOfThisType = 0;

        // Create the Agents for the simulation.
        for (int j = 1; j <= ResourceExchangeArena.POPULATION_SIZE; j++) {
            if (agentsOfThisType >= ArenaEnvironment.numberOfEachAgentType) {
                agentType++;
                agentsOfThisType = 0;
            }
            if (agentType < ArenaEnvironment.agentTypes.length) {
                new Agent(j, ArenaEnvironment.agentTypes[agentType]);
                agentsOfThisType++;
            } else {
                j--;
                agentType = 0;
                agentsOfThisType = 0;
                ArenaEnvironment.numberOfEachAgentType = 1;
            }
        }

        // Increment the simulations seed each run.
        ArenaEnvironment.seed++;
        ResourceExchangeArena.random.setSeed(ArenaEnvironment.seed);

        // Initialise each Agents relations with each other Agent if trading Agents are being simulated.
        for (Agent a : agents) {
            a.initializeFavoursStore();
        }


        for (int j = 1; j <= ResourceExchangeArena.DAYS; j++) {
            Day.dayVariedAgents(i, j, averageCSVWriter, individualCSVWriter);
        }
    }

    static void simulate(int i, int type, FileWriter averageCSVWriter, FileWriter individualCSVWriter) throws IOException {
        // Clear the list of agents before a simulation begins.
        if (!agents.isEmpty()) {
            agents.clear();
         }

        if (type < ArenaEnvironment.uniqueAgentTypes.size()) {
            if (type < 0) {
                for (int j = 1; j <= ArenaEnvironment.POPULATION_SIZE; j++) {
                    new Agent(j, ArenaEnvironment.NON_TRADER);
                }
            } else {
                for (int j = 1; j <= ArenaEnvironment.POPULATION_SIZE; j++) {
                    new Agent(j, ArenaEnvironment.uniqueAgentTypes.get(type));
                }
            }
        } else {
            return;
        }
        // Increment the simulations seed each run.
        ArenaEnvironment.seed++;
        ResourceExchangeArena.random.setSeed(ArenaEnvironment.seed);

        // Initialise each Agents relations with each other Agent if trading Agents are being simulated.
        if (type >= 0) {
            for (Agent a : agents) {
                a.initializeFavoursStore();
            }
        }

        for (int j = 1; j <= ArenaEnvironment.DAYS; j++) {
            Day.day(i, j, type, averageCSVWriter, individualCSVWriter);
        }
    }
}
