package ru.practicum.ewm.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.location.model.Location;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByLatAndLon(Double lat, Double lon);

    @Query("Select l from Location l where distance(:lat, :lon, l.lat, l.lon) <= :radius")
    List<Location> findAllByCoordinatesAndRadius(@Param("lat") double lat, @Param("lon") double lon, @Param("radius") double radius);
}
