package com.bkleszcz.WordApp.database.userRepository;

import com.bkleszcz.WordApp.domain.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Privilege findByName(String name);

    @Override
    void delete(Privilege privilege);
}
