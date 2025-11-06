package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private List<Transaction> transactions = new ArrayList<>();
    private Map<String, BudgetCategory> budgets = new HashMap<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addBudgetCategory(BudgetCategory category) {
        budgets.put(category.getName(), category);
    }

    public BudgetCategory getBudgetCategory(String name) {
        return budgets.get(name);
    }

    public Map<String, BudgetCategory> getBudgets() {
        return budgets;
    }

    // Методы подсчета доходов, расходов и проверок бюджета будут позже
}
