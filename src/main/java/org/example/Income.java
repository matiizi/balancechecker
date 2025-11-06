package org.example;

import java.time.LocalDate;

public class Income extends Transaction {
    public Income(double amount, String category, String description) {
        super(amount, category, description);
    }

    @Override
    public boolean isIncome() {
        return true;
    }
}
