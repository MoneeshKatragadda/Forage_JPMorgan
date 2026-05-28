package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaConsumer {
    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private BalanceQuerier balanceQuerier;

    public KafkaConsumer(UserRepository userRepository, TransactionRepository transactionRepository, BalanceQuerier balanceQuerier){
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.balanceQuerier = balanceQuerier;
    }
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "kafka-group")
    public void listen(String transaction){
        String[] parts = transaction.split(",");

        //Test two
        System.out.println("Transaction received: " + transaction);

        //Test four
        long senderId = Long.parseLong(parts[0].trim());
        long recipientId = Long.parseLong(parts[1].trim());
        float amount = Float.parseFloat(parts[2].trim());

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        if(sender == null || recipient == null) return;
        if(sender.getBalance() < amount) return;

        Transaction t = new Transaction(senderId, recipientId, amount);
        Balance incentive = balanceQuerier.query(t);

        float incentiveAmount = incentive.getAmount();

        //update the balances of sender and receiver if valid transaction
        sender.setBalance(sender.getBalance()-amount);
        recipient.setBalance(recipient.getBalance()+amount+incentiveAmount);

        //save the updated amounts
        userRepository.save(sender);
        userRepository.save(recipient);

        //save the record
        TransactionRecord record = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(record);

        //Printing the values
        if (sender.getName().equals("wilbur")) {
            System.out.println("wilbur Balance: " + sender.getBalance());
        }
        if (recipient.getName().equals("wilbur")) {
            System.out.println("wilbur Balance: " + recipient.getBalance());
        }

    }
}
