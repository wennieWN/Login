package com.sample.DAO;

import com.sample.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer>{
//    List<User> findByUsername(String username);
    List<User> findByMail(String mail);
}
