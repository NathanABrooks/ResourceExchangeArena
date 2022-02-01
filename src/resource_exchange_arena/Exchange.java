package resource_exchange_arena;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

class Exchange {
    /**
     * With each exchange all agents form pairwise exchanges and are able to consider a trade with their partner for
     * one time slot.
     *
     * @param daysOfInterest Integer array containing the days be shown in graphs produced after the simulation.
     * @param day Integer value representing the current day being simulated.
     * @param exchange Integer value representing the current exchange being simulated.
     * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the simulation
     *                         begins.
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param endOfRoundAverageSatisfactions Stores the average satisfaction for each agent type at the end of each
     *                                       round.
     * @exception IOException On input error.
     * @see IOException
     */
    Exchange(
            int[] daysOfInterest,
            int day,
            int exchange,
            ArrayList<Integer> uniqueAgentTypes,
            ArrayList<Agent> agents,
            ArrayList<ArrayList<Double>> endOfRoundAverageSatisfactions
    ) throws IOException {

        ArrayList<ArrayList<Integer>> advertisingBoard = new ArrayList<>();

        // Reset the check for whether each Agent has made an interaction this round.
        for (Agent a : agents) {
            a.setMadeInteraction(false);
        }

        // Exchanges start by Agents advertising time slots they may be willing to exchange.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        for (Agent a : agents) {
            ArrayList<Integer> unlockedTimeSlots = a.publishUnlockedTimeSlots();
            if (!unlockedTimeSlots.isEmpty()) {
                ArrayList<Integer> advert = new ArrayList<>();
                advert.add(a.agentID);
                advert.addAll(unlockedTimeSlots);
                advertisingBoard.add(advert);
            }
        }

        // Each Agent has the opportunity to make exchange requests for advertised time slots.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        for (Agent a : agents) {
            if (!a.madeInteraction()) {
                ArrayList<Integer> chosenAdvert = a.requestExchange(advertisingBoard);
                a.setMadeInteraction(true);
                if (!chosenAdvert.isEmpty()) {
                    // Select an unwanted time slot to offer in the exchange.
                    ArrayList<Integer> unwantedTimeSlots = a.publishUnlockedTimeSlots();
                    int selector = ResourceExchangeArena.random.nextInt(unwantedTimeSlots.size());
                    int unwantedTimeSlot = unwantedTimeSlots.get(selector);

                    ArrayList<Integer> request = new ArrayList<>();
                    request.add(a.agentID);
                    request.add(chosenAdvert.get(1));
                    request.add(unwantedTimeSlot);

                    // The agent who offered the requested time slot receives the exchange request.
                    for (Agent b : agents) {
                        if (b.agentID == chosenAdvert.get(0)) {
                            if (b.madeInteraction() == false) {
                                b.receiveExchangeRequest(request, a.getAgentType());
                                b.setMadeInteraction(true);
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Agents who have received a request consider it.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        for (Agent a : agents) {
            if (!a.getExchangeRequestReceived().isEmpty()) {
                boolean accepted = a.considerRequest();
                if (!accepted) {
                    for (Agent b : agents) {
                        if (b.agentID == a.getExchangeRequest().get(0)) {
                            b.requestRejected();
                            break;
                        }
                    }
                }
            }
        }

        // Agents confirm and complete approved requests if they are able to do so, and update their relations with
        // other Agents accordingly.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        for (Agent a : agents) {
            if (a.getExchangeRequestApproved()) {
                ArrayList<Integer> offer = a.getExchangeRequestReceived();
                if (a.finalCheck(offer.get(1))) {
                    for (Agent b : agents) {
                        if (b.agentID == offer.get(0)) {
                            if (b.finalCheck(offer.get(2))) {
                                boolean scgain = b.completeRequestedExchange(offer, a.agentID, a.getAgentType());
                                boolean scloss = a.completeReceivedExchange(offer, b.getAgentType());
                                if (scgain) {
                                    a.gainedSocialCapital();
                                }
                                if (scloss) {
                                    b.lostSocialCapital();
                                }
                            }
                            break;
                        }
                    }
                }
                a.setExchangeRequestApproved(false);
            }
            // Clear the agents accepted offers list before the next exchange round.
            if (!a.getExchangeRequestReceived().isEmpty()) {
                a.setExchangeRequestReceived();
            }
        }

        // The average end of round satisfaction is stored for each Agent type if the current day exists in the
        // daysOfInterest array. This data can later be averaged over simulation runs and added to the individual
        // data file.
        if (IntStream.of(daysOfInterest).anyMatch(val -> val == day)) {
            for (int uniqueAgentType : uniqueAgentTypes) {
                double averageSatisfactionForType =
                        CalculateSatisfaction.averageAgentSatisfaction(agents, uniqueAgentType);
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
