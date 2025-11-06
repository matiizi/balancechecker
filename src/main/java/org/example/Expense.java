package org.example;

import java.time.LocalDate;

public class Expense extends Transaction {
    public Expense(double amount, String category, String description) {
        super(amount, category, description);
    }

    @Override
    public boolean isIncome() {
        return false;
    }
}
