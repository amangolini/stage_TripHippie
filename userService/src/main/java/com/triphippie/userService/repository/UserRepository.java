package com.triphippie.userService.repository;

import com.triphippie.userService.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public List<User> findByUsernameContaining(String username);

    public Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM trip_user OFFSET ?1 ROWS FETCH NEXT ?2 ROWS ONLY", nativeQuery = true)
    public List<User> findUsersWithOffset(Integer offset, Integer next);

    @Query(value = "SELECT * FROM trip_user WHERE username LIKE ?3% OFFSET ?1 ROWS FETCH NEXT ?2 ROWS ONLY", nativeQuery = true)
    public List<User> findUsersByUsernameWithOffset(Integer offset, Integer next, String username);

    @Query("UPDATE User u SET u.profileImageUrl = :imageUrl WHERE u.id = :id")
    @Modifying
    @Transactional
    public void saveUserProfileImageUrl(@Param("id") Integer id, @Param("imageUrl") String imgUrl);

    @Query("UPDATE User u SET u.profileImageUrl = null WHERE u.id = :id")
    @Modifying
    @Transactional
    public void deleteUserProfileImageUrl(@Param("id") Integer id);

    @Query("SELECT u.profileImageUrl FROM User u WHERE u.id = :id")
    public Optional<String> findUserProfileImageUrlById(Integer id);
}
