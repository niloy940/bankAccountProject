/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank.account.project;

import account.BankAccount;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author niloy
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField balanceField;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<BankAccount> accountListView;
    @FXML
    private TextField numberField;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea addressArea;
    @FXML
    private ComboBox<BankAccount> accountComboBox;
    @FXML
    private TextField searchFieldTransaction;
    @FXML
    private ComboBox<TransactionType> transactionTypeComboBox;
    @FXML
    private DatePicker transactionDatePicker;
    @FXML
    private TextField transactionTimeField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<BankAccount> accountComboBoxHistory;
    @FXML
    private TextField searchFieldHistory;
    @FXML
    private TableView<Transaction> tableView;
    @FXML
    private TableColumn<Transaction, Integer> accountNumberColumn;
    @FXML
    private TableColumn<Transaction, TransactionType> transactionTypeColumn;
    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;
    @FXML
    private TableColumn<Transaction, LocalTime> timeColumn;
    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    private ObservableList<BankAccount> bankAccountList;
    private ObservableList<BankAccount> filteredBankAccountList;
    private ObservableList<Transaction> transactionList;
    private ObservableList<Transaction> transactionList1;
    private ObservableList<TransactionType> transactionTypeList;
    private BankAccount selectedAccount;
    private Transaction transaction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bankAccountList = FXCollections.observableArrayList();
        filteredBankAccountList = FXCollections.observableArrayList();
        transactionTypeList = FXCollections.observableArrayList();
        transactionList = FXCollections.observableArrayList();
        transactionList1 = FXCollections.observableArrayList();

        accountListView.setItems(filteredBankAccountList);
        accountComboBox.setItems(filteredBankAccountList);
        accountComboBoxHistory.setItems(filteredBankAccountList);
        tableView.setItems(transactionList);

        transactionTypeList.addAll(TransactionType.values());

        transactionTypeComboBox.setItems(transactionTypeList);

        LocalDate currentDate = LocalDate.now();
        transactionDatePicker.setValue(currentDate);

        LocalTime currentTime = LocalTime.now();
        transactionTimeField.setText(currentTime.toString());

        try {
            RandomAccessFile output = new RandomAccessFile("accounts.txt", "r");

            int accountNumber = 0;
            String accountName = "";
            String address = "";
            double balance = 0;
            int countData = 0;

            while (true) {
                String line = output.readLine();
                if (line == null) {
                    break;
                }

                String tokens[] = line.split(":");

                switch (tokens[0]) {
                    case "accountNumber":
                        accountNumber = Integer.parseInt(tokens[1]);
                        countData++;
                        break;
                    case "accountName":
                        accountName = tokens[1];
                        countData++;
                        break;
                    case "address":
                        address = tokens[1];
                        countData++;
                        break;
                    case "balance":
                        balance = Double.parseDouble(tokens[1]);
                        countData++;
                        break;
                }

                if (countData % 4 == 0) {
                    BankAccount account = new BankAccount(accountNumber, accountName, address, balance);
                    bankAccountList.add(account);
                }
            }

            filteredBankAccountList.addAll(bankAccountList);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void informationAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            reset();
        }
    }

    private void reset() {
        clearForm();
        accountComboBoxHistory.valueProperty().set(null);
        tableView.getItems().clear();
        numberField.setDisable(true);

        //sorting objects (accountNumber) in ArrayList
        filteredBankAccountList.sort(Comparator.comparing(BankAccount::getAccountNumber));

        if (filteredBankAccountList.isEmpty()) {
            numberField.setText("1");
        } else {

            //getting the last object (accountNumber) in ArrayList 
            BankAccount ac = filteredBankAccountList.get(filteredBankAccountList.size() - 1);

            numberField.setText(ac.getAccountNumber() + 1 + "");
        }
    }

    @FXML
    private void handleResetAction(ActionEvent event) {
        reset();
    }

    @FXML
    private void handleCreateAction(ActionEvent event) {
        int accountNumber = Integer.parseInt(numberField.getText());
        String accountName = nameField.getText();
        String address = addressArea.getText();
        double balance = Double.parseDouble(balanceField.getText());

        BankAccount account = new BankAccount(accountNumber, accountName, address, balance);

        try {
            RandomAccessFile input = new RandomAccessFile("accounts.txt", "rw");
            input.seek(input.length());

            input.writeBytes(account.getAllData() + "\n");

            bankAccountList.add(account);
            filteredBankAccountList.add(account);

            input.close();

            informationAlert("Congratulation!", "Account has been created!");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Sure, want to update the address?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            String updatedAddress = addressArea.getText();
            selectedAccount.setAddress(updatedAddress);

            try {
                RandomAccessFile input = new RandomAccessFile("accounts.txt", "rw");
                input.setLength(0);
                filteredBankAccountList.forEach(action -> {
                    try {
                        input.writeBytes(action.getAllData() + "\n");

                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                });

                input.close();

                reset();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(FXMLDocumentController.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void handleDeleteAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Sure, want to delete this account?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            ////////Deleting accounts////////
            bankAccountList.remove(selectedAccount);
            filteredBankAccountList.remove(selectedAccount);

            try {
                RandomAccessFile input = new RandomAccessFile("accounts.txt", "rw");
                input.setLength(0);
                bankAccountList.forEach(action -> {
                    try {
                        input.writeBytes(action.getAllData() + "\n");

                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                });

                input.close();

                reset();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(FXMLDocumentController.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

            ///Deleting transactions////
            try {
                RandomAccessFile output = new RandomAccessFile("transactions.txt", "rw");

                transactionList1.removeIf(filter -> filter.getAccountNum() == selectedAccount.getAccountNumber());

                output.setLength(0);

                transactionList1.forEach(action -> {
                    try {
                        output.writeBytes(action.getData() + "\n");
                    } catch (IOException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void clearForm() {
        numberField.clear();
        nameField.clear();
        addressArea.clear();
        balanceField.clear();
        searchFieldTransaction.clear();
        amountField.clear();
        accountComboBox.valueProperty().set(null);
        accountComboBoxHistory.valueProperty().set(null);
        transactionTypeComboBox.valueProperty().set(null);
    }

    private void displayData() {
        numberField.setText(selectedAccount.getAccountNumber() + "");
        nameField.setText(selectedAccount.getAccountName());
        addressArea.setText(selectedAccount.getAddress().replaceAll(";", "\n"));
        balanceField.setText(selectedAccount.getBalance() + "");
    }

    private void filter(String name) {
        filteredBankAccountList.clear();
        //name = searchField.getText().toLowerCase();

        bankAccountList.forEach((action) -> {
            BankAccount account = action;
            if (account.getAccountName().toLowerCase().contains(name)) {
                filteredBankAccountList.add(account);
            }
        });
    }

    @FXML
    private void handleFilterAction(ActionEvent event) {
        filter(searchField.getText().toLowerCase());
    }

    @FXML
    private void handleKeyFilterAction(KeyEvent event) {
        filter(searchField.getText().toLowerCase());
    }

    @FXML
    private void handleListClickAction(MouseEvent event) {
        selectedAccount = accountListView.getSelectionModel().getSelectedItem();
        displayData();
        transactionList1.clear();
        try {
            RandomAccessFile output = new RandomAccessFile("transactions.txt", "r");

            while (true) {
                String line = output.readLine();
                if (line == null) {
                    break;
                }
                String tokens[] = line.split(";");

                int accountNum = Integer.parseInt(tokens[0]);
                TransactionType transactionType = TransactionType.valueOf(tokens[1]);
                LocalDate transactionDate = LocalDate.parse(tokens[2]);
                LocalTime transactionTime = LocalTime.parse(tokens[3]);
                double amount = Double.parseDouble(tokens[4]);

                transaction = new Transaction(accountNum, transactionType, transactionDate, transactionTime, amount);

                transactionList1.add(transaction);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        clearForm();
        numberField.setDisable(false);
        numberField.setEditable(true);
        numberField.setText(numberField.getText());
    }

    @FXML
    private void handleTransactionFilterAction(ActionEvent event) {
        filter(searchFieldTransaction.getText().toLowerCase());
    }

    @FXML
    private void handleTransactionKeyFilterAction(KeyEvent event) {
        filter(searchFieldTransaction.getText().toLowerCase());
    }

    @FXML
    private void handleTransactionAccountClickAction(MouseEvent event) {
        filteredBankAccountList.sort(Comparator.comparing(BankAccount::getAccountNumber));
    }

    @FXML
    private void handleSubmitAction(ActionEvent event) {
        try {
            RandomAccessFile input = new RandomAccessFile("transactions.txt", "rw");
            input.seek(input.length());

            BankAccount bankAccount = accountComboBox.getSelectionModel().getSelectedItem();
            TransactionType transactionType = transactionTypeComboBox.getSelectionModel().getSelectedItem();
            LocalDate transactionDate = transactionDatePicker.getValue();
            LocalTime transactionTime = LocalTime.parse(transactionTimeField.getText());
            int amount = Integer.parseInt(amountField.getText());

            Transaction transaction = new Transaction(bankAccount, transactionType, transactionDate, transactionTime, amount);

            input.writeBytes(transaction.getAllData() + "\n");

            double oldBalance = bankAccount.getBalance();

            if (transactionType == TransactionType.DEPOSITE) {
                bankAccount.deposite(amount);
                informationAlert("Transaction", "Deposite Successful!\n\nOld Balance : " + oldBalance + "\nDeposited Amount : " + amount + "\nNew Balance : " + bankAccount.getBalance());
            } else {
                bankAccount.withdraw(amount);
                informationAlert("Transaction", "Withdraw Sucsessful!\n\nOld Balance : " + oldBalance + "\nWithdrawal Amount : " + amount + "\nNew Balance : " + bankAccount.getBalance());
            }

            ///updating the balance field on accounts.txt///
            RandomAccessFile output = new RandomAccessFile("accounts.txt", "rw");
            output.setLength(0);

            bankAccountList.forEach(action -> {
                try {
                    output.writeBytes(action.getAllData() + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleHistoryFilterAction(ActionEvent event) {
        filter(searchFieldHistory.getText().toLowerCase());
    }

    @FXML
    private void handleHistoryKeyFilterAction(KeyEvent event) {
        filter(searchFieldHistory.getText().toLowerCase());
    }

    @FXML
    private void handleHistoryAccountClickAction(MouseEvent event) {
        filteredBankAccountList.sort(Comparator.comparing(BankAccount::getAccountNumber));
    }

    @FXML
    private void handleHistorySubmitAction(ActionEvent event) {
        try {
            RandomAccessFile input = new RandomAccessFile("transactions.txt", "r");

            BankAccount account = accountComboBoxHistory.getSelectionModel().getSelectedItem();

            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }
                String tokens[] = line.split(";");

                int accountNumber = Integer.parseInt(tokens[0]);
                TransactionType transactionType = TransactionType.valueOf(tokens[1]);
                LocalDate transactionDate = LocalDate.parse(tokens[2]);
                LocalTime transactionTime = LocalTime.parse(tokens[3]);
                double amount = Double.parseDouble(tokens[4]);

                while (account.getAccountNumber() == accountNumber) {
                    Transaction transaction = new Transaction(account, transactionType, transactionDate, transactionTime, amount);
                    transactionList.add(transaction);
                    break;
                }

                accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
                transactionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
                timeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionTime"));
                amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        searchFieldHistory.clear();
    }

    @FXML
    private void handleHistoryResetAction(ActionEvent event) {
        reset();
    }
}
