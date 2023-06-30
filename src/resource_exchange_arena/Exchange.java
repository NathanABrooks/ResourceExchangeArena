package resource_exchange_arena;

import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class Exchange {

    boolean noExchanges = false;

    /**
     * With each exchange all {@link Agent}s form pairwise exchanges and are able to consider a trade with their partner for
     * one time slot.
     *
     * @param run                    {@link Integer} value identifying the current simulation run.
     * @param day                    {@link Integer} value representing the current {@link Day} being simulated.
     * @param exchange               {@link Integer} value representing the current exchange being simulated.
     * @param uniqueAgentTypes       {@link Integer} ArrayList containing each unique {@link Agent} type that exists when the simulation begins.
     * @param agents                 {@link ArrayList} of all the {@link Agent}s that exist in the current simulation.
     * @param eachRoundDataCSVWriter Used to store data regarding the state of the system at the end of each round.
     * @throws IOException On input error.
     * @see IOException
     */
    Exchange(
            int run,
            int day,
            int exchange,
            ArrayList<Integer> uniqueAgentTypes,
            @NotNull ArrayList<Agent> agents,
            FileWriter eachRoundDataCSVWriter
    ) throws IOException {

        ArrayList<ArrayList<Integer>> advertisingBoard = new ArrayList<>();

        // Reset the check for whether each Agent has made an interaction this round.
        agents.forEach(a -> a.setMadeInteraction(false));

        // Exchanges start by Agents advertising time slots they may be willing to exchange.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        agents.forEach(a -> {
            ArrayList<Integer> unlockedTimeSlots = a.publishUnlockedTimeSlots();
            if (!unlockedTimeSlots.isEmpty()) {
                ArrayList<Integer> advert = new ArrayList<>();
                advert.add(a.agentID);
                advert.addAll(unlockedTimeSlots);
                advertisingBoard.add(advert);
            }
        });

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
                        if (b.agentID == chosenAdvert.get(0) && !b.madeInteraction()) {
                            b.receiveExchangeRequest(request, a.getAgentType());
                            b.setMadeInteraction(true);
                            break;
                        }
                    }
                }
            }
        }

        // Agents who have received a request consider it.
        Collections.shuffle(agents, ResourceExchangeArena.random);
        agents.stream().filter(a -> !a.getExchangeRequestReceived().isEmpty()).forEach(a -> {
            boolean accepted = a.considerRequest();
            if (!accepted)
                agents.stream().filter(b -> b.agentID == a.getExchangeRequest().get(0)).findFirst().ifPresent(Agent::requestRejected);
        });


        int successfulExchanges = 0;

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
                                boolean scGain = b.completeRequestedExchange(offer, a.agentID, a.getAgentType());
                                boolean scLoss = a.completeReceivedExchange(offer, b.getAgentType());
                                if (scGain) a.gainedSocialCapital();
                                if (scLoss) b.lostSocialCapital();
                                successfulExchanges++;
                            }
                            break;
                        }
                    }
                }
                a.setExchangeRequestApproved(false);
            }
            // Clear the agents accepted offers list before the next exchange round.
            if (!a.getExchangeRequestReceived().isEmpty()) a.setExchangeRequestReceived();
        }

        if (successfulExchanges == 0) noExchanges = true;

        // The average end of round satisfaction is stored for each Agent type.
        // This data can later be averaged over simulation runs and added to the individual data file.
        for (int uniqueAgentType : uniqueAgentTypes)
            Utilities.write(eachRoundDataCSVWriter, String.valueOf(run), ",",
                    String.valueOf(day), ",",
                    String.valueOf(exchange), ",",
                    String.valueOf(uniqueAgentType), ",",
                    String.valueOf(CalculateSatisfaction.averageAgentSatisfaction(agents, uniqueAgentType)), "\n");

    }
}
