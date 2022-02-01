package resource_exchange_arena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class SimulationRun {
    /**
     * Each Simulation run with the same parameters runs as an isolated instance although data is recorded in a single
     * location.
     *
     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
     * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used the agents
     *                    are split equally between the curves.
     * @param totalDemandValues Double values represeneting the sum of all values in their associated demand curves.
     * @param availabilityCurve Integer array representing the amount of energy available at each timeslot.
     * @param totalAvailability Integer value representing the total energy available throughout the day.
     * @param days Integer value representing the number of days to be simulated.
     * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per day.
     * @param populationSize Integer value representing the size of the initial agent population.
     * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the simulation.
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change at the
     *                               end of each day.
     * @param agentTypes Integer array containing the agent types that the simulation will begin with. The same type
     *                   can exist multiple times in the array where more agents of one type are required.
     * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the simulation
     *                         begins.
     * @param singleAgentType Boolean value specifying whether only a single agent type should exist, used for
     *                        establishing baseline results.
     * @param selectedSingleAgentType Integer value representing the single agent type to be modelled when
     *                                singleAgentType is true.
     * @param socialCapital Boolean value that determines whether or not social agents will utilise social capital.
     * @param socialCapitalTracking Stores the amount of social capital per agent for each agent type.
     * @param exchangeTypeTracking Stores how many exchanges used social capital and how many did not.
     * @param exchangeSuccessTracking Stores how many potential exchanges were accepted.
     * @param endOfDaySatisfactions Stores the satisfaction of each agent at the end of days of interest.
     * @param endOfRoundAverageSatisfactions Stores the average satisfaction for each agent type at the end of each
     *                                       round.
     * @param endOfDayAverageSatisfactions Stores the average satisfaction for each agent type at the end of each day.
     * @param endOfDayPopulationDistributions Stores the population of each agent type at the end of each day.
     * @exception IOException On input error.
     * @see IOException
     */
    SimulationRun(
            int[] daysOfInterest,
            double[][] demandCurves,
            double[] totalDemandValues,
            int [] availabilityCurve,
            int totalAvailability,
            int days,
            int exchanges,
            int populationSize,
            int uniqueTimeSlots,
            int slotsPerAgent,
            int numberOfAgentsToEvolve,
            int[] agentTypes,
            ArrayList<Integer> uniqueAgentTypes,
            boolean singleAgentType,
            int selectedSingleAgentType,
            boolean socialCapital,
            ArrayList<ArrayList<Integer>>  socialCapitalTracking,
            ArrayList<ArrayList<Integer>>  exchangeTypeTracking,
            ArrayList<ArrayList<Integer>>  exchangeSuccessTracking,
            ArrayList<ArrayList<Double>> endOfDaySatisfactions,
            ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions,
            ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions,
            ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions
    ) throws IOException {

        // List of all the Agents that are part of the current simulation.
        ArrayList<Agent> agents = new ArrayList<>();

        // Create the Agents for the simulation.
        for (int agentNumber = 1; agentNumber <= populationSize; agentNumber++) {
                /*
                 * This is the constructor for Agent objects.
                 *
                 * @param agentID This is an integer value that is unique to the individual agent and used to identify
                 *                it to others in the ExchangeArena.
                 * @param agentType Integer value denoting the agent type, and thus how it will behave.
                 * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
                 * @param agents Array List of all the agents that exist in the current simulation.
                 * @param socialCapital determines whether the agent uses socialCapital.
                 */
                new Agent(
                        agentNumber,
                        agentTypes[agentNumber % agentTypes.length],
                        slotsPerAgent,
                        agents,
                        socialCapital
                );
        }
        Collections.shuffle(agents, ResourceExchangeArena.random);

        // Set all agents to a single type, used for establishing baseline performance.
        if (singleAgentType && selectedSingleAgentType != 0) {
            for (Agent a: agents) {
                a.setType(selectedSingleAgentType);
            }
        }   
        
        // Increment the simulations seed each run.
        ResourceExchangeArena.seed++;
        ResourceExchangeArena.random.setSeed(ResourceExchangeArena.seed);

        // Initialise each Agents relations with each other Agent.
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
             * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used the agents
             *                    are split equally between the curves.
             * @param totalDemandValues Double values represeneting the sum of all values in their associated demand curves.
             * @param availabilityCurve Integer array representing the amount of energy available at each timeslot.
             * @param totalAvailability Integer value representing the total energy available throughout the day.
             * @param day Integer value representing the current day being simulated.
             * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges
             *                  per day.
             * @param populationSize Integer value representing the size of the initial agent population.
             * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the
             *                        simulation.
             * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
             * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change
             *                               at the end of each day.
             * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the
             *                         simulation begins.
             * @param agents Array List of all the agents that exist in the current simulation.
             * @param socialCapitalTracking Stores the amount of social capital per agent for each agent type.
             * @param exchangeTypeTracking Stores how many exchanges used social capital and how many did not.
             * @param exchangeSuccessTracking Stores how many potential exchanges were accepted.
             * @param endOfDaySatisfactions Stores the satisfaction of each agent at the end of days of interest.
             * @param endOfRoundAverageSatisfactions Stores the average satisfaction for each agent type at the end of
             *                                       each round.
             * @param endOfDayAverageSatisfactions Stores the average satisfaction for each agent type at the end of
             *                                     each day.
             * @param endOfDayPopulationDistributions Stores the population of each agent type at the end of each day.
             * @exception IOException On input error.
             * @see IOException
             */
            new Day(
                    daysOfInterest,
                    demandCurves,
                    totalDemandValues,
                    availabilityCurve,
                    totalAvailability,
                    day,
                    exchanges,
                    populationSize,
                    uniqueTimeSlots,
                    slotsPerAgent,
                    numberOfAgentsToEvolve,
                    uniqueAgentTypes,
                    agents,
                    socialCapitalTracking,
                    exchangeTypeTracking,
                    exchangeSuccessTracking,
                    endOfDaySatisfactions,
                    endOfRoundAverageSatisfactions,
                    endOfDayAverageSatisfactions,
                    endOfDayPopulationDistributions
            );
        }
    }
}
