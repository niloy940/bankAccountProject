/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.account.project;

import account.BankAccount;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author niloy
 */
public class Transaction {

    private BankAccount account;
    private TransactionType transactionType;
    private LocalDate transactionDate;
    private LocalTime transactionTime;
    private double amount;
    private int accountNum;

    public Transaction(BankAccount account, TransactionType transactionType, LocalDate transactionDate, LocalTime transactionTime, double amount) {
        this.account = account;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.amount = amount;
    }

    public Transaction(int accountNum, TransactionType transactionType, LocalDate transactionDate, LocalTime transactionTime, double amount) {
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.amount = amount;
        this.accountNum = accountNum;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public LocalTime getTransactionTime() {
        return transactionTime;
    }

    public double getAmount() {
        return amount;
    }

    public int getAccountNum() {
        return accountNum;
    }
    
    public int getAccountNumber(){
        return account.getAccountNumber();
    }

    public String getAllData() {
        return account.getAccountNumber() + ";"
                + transactionType.toString() + ";"
                + transactionDate.toString() + ";"
                + transactionTime.toString() + ";"
                + amount;
    }

//    @Override
//    public String toString() {
//        return account.getAccountNumber() + "  |  "
//                + transactionType.toString() + "  |  "
//                + transactionDate.toString() + "  |  "
//                + transactionTime.toString() + "  |  "
//                + amount;
//    }
    
    public String getData(){
        return    accountNum + ";"
                + transactionType.toString() + ";"
                + transactionDate.toString() + ";"
                + transactionTime.toString() + ";"
                + amount;
    }
}
