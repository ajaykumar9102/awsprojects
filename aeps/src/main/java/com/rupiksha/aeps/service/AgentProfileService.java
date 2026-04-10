package com.rupiksha.aeps.service;

import com.rupiksha.aeps.entity.AgentProfile;
import com.rupiksha.aeps.repository.AgentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgentProfileService {

    private final AgentProfileRepository repository;


    // Save or Update profile
    public AgentProfile save(AgentProfile profile){

        return repository.findByMobile(profile.getMobile())
                .map(existing -> {

                    existing.setAgentId(profile.getAgentId());
                    existing.setMerchantId(profile.getMerchantId());
                    existing.setName(profile.getName());
                    existing.setShopName(profile.getShopName());
                    existing.setAddress(profile.getAddress());
                    existing.setCity(profile.getCity());
                    existing.setState(profile.getState());
                    existing.setPinCode(profile.getPinCode());
                    existing.setLatitude(profile.getLatitude());
                    existing.setLongitude(profile.getLongitude());

                    return repository.save(existing);

                })
                .orElseGet(() -> repository.save(profile));

    }


    // Get profile
    public AgentProfile getByMobile(String mobile){

        return repository.findByMobile(mobile)
                .orElse(null);

    }

}