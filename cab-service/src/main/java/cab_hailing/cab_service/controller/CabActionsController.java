package cab_hailing.cab_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CabActionsController {
	
	@GetMapping("requestRide")
	public boolean requestRide(@RequestParam int cabId, @RequestParam int rideId, 
			@RequestParam int sourceLoc, @RequestParam int destinationLoc) {
		return true;
	}
	
	@GetMapping("rideStarted")
	public boolean rideStarted(@RequestParam int cabId, @RequestParam int rideId) {
		return true;
	}
	
	@GetMapping("rideCanceled")
	public boolean rideCanceled(@RequestParam int cabId, @RequestParam int rideId) {
		return true;
	}

	@GetMapping("rideEnded")
	public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
		return true;
	}

	@GetMapping("signIn")
	public boolean signIn(@RequestParam int cabId, @RequestParam int initialPos) {
		return true;
	}

	@GetMapping("signOut")
	public boolean signOut(@RequestParam int cabId) {
		return true;
	}
	
	@GetMapping("numRides")
	public int numRides(@RequestParam int cabId) {
		return 0;
	}

}
