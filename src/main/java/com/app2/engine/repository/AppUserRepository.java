package com.app2.engine.repository;


import com.app2.engine.entity.app.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Long> {

    @Query("select o from AppUser o where o.username=:user and o.userType='I' ")
    AppUser findByUsernameInternal(@Param("user")String user);
}
