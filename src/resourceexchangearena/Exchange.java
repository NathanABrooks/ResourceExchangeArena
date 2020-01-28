package resourceexchangearena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

class Exchange {
    Exchange(
            ArrayList<Agent> shuffledAgents,
            int day,
            int exchange,
            FileWriter individualCSVWriter,
            ArrayList<Agent> agents,
            int[] daysOfInterest,
            ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions,
            ArrayList<Integer> uniqueAgentTypes
    ) throws IOException {

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
            individualCSVWriter.append(String.valueOf(ResourceExchangeArena.seed));
            individualCSVWriter.append(",");
            individualCSVWriter.append(String.valueOf(day));
            individualCSVWriter.append(",");
            individualCSVWriter.append(String.valueOf(exchange));
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
        if (IntStream.of(daysOfInterest).anyMatch(val -> val == day)) {
            for (int uniqueAgentType : uniqueAgentTypes) {
                double averageSatisfactionForType = CalculateSatisfaction.averageAgentSatisfaction(agents, uniqueAgentType);
                ArrayList<Double> endOfRoundAverageSatisfaction = new ArrayList<>();
                endOfRoundAverageSatisfaction.add((double) day);
                endOfRoundAverageSatisfaction.add((double) exchange);
                endOfRoundAverageSatisfaction.add((double) uniqueAgentType);
                endOfRoundAverageSatisfaction.add(averageSatisfactionForType);

                endOfRoundAverageSatisfactions.add(endOfRoundAverageSatisfaction);
            }
        }
    }
}
