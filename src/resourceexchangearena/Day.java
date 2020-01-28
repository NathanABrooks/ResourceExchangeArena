package resourceexchangearena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day {

    // List of all the possible allocations that exist in the current simulation.
    private List<Integer> availableTimeSlots = new ArrayList<>();

    Day(
            int day,
            int maximumPeakConsumption,
            int uniqueTimeSlots,
            int exchanges,
            int slotsPerAgent,
            FileWriter averageCSVWriter,
            FileWriter individualCSVWriter,
            ArrayList<Agent> agents,
            ArrayList<Integer> uniqueAgentTypes,
            ArrayList<ArrayList<Double>> endOfDayAverageSatisfactions,
            ArrayList<ArrayList<ArrayList<Integer>>> endOfDayPopulationDistributions,
            int[] daysOfInterest,
            ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions
    ) throws IOException{

        // Create a copy of the Agents list that can be shuffled so Agents act in a random order.
        ArrayList<Agent> shuffledAgents = new ArrayList<>(agents);

        // Fill the available time slots with all the slots that exist each day.
        for (int timeSlot = 1; timeSlot <= uniqueTimeSlots; timeSlot++) {
            for (int unit = 1; unit <= maximumPeakConsumption; unit++) {
                availableTimeSlots.add(timeSlot);
            }
        }

        // Agents start the day by requesting and receiving an allocation of time slots.
        Collections.shuffle(shuffledAgents, ResourceExchangeArena.random);

        for (Agent a : shuffledAgents) {
            ArrayList<Integer> requestedTimeSlots = a.requestTimeSlots(uniqueTimeSlots);
            ArrayList<Integer> allocatedTimeSlots = getRandomInitialAllocation(requestedTimeSlots);
            a.receiveAllocatedTimeSlots(allocatedTimeSlots);
        }

        double randomAllocations = CalculateSatisfaction.averageAgentSatisfaction(agents);
        double optimumAllocations = CalculateSatisfaction.optimumAgentSatisfaction(agents);

        // The random and optimum average satisfaction scores are calculated before exchanges take place.
        averageCSVWriter.append(String.valueOf(ResourceExchangeArena.seed));
        averageCSVWriter.append(",");
        averageCSVWriter.append(String.valueOf(day));
        averageCSVWriter.append(",");
        averageCSVWriter.append(String.valueOf(randomAllocations));
        averageCSVWriter.append(",");
        averageCSVWriter.append(String.valueOf(optimumAllocations));

        for (int exchange = 1; exchange <= exchanges; exchange++) {
            new Exchange(
                    shuffledAgents,
                    day,
                    exchange,
                    individualCSVWriter,
                    agents,
                    daysOfInterest,
                    endOfRoundAverageSatisfactions,
                    uniqueAgentTypes
            );
        }

        // The average end of day satisfaction is stored for each Agent type to later be averaged
        // and added to the prePreparedAverageFile.
        ArrayList<Double> endOfDayAverageSatisfaction = new ArrayList<>();
        endOfDayAverageSatisfaction.add((double) day);
        endOfDayAverageSatisfaction.add(randomAllocations);
        endOfDayAverageSatisfaction.add(optimumAllocations);

        // Store the end of day average satisfaction for each agent type.
        for (int uniqueAgentType : uniqueAgentTypes) {
            double typeAverageSatisfaction = CalculateSatisfaction.averageAgentSatisfaction(agents, uniqueAgentType);
            averageCSVWriter.append(",");
            averageCSVWriter.append(String.valueOf(typeAverageSatisfaction));
            endOfDayAverageSatisfaction.add(typeAverageSatisfaction);
        }
        // Temporarily store the end of day average variance for each agent type.
        for (int uniqueAgentType : uniqueAgentTypes) {
            double typeAverageSatisfactionSD = CalculateSatisfaction.averageSatisfactionStandardDeviation(agents, uniqueAgentType);
            endOfDayAverageSatisfaction.add(typeAverageSatisfactionSD);
        }
        averageCSVWriter.append("\n");
        endOfDayAverageSatisfactions.add(endOfDayAverageSatisfaction);

        for (Integer uniqueAgentType : uniqueAgentTypes) {
            int populationQuantity = 0;
            for (Agent a : agents) {
                if (a.getAgentType() == uniqueAgentType) {
                    populationQuantity++;
                }
            }
            endOfDayPopulationDistributions.get(day - 1).get(uniqueAgentTypes.indexOf(uniqueAgentType)).add(populationQuantity);
        }

        SocialLearning.Evolve(agents, slotsPerAgent);
    }

    /**
     * Gives a random initial time slot allocation to an Agent based on the number of time slots it requests and
     * the time slots that are currently available.
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
            }
        }
        return timeSlots;
    }
}
