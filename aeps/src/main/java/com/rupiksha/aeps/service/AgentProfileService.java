package com.rupiksha.aeps.service;

import com.rupiksha.aeps.entity.AgentProfile;
import com.rupiksha.aeps.repository.AgentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentProfileService {

    private final AgentProfileRepository repository;


    // save profile
    public AgentProfile save(AgentProfile profile){

        return repository.save(profile);

    }


    // get profile
    public AgentProfile getByMobile(String mobile){

        return repository.findByMobile(mobile)
                .orElse(null);

    }

}