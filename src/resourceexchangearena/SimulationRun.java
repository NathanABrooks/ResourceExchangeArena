package resourceexchangearena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class SimulationRun {

    /**
     * Each Simulation run with the same parameters runs as an isolated instance although data is recorded in a single
     * location.
     *
     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
     * @param additionalData Boolean value that configures the simulation to output the state of each agent after each
     *                       exchange and at the end of each day.
     * @param days Integer value representing the number of days to be simulated.
     * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per day.
     * @param populationSize Integer value representing the size of the initial agent population.
     * @param maximumPeakConsumption Integer value representing how many agents can be allocated to each time slot.
     * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the simulation.
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change
     *                               at the end of each day.
     * @param agentTypes Integer array containing the agent types that the simulation will begin with. The same type
     *                   can exist multiple times in the array where more agents of one type are required.
     * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the simulation
     *                         begins.
     * @param singleAgentType Boolean value specifying whether only a single agent type should exist, used for
     *                        establishing baseline results.
     * @param selectedSingleAgentType Integer value representing the single agent type to be modelled when
     *                                singleAgentType is true.
     * @param endOfDaySatisfactions  Stores the satisfaction of each agent at the end of days of interest.
     * @param endOfRoundAverageSatisfactions Stores the average satisfaction for each agent type at the end of each
     *                                       round.
     * @param endOfDayAverageSatisfactions  Stores the average satisfaction for each agent type at the end of each day.
     * @param endOfDayPopulationDistributions Stores the population of each agent type at the end of each day.
     * @param averageCSVWriter Writes additional data on the average satisfaction of every agent at the end of each day
     *                         when additional data is requested.
     * @param individualCSVWriter Writes additional data on the individual agents satisfaction after each exchange when
     *                            additional data is requested.
     * @exception IOException On input error.
     * @see IOException
     */
    SimulationRun(
            int[] daysOfInterest,
            boolean additionalData,
            int days,
            int exchanges,
            int populationSize,
            int maximumPeakConsumption,
            int uniqueTimeSlots,
            int slotsPerAgent,
            int numberOfAgentsToEvolve,
            int[] agentTypes,
            ArrayList<Integer> uniqueAgentTypes,
            boolean singleAgentType,
            int selectedSingleAgentType,
            ArrayList<ArrayList<Double>> endOfDaySatisfactions,
            ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions,
            ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions,
            ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions,
            FileWriter averageCSVWriter,
            FileWriter individualCSVWriter
    ) throws IOException {

        // List of all the Agents that are part of the current simulation.
        ArrayList<Agent> agents = new ArrayList<>();

        // Create the Agents for the simulation.
        int numberOfEachAgentType = populationSize / agentTypes.length;
        int agentType = 0;
        int agentsOfThisType = 0;

        for (int agentNumber = 1; agentNumber <= populationSize; agentNumber++) {
            if (agentsOfThisType >= numberOfEachAgentType) {
                agentType++;
                agentsOfThisType = 0;
            }
            if (agentType < agentTypes.length) {

                /*
                 * This is the constructor for Agent objects.
                 *
                 * @param agentID This is an integer value that is unique to the individual agent and
                 *                used to identify it to others in the ExchangeArena.
                 * @param agentType Integer value denoting the agent type, and thus how it will behave.
                 * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
                 * @param agents Array List of all the agents that exist in the current simulation.
                 */
                new Agent(agentNumber, agentTypes[agentType], slotsPerAgent, agents);
                agentsOfThisType++;
            } else {
                // When there can't be an equal number of each agent type, each type gets an additional agent until all
                // agents have been assigned.
                agentNumber--;
                agentType = 0;
                agentsOfThisType = 0;
                numberOfEachAgentType = 1;
            }
        }

        // Set all agents to a single type, used for establishing baseline performance.
        if (singleAgentType && selectedSingleAgentType != 0)
        for (Agent a: agents) {
            a.setType(selectedSingleAgentType);
        }


        // Increment the simulations seed each run.
        ResourceExchangeArena.seed++;
        ResourceExchangeArena.random.setSeed(ResourceExchangeArena.seed);

        // Initialise each Agents relations with each other Agent if trading Agents are being simulated.
        for (Agent a : agents) {
            a.initializeFavoursStore(agents);
        }

        // Run the simulation for a pre-determined number of days.
        for (int day = 1; day <= days; day++) {

            /*
             * Each Simulation run with the same parameters runs as an isolated instance although data is recorded in a
             * single location.
             *
             * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
             * @param additionalData Boolean value that configures the simulation to output the state of each agent
             *                       after each exchange and at the end of each day.
             * @param day Integer value representing the current day being simulated.
             * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges
             *                  per day.
             * @param maximumPeakConsumption Integer value representing how many agents can be allocated to each time
             *                               slot.
             * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the
             *                        simulation.
             * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
             * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change
             *                               at the end of each day.
             * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the
             *                         simulation begins.
             * @param agents Array List of all the agents that exist in the current simulation.
             * @param endOfDaySatisfactions  Stores the satisfaction of each agent at the end of days of interest.
             * @param endOfRoundAverageSatisfactions Stores the average satisfaction for each agent type at the end of
             *                                       each round.
             * @param endOfDayAverageSatisfactions Stores the average satisfaction for each agent type at the end of
             *                                     each day.
             * @param endOfDayPopulationDistributions Stores the population of each agent type at the end of each day.
             * @param averageCSVWriter Writes additional data on the average satisfaction of every agent at the end of
             *                         each day when additional data is requested.
             * @param individualCSVWriter Writes additional data on the individual agents satisfaction after each
             *                            exchange when additional data is requested.
             * @exception IOException On input error.
             * @see IOException
             */
            new Day(
                    daysOfInterest,
                    additionalData,
                    day,
                    exchanges,
                    maximumPeakConsumption,
                    uniqueTimeSlots,
                    slotsPerAgent,
                    numberOfAgentsToEvolve,
                    uniqueAgentTypes,
                    agents,
                    endOfDaySatisfactions,
                    endOfRoundAverageSatisfactions,
                    endOfDayAverageSatisfactions,
                    endOfDayPopulationDistributions,
                    averageCSVWriter,
                    individualCSVWriter
            );
        }
    }
}
