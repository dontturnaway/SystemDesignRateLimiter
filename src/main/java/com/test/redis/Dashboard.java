package com.test.redis;

import java.util.List;

public class Dashboard {
    private int total;
    private List<Integer> cards;

    // Constructors, getters, setters
    public Dashboard(int total, List<Integer> cards) {
        this.total = total;
        this.cards = cards;
    }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public List<Integer> getCards() { return cards; }
    public void setCards(List<Integer> cards) { this.cards = cards; }
}