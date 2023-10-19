package resource_exchange_arena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class SimulationRun {
    /**
     * Each Simulation run with the same parameters runs as an isolated instance although data is recorded in a single
     * location.
     *
     * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used the agents
     *                    are split equally between the curves.
     * @param totalDemandValues Double values represeneting the sum of all values in their associated demand curves.
     * @param availabilityCurve Integer array representing the amount of energy available at each time-slot.
     * @param totalAvailability Integer value representing the total energy available throughout the day.
     * @param days Integer value representing the number of days to be simulated.
     * @param maxExchanges Stores the highest number of exchange rounds reached each simulation.
     * @param populationSize Integer value representing the size of the initial agent population.
     * @param uniqueTimeSlots Integer value representing the number of unique time-slots available in the simulation.
     * @param slotsPerAgent Integer value representing the number of time-slots each agent requires.
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
     * @param keyDaysData Stores the state of the simulation when a population takes over and when the simulation ends.
     * @param allDailyDataCSVWriter Used to store data ragarding the state of the system at the end of each day.
     * @param perAgentDataCSVWriter Used to store data ragarding the state of the agent at the end of each day.
     * @param eachRoundDataCSVWriter Used to store data ragarding the state of the system at the end of each round.
     * @param β Double value that increases the the chance that agents will change their strategy.
     * @param satisfactionCurve Double array that determines the satisfaction fall off for slots received close to the agents preferences.
     * @exception IOException On input error.
     * @see IOException
     */
    SimulationRun(
        double[][] demandCurves,
        double[] totalDemandValues,
        int [] availabilityCurve,
        int totalAvailability,
        int days,
        ArrayList<Integer> maxExchanges,
        int populationSize,
        int uniqueTimeSlots,
        int slotsPerAgent,
        int numberOfAgentsToEvolve,
        int[] agentTypes,
        ArrayList<Integer> uniqueAgentTypes,
        boolean singleAgentType,
        int selectedSingleAgentType,
        boolean socialCapital,
        ArrayList<ArrayList<Double>> keyDaysData,
        FileWriter dailyDataWriter,
        FileWriter perAgentDataCSVWriter,
        FileWriter eachRoundDataCSVWriter,
        int run,
        double β,
        double[] satisfactionCurve
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
                 * @param slotsPerAgent Integer value representing the number of time-slots each agent requires.
                 * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the simulation begins.
                 * @param agents Array List of all the agents that exist in the current simulation.
                 * @param socialCapital determines whether the agent uses socialCapital.
                 * @param satisfactionCurve Double array that determines the satisfaction fall off for slots received close to the agents preferences.
                 */
                new Agent(
                        agentNumber,
                        agentTypes[agentNumber % agentTypes.length],
                        slotsPerAgent,
                        uniqueTimeSlots,
                        agents,
                        socialCapital,
                        satisfactionCurve
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

        boolean complete = false;
        boolean takeover = false;
        int extention = 0;
        int day = 1;
        while (!complete) {
            /*
            * Each Simulation run with the same parameters runs as an isolated instance although data is recorded in a
            * single location.
            *
            * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used the agents
            *                    are split equally between the curves.
            * @param totalDemandValues Double values represeneting the sum of all values in their associated demand curves.
            * @param availabilityCurve Integer array representing the amount of energy available at each time-slot.
            * @param totalAvailability Integer value representing the total energy available throughout the day.
            * @param day Integer value representing the current day being simulated.
            * @param maxExchanges Stores the highest number of exchange rounds reached each simulation.
            * @param populationSize Integer value representing the size of the initial agent population.
            * @param uniqueTimeSlots Integer value representing the number of unique time-slots available in the
            *                        simulation.
            * @param slotsPerAgent Integer value representing the number of time-slots each agent requires.
            * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change
            *                               at the end of each day.
            * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the
            *                         simulation begins.
            * @param agents Array List of all the agents that exist in the current simulation.
            * @param allDailyDataCSVWriter Used to store data ragarding the state of the system at the end of each day.
            * @param perAgentDataCSVWriter Used to store data ragarding the state of the agent at the end of each day.
            * @param eachRoundDataCSVWriter Used to store data ragarding the state of the system at the end of each round.
            * @param run Integer value identifying the current simulation run.
            * @param β Double value that increases the the chance that agents will change their strategy.
            * @exception IOException On input error.
            * @see IOException
            */
            Day current = new Day(
                demandCurves,
                totalDemandValues,
                availabilityCurve,
                totalAvailability,
                day,
                maxExchanges,
                populationSize,
                uniqueTimeSlots,
                slotsPerAgent,
                numberOfAgentsToEvolve,
                uniqueAgentTypes,
                agents,
                dailyDataWriter,
                perAgentDataCSVWriter,
                eachRoundDataCSVWriter,
                run,
                β
            );

            if (((current.selPop == 0 || current.socPop == 0) || numberOfAgentsToEvolve == 0) && !takeover) {
                takeover = true;
                ArrayList<Double> takeoverData = new ArrayList<>();
                takeoverData.add((double) run);
                takeoverData.add((double) day);
                takeoverData.add((double) current.socPop);
                takeoverData.add((double) current.selPop);
                takeoverData.add(current.socSat);
                takeoverData.add(current.selSat);
                takeoverData.add(current.socSD);
                takeoverData.add(current.selSD);
                takeoverData.add(current.socialStatValues[0]);
                takeoverData.add(current.selfishStatValues[0]);
                takeoverData.add(current.socialStatValues[1]);
                takeoverData.add(current.selfishStatValues[1]);
                takeoverData.add(current.socialStatValues[2]);
                takeoverData.add(current.selfishStatValues[2]);
                takeoverData.add(current.socialStatValues[3]);
                takeoverData.add(current.selfishStatValues[3]);
                takeoverData.add(current.socialStatValues[4]);
                takeoverData.add(current.selfishStatValues[4]);
                takeoverData.add(current.socialStatValues[5]);
                takeoverData.add(current.selfishStatValues[5]);
                takeoverData.add(current.randomAllocations);
                takeoverData.add(current.optimumAllocations);
                takeoverData.add(0.0);
                keyDaysData.add(takeoverData);
            }

            if (extention == days) {
                complete = true;
                ArrayList<Double> finalData = new ArrayList<>();
                finalData.add((double) run);
                finalData.add((double) day);
                finalData.add((double) current.socPop);
                finalData.add((double) current.selPop);
                finalData.add(current.socSat);
                finalData.add(current.selSat);
                finalData.add(current.socSD);
                finalData.add(current.selSD);
                finalData.add(current.socialStatValues[0]);
                finalData.add(current.selfishStatValues[0]);
                finalData.add(current.socialStatValues[1]);
                finalData.add(current.selfishStatValues[1]);
                finalData.add(current.socialStatValues[2]);
                finalData.add(current.selfishStatValues[2]);
                finalData.add(current.socialStatValues[3]);
                finalData.add(current.selfishStatValues[3]);
                finalData.add(current.socialStatValues[4]);
                finalData.add(current.selfishStatValues[4]);
                finalData.add(current.socialStatValues[5]);
                finalData.add(current.selfishStatValues[5]);
                finalData.add(current.randomAllocations);
                finalData.add(current.optimumAllocations);
                finalData.add(1.0);
                keyDaysData.add(finalData);
            }
            day++;
            
            if (takeover) {
                extention++;
            }
        }
    }
}
