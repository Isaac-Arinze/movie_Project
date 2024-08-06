    package com.zikan.movieAPI.Auth.repository;

    import com.zikan.movieAPI.Auth.entities.User;
    import jakarta.transaction.Transactional;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;

    import java.util.Optional;

    public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByEmail (String email);

        @Transactional
        @Modifying
        @Query ("update User u set u.password = ?2 where u.email = ?1")
        void updatePassword (String email, String password);

        boolean existsByEmail(String email);

        boolean existsByUsername(String username);
    }
