package com.luv2code.jobportal.service;


import com.luv2code.jobportal.entity.RecruiterProfile;
import com.luv2code.jobportal.entity.Users;
import com.luv2code.jobportal.repository.RecruitorProfileRepository;
import com.luv2code.jobportal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterProfileService {

    private final RecruitorProfileRepository recruitorProfileRepository;

    private final UsersRepository usersRepository;


    @Autowired
    public RecruiterProfileService(RecruitorProfileRepository recruitorProfileRepository, UsersRepository usersRepository) {
        this.recruitorProfileRepository = recruitorProfileRepository;
        this.usersRepository = usersRepository;
    }

    public Optional<RecruiterProfile> getOne(Integer id){
        return recruitorProfileRepository.findById(id);
    }

    public RecruiterProfile addNew(RecruiterProfile recruiterProfile) {

        return recruitorProfileRepository.save(recruiterProfile);

    }

    public RecruiterProfile getCurrentRecruiterProfile() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AbstractAuthenticationToken)){
            String username=authentication.getName();
             Users users= usersRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
             Optional<RecruiterProfile> recruiterProfile=getOne(users.getUserId());
             return recruiterProfile.orElse(null);


        }else return null;
    }
}


















