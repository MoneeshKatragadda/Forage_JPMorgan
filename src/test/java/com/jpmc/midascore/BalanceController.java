package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/balance")
public class BalanceController {

    private UserRepository userRepository;

    public BalanceController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @GetMapping
    public Balance getBalance(@RequestParam long userId){
        UserRecord user = userRepository.findById(userId);
        if(user==null) return new Balance(0);
        return new Balance(user.getBalance());
    }
}
