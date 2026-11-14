package kg.petsy.petsy.repository;

import kg.petsy.petsy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    /**
     * SPATIAL-ЗАПРОС НА POSTGIS (ST_DWithin)
     * Ищет пользователей определенной роли в заданном радиусе (в метрах) от центральной точки.
     *
     * @param point центр поиска (тип Point из JTS)
     * @param role  роль (например, ROLE_VOLUNTEER)
     * @param distanceMeters радиус в метрах (например, 5000 = 5 км)
     */
    @Query(value = "SELECT * FROM users u WHERE u.role = :role AND u.location IS NOT NULL AND ST_DWithin(u.location, :point, :distanceMeters)", nativeQuery = true)
    List<User> findUsersByRoleAndLocationWithinRadius(
            @Param("role") String role,
            @Param("point") org.locationtech.jts.geom.Point point,
            @Param("distanceMeters") double distanceMeters
    );

}