/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

/**
 *
 * @author niloy
 */
public class BankAccount {
    private int accountNumber;
    private String accountName;
    private String address;
    protected double balance;
    
    public BankAccount(int accountNumber, String accountName, String address, double balance) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.address = address;
        this.balance = balance;
    }
    
    public int getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAddress() {
        return address;
    }

    public double getBalance() {
        return balance;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String toString(){
        return accountNumber + " : " + accountName;
    }
    
    public String getAllData(){
        return "accountNumber:" + accountNumber + "\n"
                +"accountName:" + accountName + "\n"
                +"address:" + address.replaceAll("\n", ";") + "\n"
                +"balance:" + balance;
    }
    
    public void deposite(int amount){
        if (amount>0) {
            balance += amount;
        } else {
            System.err.println("Depositing negetive amount is not acceptable!");
        }
    }
    
    public void withdraw(int amount){
        if (amount > 0 && amount <= balance + 500) {
            balance -= amount;
        } else {
            System.err.println("You don't have sufficient balance!");
        }
    }
}
