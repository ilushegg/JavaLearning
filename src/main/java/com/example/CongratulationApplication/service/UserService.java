package com.example.CongratulationApplication.service;

import com.example.CongratulationApplication.domain.Person;
import com.example.CongratulationApplication.domain.User;
import com.example.CongratulationApplication.repos.PersonRepo;
import com.example.CongratulationApplication.repos.UserRepo;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final PersonRepo personRepo;

    public Map<User, Map<Boolean, String>> map = new HashMap<>();

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, @Lazy MailService mailService, PersonRepo personRepo){
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.personRepo = personRepo;
        birthdayPersons();
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User not found!");
        }
        return user;
    }

    public boolean add(User user){
        User userFromDb = userRepo.findByEmail(user.getEmail());

        if(userFromDb != null ){
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepo.save(user);
        String link = "http://localhost:8080/activate/" + user.getActivationCode();
        sendConfirm(user, link);
        return true;
    }

    public void sendConfirm(User user, String link){
        String message = String.format(
                "Hello, %s!" +
                        "Welcome to the application where you will never forget about your friends' birthday!" +
                        "Please follow the following link to confirm your email address: %s",
                user.getUsername(), link
        );
        mailService.send(user.getEmail(), "Welcome to the app", message);
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);
        if(user == null){
            return  false;
        }
        user.setActivationCode(null);
        userRepo.save(user);
        return true;
    }

    public void birthdayPersons(){
        List<User> users = userRepo.findAll();
        for(User user : users){
            List<Person> persons = personRepo.findAllByUser((User)user);
            String birthdayPersons = "";
            for(Person person : persons){
                if(person.getBirthday().getDayOfMonth() == LocalDate.now().getDayOfMonth() && person.getBirthday().getMonth() == LocalDate.now().getMonth()) {
                    birthdayPersons += person.getName() + "\n";
                }
            }
            Map<Boolean, String> temp = new HashMap<>();
            temp.put(true, birthdayPersons);
            map.put(user, temp);
        }
    }
}
