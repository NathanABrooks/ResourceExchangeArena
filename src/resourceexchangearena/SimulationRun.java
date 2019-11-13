package resourceexchangearena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

class SimulationRun {
    // List of all the Agents that are part of the current simulation.
    static ArrayList<Agent> agents = new ArrayList<>();

    // List of all the possible allocations that exist in the current simulation.
    private static List<Integer> availableTimeSlots = new ArrayList<>();


    static void simulate(FileWriter averageCSVWriter, FileWriter individualCSVWriter) throws IOException {
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
            // Create a copy of the Agents list that can be shuffled so Agents act in a random order.
            List<Agent> shuffledAgents = new ArrayList<>(agents);

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

            double randomAllocations = CalculateSatisfaction.averageAgentSatisfaction(agents);
            double optimumAllocations = CalculateSatisfaction.optimumAgentSatisfaction(agents);

            // The random and optimum average satisfaction scores are calculated before exchanges take place.
            averageCSVWriter.append(String.valueOf(ArenaEnvironment.seed));
            averageCSVWriter.append(",");
            averageCSVWriter.append(String.valueOf(j));
            averageCSVWriter.append(",");
            averageCSVWriter.append(String.valueOf(randomAllocations));
            averageCSVWriter.append(",");
            averageCSVWriter.append(String.valueOf(optimumAllocations));

            for (int k = 1; k <= ArenaEnvironment.EXCHANGES; k++) {
                ArrayList<ArrayList<Integer>> advertisingBoard = new ArrayList<>();

                // Reset the check for whether each Agent has made an interaction this round.
                for (Agent a : agents) {
                    a.setMadeInteraction(false);
                }

                // Exchanges start by Agents advertising time slots they may be willing to exchange.
                Collections.shuffle(shuffledAgents, ResourceExchangeArena.random);
                for (Agent a : shuffledAgents) {
                    ArrayList<Integer> unlockedTimeSlots = a.publishUnlockedTimeSlots();
                    if (!unlockedTimeSlots.isEmpty()) {
                        ArrayList<Integer> advert = new ArrayList<>();
                        advert.add(a.agentID);
                        advert.addAll(a.publishUnlockedTimeSlots());
                        advertisingBoard.add(advert);
                    }
                }

                // Each Agent has the opportunity to make exchange requests for advertised time slots.
                Collections.shuffle(shuffledAgents, ResourceExchangeArena.random);
                for (Agent a : shuffledAgents) {
                    if (a.canMakeInteraction()) {
                        ArrayList<Integer> chosenAdvert = a.requestExchange(advertisingBoard);
                        a.setMadeInteraction(true);
                        if (!chosenAdvert.isEmpty()) {
                            // Select an unwanted time slot to offer in the exchange.
                            ArrayList<Integer> unwantedTimeSlots = a.publishUnwantedTimeSlots();
                            int selector = ResourceExchangeArena.random.nextInt(unwantedTimeSlots.size());
                            int unwantedTimeSlot = unwantedTimeSlots.get(selector);

                            ArrayList<Integer> request = new ArrayList<>();
                            request.add(a.agentID);
                            request.add(chosenAdvert.get(1));
                            request.add(unwantedTimeSlot);

                            // The agent who offered the requested time slot receives the exchange request.
                            for (Agent b : agents) {
                                if (b.agentID == chosenAdvert.get(0)) {
                                    b.receiveExchangeRequest(request);
                                    b.setMadeInteraction(true);
                                    break;
                                }
                            }
                        }
                    }
                }

                // Agents who have received a request consider it.
                Collections.shuffle(shuffledAgents, ResourceExchangeArena.random);
                for (Agent a : shuffledAgents) {
                    if (!a.getExchangeRequestReceived().isEmpty()) {
                        a.considerRequest();
                    }
                }

                // Agents confirm and complete approved requests if they are able to do so, and update
                // their relations with other Agents accordingly.
                Collections.shuffle(shuffledAgents, ResourceExchangeArena.random);
                for (Agent a : shuffledAgents) {
                    if (a.getExchangeRequestApproved()) {
                        ArrayList<Integer> offer = a.getExchangeRequestReceived();
                        if (a.finalCheck(offer.get(1))) {
                            for (Agent b : agents) {
                                if (b.agentID == offer.get(0)) {
                                    if (b.finalCheck(offer.get(2))) {
                                        b.completeRequestedExchange(offer, a.agentID);
                                        a.completeReceivedExchange(offer);
                                    }
                                    break;
                                }
                            }
                        }
                        a.setExchangeRequestApproved();
                    }
                    // Clear the agents accepted offers list before the next exchange round.
                    if (!a.getExchangeRequestReceived().isEmpty()) {
                        a.setExchangeRequestReceived();
                    }
                }

                for (Agent a : agents) {
                    individualCSVWriter.append(String.valueOf(ArenaEnvironment.seed));
                    individualCSVWriter.append(",");
                    individualCSVWriter.append(String.valueOf(j));
                    individualCSVWriter.append(",");
                    individualCSVWriter.append(String.valueOf(k));
                    individualCSVWriter.append(",");
                    individualCSVWriter.append(String.valueOf(a.agentID));
                    individualCSVWriter.append(",");
                    individualCSVWriter.append(String.valueOf(a.getAgentType()));
                    individualCSVWriter.append(",");
                    individualCSVWriter.append(String.valueOf(a.calculateSatisfaction(null)));
                    individualCSVWriter.append("\n");
                }

                // The average end of round satisfaction is stored for each Agent type if the current day exists in
                // the daysOfInterest array. This data can later be averaged over simulation runs and added to
                // the prePreparedIndividualFile.
                int currentDay = j;
                if (IntStream.of(ArenaEnvironment.daysOfInterest).anyMatch(val -> val == currentDay)) {
                    for (int uniqueAgentType : ArenaEnvironment.uniqueAgentTypes) {
                        double averageSatisfactionForType = CalculateSatisfaction.averageAgentSatisfaction(agents, uniqueAgentType);
                        ArrayList<Double> endOfRoundAverageSatisfaction = new ArrayList<>();
                        endOfRoundAverageSatisfaction.add((double) j);
                        endOfRoundAverageSatisfaction.add((double) k);
                        endOfRoundAverageSatisfaction.add((double) uniqueAgentType);
                        endOfRoundAverageSatisfaction.add(averageSatisfactionForType);

                        ArenaEnvironment.endOfRoundAverageSatisfactions.add(endOfRoundAverageSatisfaction);
                    }
                }
            }

            // The average end of day satisfaction is stored for each Agent type to later be averaged
            // and added to the prePreparedAverageFile.
            ArrayList<Double> endOfDayAverageSatisfaction = new ArrayList<>();
            endOfDayAverageSatisfaction.add((double) j);
            endOfDayAverageSatisfaction.add(randomAllocations);
            endOfDayAverageSatisfaction.add(optimumAllocations);

            // Store the end of day average satisfaction for each agent type.
            for (int uniqueAgentType : ArenaEnvironment.uniqueAgentTypes) {
                double typeAverageSatisfaction = CalculateSatisfaction.averageAgentSatisfaction(agents, uniqueAgentType);
                averageCSVWriter.append(",");
                averageCSVWriter.append(String.valueOf(typeAverageSatisfaction));
                endOfDayAverageSatisfaction.add(typeAverageSatisfaction);
            }
            // Temporarily store the end of day average variance for each agent type.
            for (int uniqueAgentType : ArenaEnvironment.uniqueAgentTypes) {
                double typeAverageSatisfactionSD = CalculateSatisfaction.averageSatisfactionStandardDeviation(agents, uniqueAgentType);
                endOfDayAverageSatisfaction.add(typeAverageSatisfactionSD);
            }
            averageCSVWriter.append("\n");
            ArenaEnvironment.endOfDayAverageSatisfactions.add(endOfDayAverageSatisfaction);

            for (Integer uniqueAgentType : ArenaEnvironment.uniqueAgentTypes) {
                int populationQuantity = 0;
                for (Agent a : agents) {
                    if (a.getAgentType() == uniqueAgentType) {
                        populationQuantity++;
                    }
                }
                ArenaEnvironment.endOfDayPopulationDistributions.get(j - 1).get(ArenaEnvironment.uniqueAgentTypes.indexOf(uniqueAgentType)).add(populationQuantity);
            }

            SocialLearning.Evolve(agents);
        }
    }

    /**
     * Gives a random initial time slot allocation to an Agent based on the number of time slots it requests and
     * the time slots that are currently available.
     *
     * @param requestedTimeSlots The time slots that the Agent has requested.
     * @return ArrayList<Integer> Returns a list of time slots to allocated to the Agent.
     */
    private static ArrayList<Integer> getRandomInitialAllocation(ArrayList<Integer> requestedTimeSlots) {                // TODO: CAN ASSIGN SAME SLOT TWICE TO AN AGENT, NEED TO USE PSEUDO RANDOM APPROACH WITH HASH MAPS, ASSIGNING MOST COMMONLY AVAILABLE SLOTS FIRST TO SOLVE THIS.
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
