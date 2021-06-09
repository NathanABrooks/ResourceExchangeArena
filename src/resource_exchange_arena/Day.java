package resource_exchange_arena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

public class Day {
    // List of all the possible allocations that exist in the current simulation.
    private final ArrayList<Integer> availableTimeSlots = new ArrayList<>();

    /**
     * Each Simulation run consists of a number of days, each day consists of requesting and being allocated time slots,
     * exchanging those slots with other agents, and agents using social learning to learn from their experiences.
     *
     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
     * @param day Integer value representing the current day being simulated.
     * @param exchanges Integer value representing the number of times all agents perform pairwise exchanges per day.
     * @param populationSize Integer value representing the size of the initial agent population.
     * @param uniqueTimeSlots Integer value representing the number of unique time slots available in the simulation.
     * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
     * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy will change at the
     *                               end of each day.
     * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the simulation
     *                         begins.
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param endOfDaySatisfactions Stores the satisfaction of each agent at the end of days of interest.
     * @param endOfRoundAverageSatisfactions Stores the average satisfaction for each agent type at the end of each
     *                                       round.
     * @param endOfDayAverageSatisfactions Stores the average satisfaction for each agent type at the end of each day.
     * @param endOfDayPopulationDistributions Stores the population of each agent type at the end of each day.
     * @exception IOException On input error.
     * @see IOException
     */
    Day(
            int[] daysOfInterest,
            int day,
            int exchanges,
            int populationSize,
            int uniqueTimeSlots,
            int slotsPerAgent,
            int numberOfAgentsToEvolve,
            ArrayList<Integer> uniqueAgentTypes,
            ArrayList<Agent> agents,
            ArrayList<ArrayList<Double>> endOfDaySatisfactions,
            ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions,
            ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions,
            ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions
    ) throws IOException{

        // Fill the available time slots with all the slots that exist each day.
        int requiredTimeSLots = populationSize * slotsPerAgent;
        ArrayList<Integer> possibleTimeSlots = new ArrayList<>();

        while(availableTimeSlots.size() < requiredTimeSLots){
            for (int timeSlot = 1; timeSlot <= uniqueTimeSlots; timeSlot++) {
                possibleTimeSlots.add(timeSlot);
            }

            while(!possibleTimeSlots.isEmpty()){
                if (availableTimeSlots.size() < requiredTimeSLots) {
                    int selector = ResourceExchangeArena.random.nextInt(possibleTimeSlots.size());
                    int timeSlot = possibleTimeSlots.get(selector);

                    availableTimeSlots.add(timeSlot);
                    possibleTimeSlots.remove(selector);
                } else {
                    possibleTimeSlots.clear();
                    break;
                }
            }
        }

        // Agents start the day by requesting and receiving an allocation of time slots.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        for (Agent a : agents) {
            ArrayList<Integer> requestedTimeSlots = a.requestTimeSlots(uniqueTimeSlots);
            ArrayList<Integer> allocatedTimeSlots = getRandomInitialAllocation(requestedTimeSlots);
            a.receiveAllocatedTimeSlots(allocatedTimeSlots);
        }

        // The random and optimum average satisfaction scores are calculated before exchanges take place.
        double randomAllocations = CalculateSatisfaction.averageAgentSatisfaction(agents);
        double optimumAllocations = CalculateSatisfaction.optimumAgentSatisfaction(agents);

        // A pre-determined number of pairwise exchanges take place, during each exchange all agents have a chance to
        // trade with another agent.
        for (int exchange = 1; exchange <= exchanges; exchange++) {

            /*
             * With each exchange all agents form pairwise exchanges and are able to consider a trade with their
             * partner for one time slot.
             *
             * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
             * @param day Integer value representing the current day being simulated.
             * @param exchange Integer value representing the current exchange being simulated.
             * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the
             *                         simulation begins.
             * @param agents Array List of all the agents that exist in the current simulation.
             * @param endOfRoundAverageSatisfactions Stores the average satisfaction for each agent type at the end of
             *                                       each round.
             * @exception IOException On input error.
             * @see IOException
             */
            new Exchange(
                    daysOfInterest,
                    day,
                    exchange,
                    uniqueAgentTypes,
                    agents,
                    endOfRoundAverageSatisfactions
            );
        }
        // The average end of day satisfaction is stored for each agent type to later be averaged and analysed.
        ArrayList<Double> endOfDayAverageSatisfaction = new ArrayList<>();
        endOfDayAverageSatisfaction.add((double) day);
        endOfDayAverageSatisfaction.add(randomAllocations);
        endOfDayAverageSatisfaction.add(optimumAllocations);

        // Store the end of day average satisfaction for each agent type.
        for (int uniqueAgentType : uniqueAgentTypes) {
            double typeAverageSatisfaction = CalculateSatisfaction.averageAgentSatisfaction(agents, uniqueAgentType);
            endOfDayAverageSatisfaction.add(typeAverageSatisfaction);
        }
        // Temporarily store the end of day average variance for each agent type.
        for (int uniqueAgentType : uniqueAgentTypes) {
            double typeAverageSatisfactionSD =
                    CalculateSatisfaction.averageSatisfactionStandardDeviation(agents, uniqueAgentType);
            endOfDayAverageSatisfaction.add(typeAverageSatisfactionSD);
        }
        endOfDayAverageSatisfactions.add(endOfDayAverageSatisfaction);

        for (Integer uniqueAgentType : uniqueAgentTypes) {
            int populationQuantity = 0;
            for (Agent a : agents) {
                if (a.getAgentType() == uniqueAgentType) {
                    populationQuantity++;
                }
            }
            endOfDayPopulationDistributions.get(day - 1)
                    .get(uniqueAgentTypes.indexOf(uniqueAgentType)).add(populationQuantity);
        }

        // On days of interest, store the satisfaction for each agent at the end of the day to be added to violin plots.
        if (IntStream.of(daysOfInterest).anyMatch(val -> val == day)) {
            for (Agent a : agents) {
                ArrayList<Double> endOfDaySatisfaction = new ArrayList<>();
                endOfDaySatisfaction.add((double) day);
                endOfDaySatisfaction.add((double) a.getAgentType());
                endOfDaySatisfaction.add(a.calculateSatisfaction(null));
                endOfDaySatisfactions.add(endOfDaySatisfaction);
            }
        }

        /*
         * To facilitate social learning, for the number of the agents who are able to consider changing their strategy,
         * an Agent is selected at random, and then a second agent is selected to be observed. The first agent selected
         * checks whether their performance was weaker than the agent observed, if so they have a chance to copy the
         * strategy used by the observed agent in the previous day, with the likelihood of copying their strategy
         * proportional to the difference between their individual satisfactions.
         *
         * @param agents Array List of all the agents that exist in the current simulation.
         * @param slotsPerAgent Integer value representing the number of time slots each agent requires.
         * @param numberOfAgentsToEvolve Integer value representing the number of Agents who's strategy may change at
         *                               the end of each day.
         */
        new SocialLearning(agents, slotsPerAgent, numberOfAgentsToEvolve);
    }

    /**
     * Gives a random initial time slot allocation to an Agent based on the number of time slots it requests and the
     * time slots that are currently available.
     *
     * @param requestedTimeSlots The time slots that the Agent has requested.
     * @return ArrayList<Integer> Returns a list of time slots to allocated to the Agent.
     */
    private ArrayList<Integer> getRandomInitialAllocation(ArrayList<Integer> requestedTimeSlots) {
        ArrayList<Integer> timeSlots = new ArrayList<>();

        for (int requestedTimeSlot = 1; requestedTimeSlot <= requestedTimeSlots.size(); requestedTimeSlot++) {
            // Only allocate time slots if there are slots available to allocate.
            if (!availableTimeSlots.isEmpty()) {
                int selector = ResourceExchangeArena.random.nextInt(availableTimeSlots.size());
                int timeSlot = availableTimeSlots.get(selector);

                timeSlots.add(timeSlot);
                availableTimeSlots.remove(selector);
            } else {
                System.out.println("Error: No Timeslots Available");
            }
        }
        return timeSlots;
    }
}
