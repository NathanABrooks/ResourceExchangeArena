package resourceexchangearena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Day {

    // List of all the possible allocations that exist in the current simulation.
    private static List<Integer> availableTimeSlots = new ArrayList<>();

    static void day(int i, int j, int type, FileWriter averageCSVWriter, FileWriter individualCSVWriter) throws IOException {
        // Fill the available time slots with all the slots that exist each day.
        for (int k = 1; k <= ArenaEnvironment.UNIQUE_TIME_SLOTS; k++) {
            for (int l = 1; l <= ArenaEnvironment.MAXIMUM_PEAK_CONSUMPTION; l++) {
                availableTimeSlots.add(k);
            }
        }

        // Create a copy of the Agents list that can be shuffled so Agents act in a random order.
        ArrayList<Agent> shuffledAgents = new ArrayList<>(SimulationRun.agents);

        // Agents start the day by requesting and receiving an allocation of time slots.
        Collections.shuffle(shuffledAgents, ResourceExchangeArena.random);
        for (Agent a : shuffledAgents) {
            ArrayList<Integer> requestedTimeSlots = a.requestTimeSlots();
            ArrayList<Integer> allocatedTimeSlots = getRandomInitialAllocation(requestedTimeSlots);
            a.receiveAllocatedTimeSlots(allocatedTimeSlots);
        }

        double randomAllocations = CalculateSatisfaction.averageAgentSatisfaction(SimulationRun.agents);
        double optimumAllocations = CalculateSatisfaction.optimumAgentSatisfaction(SimulationRun.agents);

        // The random and optimum average satisfaction scores are calculated before exchanges take place.
        averageCSVWriter.append(String.valueOf(ArenaEnvironment.seed));
        averageCSVWriter.append(",");
        averageCSVWriter.append(String.valueOf(j));
        averageCSVWriter.append(",");
        averageCSVWriter.append(String.valueOf(randomAllocations));
        averageCSVWriter.append(",");
        averageCSVWriter.append(String.valueOf(optimumAllocations));

        if (type >= 0) {
            for (int k = 1; k <= ArenaEnvironment.EXCHANGES; k++) {
                Exchange.exchange(shuffledAgents, j, k, individualCSVWriter);
            }
        }

        // The average end of day satisfaction is stored for each Agent type to later be averaged
        // and added to the prePreparedAverageFile.
        if (type == -2) {
            ArenaEnvironment.endOfDayAverageSatisfactionsForType.get(j - 1).add(randomAllocations);
        } else if (type == -1) {
            ArenaEnvironment.endOfDayAverageSatisfactionsForType.get(j - 1).add(optimumAllocations);
        } else {
            double typeAverageSatisfaction = CalculateSatisfaction.averageAgentSatisfaction(SimulationRun.agents, ArenaEnvironment.uniqueAgentTypes.get(type));
            ArenaEnvironment.endOfDayAverageSatisfactionsForType.get(j - 1).add(typeAverageSatisfaction);
        }

        // Store the end of day average satisfaction for each agent type.
        for (int uniqueAgentType : ArenaEnvironment.uniqueAgentTypes) {
            double typeAverageSatisfaction = CalculateSatisfaction.averageAgentSatisfaction(SimulationRun.agents, uniqueAgentType);
            averageCSVWriter.append(",");
            averageCSVWriter.append(String.valueOf(typeAverageSatisfaction));
        }
        averageCSVWriter.append("\n");

        // The end of day satisfaction is stored for each Agent if the current day exists in
        // the daysOfInterest array. This data can later be averaged over simulation runs and added to
        // the prePreparedBoxPlotFile.
        if (IntStream.of(ArenaEnvironment.daysOfInterest).anyMatch(val -> val == j)) {
            for (Agent a : SimulationRun.agents) {
                ArrayList<Double> individualSatisfaction = new ArrayList<>();
                individualSatisfaction.add((double) i);
                individualSatisfaction.add((double) j);
                individualSatisfaction.add((double) a.getAgentType());
                individualSatisfaction.add(a.calculateSatisfaction(null));

                ArenaEnvironment.endOfDayIndividualSatisfactions.add(individualSatisfaction);
            }
        }

        // Only update the user on the simulations status every 10 days to reduce output spam.
        if (j % 10 == 0) {
            System.out.println("Agent Type: " + type + "  RUN: " + i + "  DAY: " + j);
        }
    }

    static void dayVariedAgents(int i, int j, FileWriter averageCSVWriter, FileWriter individualCSVWriter) throws IOException{
        // Create a copy of the Agents list that can be shuffled so Agents act in a random order.
        ArrayList<Agent> shuffledAgents = new ArrayList<>(SimulationRun.agents);

        // Fill the available time slots with all the slots that exist each day.
        for (int k = 1; k <= ArenaEnvironment.UNIQUE_TIME_SLOTS; k++) {
            for (int l = 1; l <= ArenaEnvironment.MAXIMUM_PEAK_CONSUMPTION; l++) {
                availableTimeSlots.add(k);
            }
        }
        // Agents start the day by requesting and receiving an allocation of time slots.
        Collections.shuffle(shuffledAgents, ResourceExchangeArena.random);
        for (Agent a : shuffledAgents) {
            ArrayList<Integer> requestedTimeSlots = a.requestTimeSlots();
            ArrayList<Integer> allocatedTimeSlots = getRandomInitialAllocation(requestedTimeSlots);
            a.receiveAllocatedTimeSlots(allocatedTimeSlots);
        }

        double randomAllocations = CalculateSatisfaction.averageAgentSatisfaction(SimulationRun.agents);
        double optimumAllocations = CalculateSatisfaction.optimumAgentSatisfaction(SimulationRun.agents);

        // The random and optimum average satisfaction scores are calculated before exchanges take place.
        averageCSVWriter.append(String.valueOf(ArenaEnvironment.seed));
        averageCSVWriter.append(",");
        averageCSVWriter.append(String.valueOf(j));
        averageCSVWriter.append(",");
        averageCSVWriter.append(String.valueOf(randomAllocations));
        averageCSVWriter.append(",");
        averageCSVWriter.append(String.valueOf(optimumAllocations));

        for (int k = 1; k <= ArenaEnvironment.EXCHANGES; k++) {
            Exchange.exchangeVariedAgents(shuffledAgents, j, k, individualCSVWriter);
        }

        // The average end of day satisfaction is stored for each Agent type to later be averaged
        // and added to the prePreparedAverageFile.
        ArrayList<Double> endOfDayAverageSatisfaction = new ArrayList<>();
        endOfDayAverageSatisfaction.add((double) j);
        endOfDayAverageSatisfaction.add(randomAllocations);
        endOfDayAverageSatisfaction.add(optimumAllocations);

        // Store the end of day average satisfaction for each agent type.
        for (int uniqueAgentType : ArenaEnvironment.uniqueAgentTypes) {
            double typeAverageSatisfaction = CalculateSatisfaction.averageAgentSatisfaction(SimulationRun.agents, uniqueAgentType);
            averageCSVWriter.append(",");
            averageCSVWriter.append(String.valueOf(typeAverageSatisfaction));
            endOfDayAverageSatisfaction.add(typeAverageSatisfaction);
        }
        // Temporarily store the end of day average variance for each agent type.
        for (int uniqueAgentType : ArenaEnvironment.uniqueAgentTypes) {
            double typeAverageSatisfactionSD = CalculateSatisfaction.averageSatisfactionStandardDeviation(SimulationRun.agents, uniqueAgentType);
            endOfDayAverageSatisfaction.add(typeAverageSatisfactionSD);
        }
        averageCSVWriter.append("\n");
        ArenaEnvironment.endOfDayAverageSatisfactions.add(endOfDayAverageSatisfaction);

        for (Integer uniqueAgentType : ArenaEnvironment.uniqueAgentTypes) {
            int populationQuantity = 0;
            for (Agent a : SimulationRun.agents) {
                if (a.getAgentType() == uniqueAgentType) {
                    populationQuantity++;
                }
            }
            ArenaEnvironment.endOfDayPopulationDistributions.get(j - 1).get(ArenaEnvironment.uniqueAgentTypes.indexOf(uniqueAgentType)).add(populationQuantity);
        }

        SocialLearning.Evolve(SimulationRun.agents);
    }

    /**
     * Gives a random initial time slot allocation to an Agent based on the number of time slots it requests and
     * the time slots that are currently available.
     *
     * @param requestedTimeSlots The time slots that the Agent has requested.
     * @return ArrayList<Integer> Returns a list of time slots to allocated to the Agent.
     */
    private static ArrayList<Integer> getRandomInitialAllocation(ArrayList<Integer> requestedTimeSlots) {
        ArrayList<Integer> timeSlots = new ArrayList<>();

        for (int i = 1; i <= requestedTimeSlots.size(); i++) {
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
