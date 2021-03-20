package cab_hailing.ride_service.repository;

import org.springframework.data.jpa.repository.JpaRepository; 
import cab_hailing.ride_service.model.Ride;

public interface RideRepository extends JpaRepository<Ride, Long>{
	
}
