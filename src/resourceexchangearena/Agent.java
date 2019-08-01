package resourceexchangearena;

import java.util.ArrayList;
import java.util.Random;

// Agents represent the individual resource consumers in the simulation.
class Agent {
    // Unique numerical identifier for the Agent.
    int ID;

    // The Agents type that determines its behavior in the ExchangeArena.
    private int agentType;

    // Amount of time slots that the Agent will request.
    private int numberOfRequestedSlots;

    // Time slots requested by the Agent.
    private ArrayList<Integer> requestedTimeSlots = new ArrayList<>();

    // Time slots currently allocated to the Agent.
    private ArrayList<Integer> allocatedTimeSlots = new ArrayList<>();

    // 2D array list holding the favours this Agent owes to others.
    private ArrayList<ArrayList<Integer>> allFavoursOwed = new ArrayList<>();

    // 2D array list holding the favours this Agent has given to others.
    private ArrayList<ArrayList<Integer>> allFavoursGiven = new ArrayList<>();

    // 2D array list holding the exchange requests this Agent has received during an exchange round.
    private ArrayList<ArrayList<Integer>> allRequests = new ArrayList<>();

    // 2D array list holding the exchange requests this Agent has deemed acceptable during an exchange round.
    private ArrayList<ArrayList<Integer>> acceptableRequests = new ArrayList<>();

    // Agent constructor
    Agent(int ID, int agentType){
        // Set the agents unique ID.
        this.ID = ID;

        // Set the agents type.
        this.agentType = agentType;

        // Set the number of time slots that the Agent will request.
        numberOfRequestedSlots = 4;

        // Add the Agent to the arenas list of participating Agents.
        ExchangeArena.agents.add(this);
    }

    // Receives an allocation of time slots and updates the Agents stored allocation.
    void initializeFavoursStore() {
        if (!allFavoursGiven.isEmpty()) {
            allFavoursGiven.clear();
        }
        if (!allFavoursOwed.isEmpty()) {
            allFavoursOwed.clear();
        }
        for (Agent a: ExchangeArena.agents) {
            if (a.ID != ID) {
                // Array list that pairs an agents ID with the favours owed.
                ArrayList<Integer> favoursOwed = new ArrayList<>();

                // Array list that pairs an agents ID with the favours given.
                ArrayList<Integer> favoursGiven = new ArrayList<>();

                // Initially, no favours are owed or have been given to any other Agent.
                favoursOwed.add(a.ID);
                favoursOwed.add(0);

                favoursGiven.add(a.ID);
                favoursGiven.add(0);

                // Store the ID and value combinations in the corresponding all favours lists.
                allFavoursOwed.add(favoursOwed);
                allFavoursGiven.add(favoursGiven);

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

        for (int i = 1; i <= numberOfRequestedSlots; i++) {
            // Get the simulations seeded Random object.
            Random random = ExchangeArena.random;

            // Selects a random integer representing the time slot between 1 and the total number of available slots.
            int timeSlot = random.nextInt(ExchangeArena.TOTAL_TIME_SLOTS) + 1;

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

    // Returns the time slots the Agent may be willing to trade.
    ArrayList<Integer> publishUnlockedTimeSlots() {
        ArrayList<Integer> unlockedTimeSlots;
        // Different agent types may offer different requirements for whether or not a time slot may be traded.
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

            // The trade that this Agent has identified as being the most likely to succeed.
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
        allRequests.add(request);
    }

    // Determine if offered requests will be accepted, and then order them by priority.
    void considerRequests() {
        // Get the Agents current satisfaction level to compare to.
        double currentSatisfaction = calculateSatisfaction(null);
        for (ArrayList<Integer> request : allRequests) {
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
                        acceptableRequests.add(request);
                    } else {
                        int favoursOwed = 0;
                        int favoursGiven = 0;
                        for (ArrayList<Integer> favours : allFavoursOwed) {
                            if (favours.get(0).equals(request.get(0))) {
                                favoursOwed = favours.get(1);
                                break;
                            }
                        }
                        for (ArrayList<Integer> favours : allFavoursGiven) {
                            if (favours.get(0).equals(request.get(0))) {
                                favoursGiven = favours.get(1);
                                break;
                            }
                        }
                        if (favoursOwed > favoursGiven) {
                            acceptableRequests.add(request);
                        }
                    }
                } else {
                    // Selfish Agents and Agents with no considered type use the default selfish approach.
                    // Selfish Agents only accept offers that improve their individual satisfaction.
                    if (newSatisfaction > currentSatisfaction) {
                        acceptableRequests.add(request);
                    }
                }
            }
        }
        // Once all requests have been considered, clear the array list for the next exchange round.
        allRequests.clear();
    }


    ArrayList<ArrayList<Integer>> getAcceptableRequests() {
        return acceptableRequests;
    }

    boolean finalCheck(int timeSlot) {
        return allocatedTimeSlots.contains(timeSlot);
    }

    void completeRequestedExchange(ArrayList<Integer> offer, int agentID) {
        allocatedTimeSlots.remove(offer.get(2));
        allocatedTimeSlots.add(offer.get(1));
        if (agentType == ExchangeArena.SOCIAL) {
            for (ArrayList<Integer> favours : allFavoursOwed) {
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
            for (ArrayList<Integer> favours : allFavoursGiven) {
                if (favours.get(0).equals(offer.get(0))) {
                    int currentFavour = favours.get(1);
                    favours.set(1, currentFavour + 1);
                    break;
                }
            }
        }
    }

    void clearAcceptedRequests() {
        acceptableRequests.clear();
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
        return ((double)satisfiedSlots) / numberOfRequestedSlots;
    }
}
