package cab_hailing.ride_service.repository;

import org.springframework.data.jpa.repository.JpaRepository; 
import cab_hailing.ride_service.model.CabStatus;

public interface CabStatusRepository extends JpaRepository<CabStatus, Long>{

}
