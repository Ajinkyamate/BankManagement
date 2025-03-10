import java.util.*;

// Interface for Bank Cards
interface BankCard {
    void deposit(double amount);

    void withdraw(double amount) throws InsufficientBalanceException;
}

abstract class BankCardImpl implements BankCard {
    protected String cardNumber;
    protected double balance;

    public BankCardImpl(String cardNumber, double balance) {
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public synchronized double getBalance() {
        return balance;
    }
}

// Credit Card Class
class CreditCard extends BankCardImpl {
    private double creditLimit;

    public CreditCard(String cardNumber, double balance, double creditLimit) {
        super(cardNumber, balance);
        this.creditLimit = creditLimit;
    }

    @Override
    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited to Credit Card. New balance: " + balance);
    }

    @Override
    public synchronized void withdraw(double amount) throws InsufficientBalanceException {
        if (balance + creditLimit >= amount) {
            balance -= amount;
            System.out.println("Withdrawn from Credit Card. Remaining balance: " + balance);
        } else {
            throw new InsufficientBalanceException("Credit limit exceeded!");
        }
    }
}

// Debit Card Class
class DebitCard extends BankCardImpl {
    public DebitCard(String cardNumber, double balance) {
        super(cardNumber, balance);
    }

    @Override
    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited to Debit Card. New balance: " + balance);
    }

    @Override
    public synchronized void withdraw(double amount) throws InsufficientBalanceException {
        if (balance >= amount) {
            balance -= amount;
            System.out.println("Withdrawn from Debit Card. Remaining balance: " + balance);
        } else {
            throw new InsufficientBalanceException("Insufficient balance!");
        }
    }
}

class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}

// Multi-threading
class Transaction extends Thread {
    private BankCard card;
    private double amount;

    public Transaction(BankCard card, double amount) {
        this.card = card;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            card.withdraw(amount);
        } catch (InsufficientBalanceException e) {
            System.out.println(e.getMessage());
        }
    }
}

// Main class
public class BankManagement {
    public static void main(String[] args) {
        // Creating bank cards
        List<BankCardImpl> cardList = new ArrayList<>();
        cardList.add(new CreditCard("123456", 5000.0, 2000.0));
        cardList.add(new DebitCard("9876543", 3000.0));

        // Displaying card details
        cardList.forEach(card -> {
            System.out.println("Card Number: " + card.getCardNumber() + " | Balance: " + card.getBalance());
        });

        BankCard card1 = cardList.get(0);
        Transaction t1 = new Transaction(card1, 100);
        Transaction t2 = new Transaction(card1, 200);
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }

        // Summing up balances using streams
        double totalBalance = cardList.stream()
                .mapToDouble(BankCardImpl::getBalance)
                .sum();
        System.out.println("Total balance across all cards: " + totalBalance);
    }
}