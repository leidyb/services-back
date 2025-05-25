package com.bernate.services_back.service;

import com.bernate.services_back.model.User;
import com.bernate.services_back.repository.UserRepository;
import com.bernate.services_back.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Intentando cargar usuario por username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con nombre de usuario: {}", username);
                    return new UsernameNotFoundException("Usuario no encontrado con nombre de usuario: " + username);
                });





        CustomUserDetails userDetails = CustomUserDetails.build(user);



        return userDetails;
    }
}