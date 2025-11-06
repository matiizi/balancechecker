package org.example;

import java.time.LocalDate;


public abstract class Transaction {
    protected double amount;
//    protected LocalDate date;
    protected String category;
    protected String description;

    public Transaction(double amount, String category, String description) {
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public abstract boolean isIncome();
}
