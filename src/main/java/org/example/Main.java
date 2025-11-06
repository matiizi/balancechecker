package org.example;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static UserService userService = new UserService();
    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            if (currentUser != null) {
//                userService.saveData();
//            }
//        }));

        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showAuthMenu() {
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Выход");
        System.out.print("Выберите действие: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                System.exit(0);
            default:
                System.out.println("Некорректный ввод.");
        }
    }

    private static void login() {
        System.out.print("Логин: ");
        String username = scanner.nextLine();
        System.out.print("Пароль: ");
        String password = scanner.nextLine();
        User user = userService.loginUser(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Вход выполнен успешно.");
        }
    }

    private static void register() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        if (userService.registerUser(username, password)) {
            System.out.println("Регистрация успешна. Можете войти.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\nГлавное меню:");
        System.out.println("1. Добавить доход");
        System.out.println("2. Добавить расход");
        System.out.println("3. Показать все транзакции");
        System.out.println("4. Добавить/редактировать бюджет категории");
        System.out.println("5. Показать статистику по бюджету");
        System.out.println("6. Проверить финансовое состояние");
        System.out.println("7. Выйти");
        System.out.print("Выберите действие: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                addTransaction(true);
                break;
            case "2":
                addTransaction(false);
                break;
            case "3":
                listTransactions();
                break;
            case "4":
                addOrEditBudget();
                break;
            case "5":
                showBudgetStats();
                break;
            case "6":
                checkFinancialHealth();
                break;
            case "7":
                userService.saveData();
                currentUser = null;
                System.out.println("Вы вышли.");
                break;
            default:
                System.out.println("Некорректный выбор.");
        }
    }

    private static void addTransaction(boolean isIncome) {
        try {
            System.out.print("Сумма: ");
            double amount = Double.parseDouble(scanner.nextLine());
            if (amount <= 0) {
                System.out.println("Сумма должна быть положительной.");
                return;
            }
            System.out.print("Категория: ");
            String category = scanner.nextLine();
            System.out.print("Описание: ");
            String description = scanner.nextLine();
//            LocalDate date = LocalDate.now();

            Transaction transaction = isIncome ?
                    new Income(amount, category, description) :
                    new Expense(amount, category, description);

            currentUser.getWallet().addTransaction(transaction);
            userService.saveData();
            System.out.println("Транзакция добавлена.");
            if (!isIncome) {
                checkBudgetAfterTransaction(transaction.getCategory());
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат суммы.");
        }
    }

    private static void checkBudgetAfterTransaction(String category) {
        Wallet wallet = currentUser.getWallet();
        BudgetCategory budget = wallet.getBudgetCategory(category);

        if (budget != null) {
            double spent = wallet.getTransactions().stream()
                    .filter(t -> !t.isIncome() && t.getCategory().equals(category))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double remaining = budget.getBudgetLimit() - spent;

            if (remaining < 0) {
                System.out.println("Только что превышен лимит бюджета '" + category + "'!");
            }
        }
    }

    private static void listTransactions() {
        var transactions = currentUser.getWallet().getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("Транзакций нет.");
            return;
        }
        for (Transaction t : transactions) {
            String type = t.isIncome() ? "Доход" : "Расход";
            System.out.printf("%s: %.2f, Категория: %s, Описание: %s\n",
                    type, t.getAmount(), t.getCategory(), t.getDescription());
        }
    }

    private static void addOrEditBudget() {
        System.out.print("Введите название категории: ");
        String category = scanner.nextLine();
        System.out.print("Введите лимит бюджета: ");
        try {
            double limit = Double.parseDouble(scanner.nextLine());
            if (limit < 0) {
                System.out.println("Лимит не может быть отрицательным.");
                return;
            }
            Wallet wallet = currentUser.getWallet();
            BudgetCategory budgetCategory = wallet.getBudgetCategory(category);
            if (budgetCategory != null) {
                budgetCategory.setBudgetLimit(limit);
                System.out.println("Лимит бюджета обновлен.");
            } else {
                wallet.addBudgetCategory(new BudgetCategory(category, limit));
                System.out.println("Категория и лимит бюджета добавлены.");
            }
            userService.saveData();
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат лимита.");
        }
    }

    private static void showBudgetStats() {
        Wallet wallet = currentUser.getWallet();
        Map<String, BudgetCategory> budgets = wallet.getBudgets();
        if (budgets.isEmpty()) {
            System.out.println("Бюджеты не заданы.");
            return;
        }

        boolean hasAlerts = false;
        for (String category : budgets.keySet()) {
            BudgetCategory budget = budgets.get(category);
            double spent = wallet.getTransactions().stream()
                    .filter(t -> !t.isIncome() && t.getCategory().equals(category))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            double remaining = budget.getBudgetLimit() - spent;
            System.out.printf("Категория: %s, Лимит: %.2f, Потрачено: %.2f, Остаток: %.2f\n",
                    category, budget.getBudgetLimit(), spent, (budget.getBudgetLimit() - spent));
            if (remaining < 0) {
                System.out.println("Превышен лимит бюджета в категории '" + category + "' на " + String.format("%.2f", Math.abs(remaining)));
                hasAlerts = true;
            }
        }
        if (!hasAlerts) {
            System.out.println("Все бюджеты в норме");
        }
    }

    private static void checkFinancialHealth() {
        Wallet wallet = currentUser.getWallet();

        double totalIncome = wallet.getTransactions().stream()
                .filter(Transaction::isIncome)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = wallet.getTransactions().stream()
                .filter(t -> !t.isIncome())
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpenses;

        System.out.print("\n--- Финансовое состояние ---\n");
        System.out.printf("Общий доход: %.2f\n", totalIncome);
        System.out.printf("Общие расходы: %.2f\n", totalExpenses);
        System.out.printf("Баланс: %.2f\n", balance);

        if (balance < 0) {
            System.out.println("Расходы превышают доходы!");
        } else {
            System.out.println("Финансовое состояние стабильное");
        }
    }


}
