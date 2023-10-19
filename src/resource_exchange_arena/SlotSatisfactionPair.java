package resource_exchange_arena;

public class SlotSatisfactionPair{
    int timeSlot;
    double satisfaction;

    /**
     * Used to associate a time-slot  with it's satisfaction level for an agent.
     *
     * @param timeSlot Integer value representing the specific time-slot.
     * @param satisfaction Double value representing the level of satisfaction.
     */
    public SlotSatisfactionPair(int timeSlot, double satisfaction) {
        this.timeSlot = timeSlot;
        this.satisfaction = satisfaction;
    }
}