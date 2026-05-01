package com.expense.model;

import java.time.LocalDate;

public class Expense {
    private int id;
    private int userId;
    private String title;
    private double amount;
    private String category;
    private LocalDate date;
    private String description;

    public Expense() {
    }

    public Expense(int userId, String title, double amount, String category, LocalDate date) {
        this.userId = userId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public Expense(int userId, String title, double amount, String category, LocalDate date, String description) {
        this.userId = userId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    public Expense(int id, int userId, String title, double amount, String category, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public Expense(int id, int userId, String title, double amount, String category, LocalDate date, String description) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
