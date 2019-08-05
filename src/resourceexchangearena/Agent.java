package resourceexchangearena;

import java.util.ArrayList;
import java.util.Random;

/**
 * Agents represent the individual resource consumers in the simulation.
 */
class Agent {
    // Unique identifier for the agent.
    int agentID;

    // Instance variables for the agents state, relations and exchanges to consider.
    private int agentType;
    private int numberOfTimeSlotsRequested;
    private ArrayList<Integer> requestedTimeSlots = new ArrayList<>();
    private ArrayList<Integer> allocatedTimeSlots = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> favoursOwed = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> favoursGiven = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> exchangeRequestsReceived = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> exchangeRequestsApproved = new ArrayList<>();

    // Agent constructor
    Agent(int agentID, int agentType){
        this.agentID = agentID;
        this.agentType = agentType;

        // Set the number of time slots that the Agent will request.
        numberOfTimeSlotsRequested = 4;

        // Add the Agent to the arenas list of participating Agents.
        ExchangeArena.agents.add(this);
    }

    // Receives an allocation of time slots and updates the Agents stored allocation.
    void initializeFavoursStore() {
        if (!favoursGiven.isEmpty()) {
            favoursGiven.clear();
        }
        if (!favoursOwed.isEmpty()) {
            favoursOwed.clear();
        }
        for (Agent a: ExchangeArena.agents) {
            if (a.agentID != agentID) {
                // Array list that pairs an agentID with the favours owed.
                ArrayList<Integer> favoursOwedRelation = new ArrayList<>();

                // Array list that pairs an agentID with the favours given.
                ArrayList<Integer> favoursGivenRelation = new ArrayList<>();

                // Initially, no favours are owed or have been given to any other Agent.
                favoursOwedRelation.add(a.agentID);
                favoursOwedRelation.add(0);

                favoursGivenRelation.add(a.agentID);
                favoursGivenRelation.add(0);

                // Store the agentID and value combinations in the corresponding all favours lists.
                favoursOwed.add(favoursOwedRelation);
                favoursGiven.add(favoursGivenRelation);
            }
        }
    }

    // Returns the time slots currently allocated to the Agent.
    int getAgentType() {
        return agentType;
    }

    // Request a number of time slots.
    ArrayList<Integer> requestTimeSlots() {
        if (!requestedTimeSlots.isEmpty()){
            requestedTimeSlots.clear();
        }

        for (int i = 1; i <= numberOfTimeSlotsRequested; i++) {
            // Get the simulations seeded Random object.
            Random random = ExchangeArena.random;

            // Selects a random integer representing the time slot between 1 and the total number of available slots.
            int timeSlot = random.nextInt(ExchangeArena.UNIQUE_TIME_SLOTS) + 1;

            // Ensures all requested time slots are unique.
            if (requestedTimeSlots.contains(timeSlot)) {
                i--;
            } else {
                requestedTimeSlots.add(timeSlot);
            }
        }
        return requestedTimeSlots;
    }

    // Returns the time slots currently requested by the Agent.
    ArrayList<Integer> publishRequestedTimeSlots() {
        return requestedTimeSlots;
    }

    // Receives an allocation of time slots and updates the Agents stored allocation.
    void receiveAllocatedTimeSlots(ArrayList<Integer> allocatedTimeSlots) {
        this.allocatedTimeSlots = allocatedTimeSlots;
    }

    // Returns the time slots currently allocated to the Agent.
    ArrayList<Integer> publishAllocatedTimeSlots() {
        return allocatedTimeSlots;
    }

    // Returns the time slots the Agent may be willing to exchange.
    ArrayList<Integer> publishUnlockedTimeSlots() {
        ArrayList<Integer> unlockedTimeSlots;
        // Different agent types may offer different requirements for whether or not a time slot may be exchanged.
        switch(agentType) {
            case ExchangeArena.SOCIAL:
            case ExchangeArena.SELFISH:
                unlockedTimeSlots = new ArrayList<>(nonExistingTimeSlots(allocatedTimeSlots, requestedTimeSlots));
                break;
            default:
                // If a human readable name doesnt exist, return the integer agentType as a string.
                unlockedTimeSlots = allocatedTimeSlots;
        }
        return unlockedTimeSlots;
    }

    ArrayList<Integer> publishUnwantedTimeSlots() {
        return nonExistingTimeSlots(allocatedTimeSlots, requestedTimeSlots);
    }

    // Returns all time slots in the first array list parameter that dont exist in the second array list parameter.
    private ArrayList<Integer> nonExistingTimeSlots(
            ArrayList<Integer> potentialTimeSlots,
            ArrayList<Integer> timeSlotsToAvoid) {
        // By making a new copy of the time slots to avoid, the array list can be modified without modifying a
        // referenced list.
        ArrayList<Integer> localTimeSlotsToAvoid = new ArrayList<>(timeSlotsToAvoid);
        ArrayList<Integer> timeSlots = new ArrayList<>();
        for(int i = 1; i <= potentialTimeSlots.size(); i++){
            int timeSlot = potentialTimeSlots.get(i - 1);
            if (!localTimeSlotsToAvoid.contains(timeSlot)) {
                timeSlots.add(timeSlot);
            } else {
                localTimeSlotsToAvoid.remove(Integer.valueOf(timeSlot));
            }
        }
        return timeSlots;
    }

    ArrayList<Integer> requestExchange(ArrayList<ArrayList<Integer>> advertisingBoard) {
        ArrayList<Integer> targetTimeSlots = nonExistingTimeSlots(requestedTimeSlots, allocatedTimeSlots);
        // If no new time slots are wanted, this Agent has no need to make new requests.
        if (!targetTimeSlots.isEmpty()) {
            // The adverts that this Agent is interested in.
            ArrayList<ArrayList<Integer>> interestingAdverts = new ArrayList<>();

            // The exchange that this Agent has identified as being the most likely to succeed.
            ArrayList<Integer> chosenAdvert;

            // Search the advertising board for adverts of interest.
            for (ArrayList<Integer> advert : advertisingBoard) {
                for (int j = 1; j < advert.size(); j++) {
                    if (targetTimeSlots.contains(advert.get(j))) {
                        // Only take the part of the advert that is interesting, and split adverts with multiple
                        // targeted time slots into two adverts.
                        ArrayList<Integer> interestingAdvert = new ArrayList<>();
                        interestingAdvert.add(advert.get(0));
                        interestingAdvert.add(advert.get(j));
                        interestingAdverts.add(interestingAdvert);
                    }
                }
            }

            if (interestingAdverts.size() == 0) {
                // If no adverts were found to be of interest, return null.
                return null;
            } else if (interestingAdverts.size() == 1) {
                // If only one advert was found to be of interest, return it.
                chosenAdvert = interestingAdverts.get(0);
            } else {
                // Choose a random suitable advert to accept.
                int selector = ExchangeArena.random.nextInt(interestingAdverts.size());
                chosenAdvert = interestingAdverts.get(selector);
            }
            return chosenAdvert;
        }
        return null;
    }

    // Receives a request of a time slot exchange and adds it to the list of requests.
    void receiveExchangeRequest(ArrayList<Integer> request) {
        exchangeRequestsReceived.add(request);
    }

    // Determine if offered requests will be accepted, and then order them by priority.
    void considerRequests() {
        // Get the Agents current satisfaction level to compare to.
        double currentSatisfaction = calculateSatisfaction(null);
        for (ArrayList<Integer> request : exchangeRequestsReceived) {
            ArrayList<Integer> potentialAllocatedTimeSlots = new ArrayList<>(allocatedTimeSlots);
            // Check this Agent still has the time slot requested.
            if (potentialAllocatedTimeSlots.contains(request.get(1))) {
                // Remove the requested time slot.
                potentialAllocatedTimeSlots.remove(request.get(1));
                // Replace the requested slot with the requesting agents unwanted time slot.
                potentialAllocatedTimeSlots.add(request.get(2));
                // Calculate the potential new satisfaction level.
                double newSatisfaction = calculateSatisfaction(potentialAllocatedTimeSlots);
                if (agentType == ExchangeArena.SOCIAL) {
                    // Social Agents accept offers that improve or dont affect their individual satisfaction.
                    if (newSatisfaction > currentSatisfaction) {
                        exchangeRequestsApproved.add(request);
                    } else {
                        int favoursOwedToRequester = 0;
                        int favoursGivenToRequester = 0;
                        for (ArrayList<Integer> favours : favoursOwed) {
                            if (favours.get(0).equals(request.get(0))) {
                                favoursOwedToRequester = favours.get(1);
                                break;
                            }
                        }
                        for (ArrayList<Integer> favours : favoursGiven) {
                            if (favours.get(0).equals(request.get(0))) {
                                favoursGivenToRequester = favours.get(1);
                                break;
                            }
                        }
                        if (favoursOwedToRequester > favoursGivenToRequester) {
                            exchangeRequestsApproved.add(request);
                        }
                    }
                } else {
                    // Selfish Agents and Agents with no considered type use the default selfish approach.
                    // Selfish Agents only accept offers that improve their individual satisfaction.
                    if (newSatisfaction > currentSatisfaction) {
                        exchangeRequestsApproved.add(request);
                    }
                }
            }
        }
        // Once all requests have been considered, clear the array list for the next exchange round.
        exchangeRequestsReceived.clear();
    }


    ArrayList<ArrayList<Integer>> getExchangeRequestsApproved() {
        return exchangeRequestsApproved;
    }

    boolean finalCheck(int timeSlot) {
        return allocatedTimeSlots.contains(timeSlot);
    }

    void completeRequestedExchange(ArrayList<Integer> offer, int agentID) {
        allocatedTimeSlots.remove(offer.get(2));
        allocatedTimeSlots.add(offer.get(1));
        if (agentType == ExchangeArena.SOCIAL) {
            for (ArrayList<Integer> favours : favoursOwed) {
                if (favours.get(0).equals(agentID)) {
                    int currentFavour = favours.get(1);
                    favours.set(1, currentFavour + 1);
                    break;
                }
            }
        }
    }

    void completeReceivedExchange(ArrayList<Integer> offer) {
        double previousSatisfaction = calculateSatisfaction(allocatedTimeSlots);
        allocatedTimeSlots.remove(offer.get(1));
        allocatedTimeSlots.add(offer.get(2));
        double newSatisfaction = calculateSatisfaction(allocatedTimeSlots);
        if ((agentType == ExchangeArena.SOCIAL) && (newSatisfaction <= previousSatisfaction)) {
            for (ArrayList<Integer> favours : favoursGiven) {
                if (favours.get(0).equals(offer.get(0))) {
                    int currentFavour = favours.get(1);
                    favours.set(1, currentFavour + 1);
                    break;
                }
            }
        }
    }

    void clearAcceptedRequests() {
        exchangeRequestsApproved.clear();
    }

    double calculateSatisfaction() {
        return (calculateSatisfaction(this.allocatedTimeSlots));
    }

    // Calculates the Agents satisfaction by comparing its requested and allocated time slots.
    double calculateSatisfaction(ArrayList<Integer> allocatedTimeSlots) {
        if (allocatedTimeSlots == null) {
            allocatedTimeSlots = this.allocatedTimeSlots;
        }

        // Count the number of allocated time slots that match the Agents requested time slots.
        int satisfiedSlots = 0;
        for(int i = 1; i <= allocatedTimeSlots.size(); i++){
            int timeSlot = allocatedTimeSlots.get(i - 1);
            if (requestedTimeSlots.contains(timeSlot)) {
                satisfiedSlots++;
            }
        }
        // Return the average satisfaction of the allocated time slots, between 1 and 0.
        return ((double)satisfiedSlots) / numberOfTimeSlotsRequested;
    }
}
