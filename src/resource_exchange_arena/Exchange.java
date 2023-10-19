package resource_exchange_arena;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class Exchange {
    /**
     * With each exchange all agents form pairwise exchanges and are able to consider a trade with their partner for
     * one time-slot.
     *
     * @param run Integer value identifying the current simulation run.
     * @param day Integer value representing the current day being simulated.
     * @param exchange Integer value representing the current exchange being simulated.
     * @param uniqueAgentTypes Integer ArrayList containing each unique agent type that exists when the
     *                         simulation begins.
     * @param agents Array List of all the agents that exist in the current simulation.
     * @param eachRoundDataCSVWriter Used to store data ragarding the state of the system at the end of each round.
     * @exception IOException On input error.
     * @see IOException
     */

    Boolean noExchanges = false;
    
    Exchange(
        int run,
            int day,
            int exchange,
            ArrayList<Integer> uniqueAgentTypes,
            ArrayList<Agent> agents,
            FileWriter eachRoundDataCSVWriter
    ) throws IOException {

        ArrayList<ArrayList<Integer>> advertisingBoard = new ArrayList<>();

        // Reset the check for whether each Agent has made an interaction this round.
        for (Agent a : agents) {
            a.setMadeInteraction(false);
        }

        // Exchanges start by Agents advertising time-slots they may be willing to exchange.
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

        // Each Agent has the opportunity to make exchange requests for advertised time-slots.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        for (Agent a : agents) {
            if (!a.madeInteraction()) {
                ArrayList<Integer> chosenAdvert = a.requestExchange(advertisingBoard);
                a.setMadeInteraction(true);
                if (!chosenAdvert.isEmpty()) {
                    // Select an unwanted time-slot to offer in the exchange.
                    ArrayList<Integer> unwantedTimeSlots = a.publishUnlockedTimeSlots();
                    int unwantedTimeSlot = unwantedTimeSlots.get(0); // Get the least wanted time-slot.

                    ArrayList<Integer> request = new ArrayList<>();
                    request.add(a.agentID);
                    request.add(chosenAdvert.get(1));
                    request.add(unwantedTimeSlot);

                    // The agent who offered the requested time-slot receives the exchange request.
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


        int successfullExchanges = 0;

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
                                successfullExchanges++;
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

        if (successfullExchanges == 0) {
            noExchanges = true;
        }

        // The average end of round satisfaction is stored for each Agent type.
        // This data can later be averaged over simulation runs and added to the individual data file.
        for (int uniqueAgentType : uniqueAgentTypes) {
            double averageSatisfactionForType = CalculateSatisfaction.averageAgentSatisfaction(agents, uniqueAgentType);

            eachRoundDataCSVWriter.append(String.valueOf(run));
            eachRoundDataCSVWriter.append(",");
            
            eachRoundDataCSVWriter.append(String.valueOf(day));
            eachRoundDataCSVWriter.append(",");
            
            eachRoundDataCSVWriter.append(String.valueOf(exchange));
            eachRoundDataCSVWriter.append(",");
            
            eachRoundDataCSVWriter.append(String.valueOf(uniqueAgentType));
            eachRoundDataCSVWriter.append(",");

            eachRoundDataCSVWriter.append(String.valueOf(averageSatisfactionForType));
            eachRoundDataCSVWriter.append("\n");
        }

    }
}
