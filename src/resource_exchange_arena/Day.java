package resource_exchange_arena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

public class Day {
    // List of all the possible allocations that exist in the current simulation.
    private final ArrayList<Integer> availableTimeSlots = new ArrayList<>();

    /**
     * Each Simulation run consists of a number of days, each day consists of requesting and being allocated time slots,
     * exchanging those slots with other agents, and agents using social learning to learn from their experiences.
     *
     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
     * @param demandCurves Double arrays of demand used by the agents, when multiple curves are used the agents
     *                    are split equally between the curves.
     * @param totalDemandValues Double values represeneting the sum of all values in their associated demand curves.
     * @param availabilityCurve Integer array representing the amount of energy available at each timeslot.
     * @param totalAvailability Integer value representing the total energy available throughout the day.
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
    Day(
            int[] daysOfInterest,
            double[][] demandCurves,
            double[] totalDemandValues,
            int [] availabilityCurve,
            int totalAvailability,
            int day,
            int exchanges,
            int populationSize,
            int uniqueTimeSlots,
            int slotsPerAgent,
            int numberOfAgentsToEvolve,
            ArrayList<Integer> uniqueAgentTypes,
            ArrayList<Agent> agents,
            ArrayList<ArrayList<Integer>>  socialCapitalTracking,
            ArrayList<ArrayList<Integer>>  exchangeTypeTracking,
            ArrayList<ArrayList<Integer>>  exchangeSuccessTracking,
            ArrayList<ArrayList<Double>> endOfDaySatisfactions,
            ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions,
            ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions,
            ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions,
            FileWriter dailyDataWriter,
            int run
    ) throws IOException{

        if(!availableTimeSlots.isEmpty()) {
            availableTimeSlots.clear();
        }

        // Fill the available time slots with all the slots that exist each day.
        int requiredTimeSLots = populationSize * slotsPerAgent;

        for (int i = 1; i <= requiredTimeSLots; i++) {
            // Get the simulations seeded Random object.
            Random random = ResourceExchangeArena.random;

            // Selects a time slot based on the demand curve.
            int wheelSelector = random.nextInt(totalAvailability);
            int wheelCalculator = 0;
            int timeSlot = 0;
            while (wheelCalculator < wheelSelector) {
                wheelCalculator = wheelCalculator + (availabilityCurve[timeSlot]);
                timeSlot++;
            }
            availableTimeSlots.add(timeSlot);
        }


        // Agents start the day by requesting and receiving an allocation of time slots.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        ArrayList<Integer> curves = new ArrayList<>();

        int curve = 0;
        for (Agent a : agents) {
            curves.add(curve);
            curve++;
            if (curve >= demandCurves.length) {
                curve = 0;
            }
        }
        Collections.shuffle(curves);

        for (Agent a : agents) {
            a.resetDailyTracking();
            int selector = curves.remove(0);
            ArrayList<Integer> requestedTimeSlots = a.requestTimeSlots(demandCurves[selector], totalDemandValues[selector]);
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
        // Also store information about average social capital and the exchanges that occured that day.
        if (IntStream.of(daysOfInterest).anyMatch(val -> val == day)) {
            for (Agent a : agents) {
                ArrayList<Double> endOfDaySatisfaction = new ArrayList<>();
                endOfDaySatisfaction.add((double) day);
                endOfDaySatisfaction.add((double) a.getAgentType());
                endOfDaySatisfaction.add(a.calculateSatisfaction(null));
                endOfDaySatisfactions.add(endOfDaySatisfaction);

                ArrayList<Integer> socialCapitalTracked = new ArrayList<>();
                socialCapitalTracked.add(day);
                socialCapitalTracked.add(a.getAgentType());
                socialCapitalTracked.add(a.getUnspentSocialCapital());
                socialCapitalTracking.add(socialCapitalTracked);

                ArrayList<Integer> exchangeTypeTracked = new ArrayList<>();
                exchangeTypeTracked.add(day);
                exchangeTypeTracked.add(a.getAgentType());
                exchangeTypeTracked.add(a.getSocialCapitalExchanges());
                exchangeTypeTracked.add(a.getNoSocialCapitalExchanges());
                exchangeTypeTracking.add(exchangeTypeTracked);

                ArrayList<Integer> exchangeSuccessTracked = new ArrayList<>();
                exchangeSuccessTracked.add(day);
                exchangeSuccessTracked.add(a.getAgentType());
                exchangeSuccessTracked.add(a.getRejectedReceivedExchanges());
                exchangeSuccessTracked.add(a.getSocialCapitalExchanges() + a.getNoSocialCapitalExchanges());
                exchangeSuccessTracked.add(a.getRejectedRequestedExchanges());
                exchangeSuccessTracked.add(a.getAcceptedRequestedExchanges());
                exchangeSuccessTracking.add(exchangeSuccessTracked);
            }
        }

        int socPop = 0;
        int selPop = 0;
        for (Agent a : agents) {
            if (a.getAgentType() == ResourceExchangeArena.SOCIAL) {
                socPop++;
            } else if (a.getAgentType() == ResourceExchangeArena.SELFISH) {
                selPop++;
            }
        }

        double[] socialStatValues = CalculateSatisfaction.statisticalValues(agents, ResourceExchangeArena.SOCIAL);
        double[] selfishStatValues = CalculateSatisfaction.statisticalValues(agents, ResourceExchangeArena.SELFISH);

        dailyDataWriter.append(String.valueOf(run));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(day));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(socPop));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(selPop));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(CalculateSatisfaction.averageAgentSatisfaction(agents, ResourceExchangeArena.SOCIAL)));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(CalculateSatisfaction.averageAgentSatisfaction(agents, ResourceExchangeArena.SELFISH)));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(CalculateSatisfaction.averageSatisfactionStandardDeviation(agents, ResourceExchangeArena.SOCIAL)));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(CalculateSatisfaction.averageSatisfactionStandardDeviation(agents, ResourceExchangeArena.SELFISH)));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[0]));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(selfishStatValues[0]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[1]));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(selfishStatValues[1]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[2]));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(selfishStatValues[2]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[3]));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(selfishStatValues[3]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[4]));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(selfishStatValues[4]));
        dailyDataWriter.append(",");

        dailyDataWriter.append(String.valueOf(socialStatValues[5]));
        dailyDataWriter.append(",");
        
        dailyDataWriter.append(String.valueOf(selfishStatValues[5]));
        dailyDataWriter.append("\n");


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
