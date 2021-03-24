package cab_hailing.cab_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cab_hailing.cab_service.Logger;
import cab_hailing.cab_service.service.CabActionsService;
import cab_hailing.cab_service.service.RideActionsService;


@RestController
public class CabActionsController {
	
	@Autowired 
	CabActionsService cabActionsService;
	
	@Autowired 
	RideActionsService rideActionsService;
	
	@GetMapping("requestRide")
	public boolean requestRide(@RequestParam int cabId, @RequestParam int rideId, 
			@RequestParam int sourceLoc, @RequestParam int destinationLoc) {
		return rideActionsService.requestRide(cabId, rideId, sourceLoc, destinationLoc);
	}
	
	@GetMapping("rideStarted")
	public boolean rideStarted(@RequestParam int cabId, @RequestParam int rideId) {
		return rideActionsService.rideStarted(cabId, rideId);
	}
	
	@GetMapping("rideCanceled")
	public boolean rideCancelled(@RequestParam int cabId, @RequestParam int rideId) {
		return rideActionsService.rideCancelled(cabId, rideId);
	}
	
	// Will be consumed by cab driver
	@GetMapping("rideEnded")
	public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {
		return rideActionsService.rideEnded(cabId, rideId);
	}
	
	// Will be consumed by cab driver
	@GetMapping("signIn")
	public boolean signIn(@RequestParam int cabId, @RequestParam int initialPos) {
		return cabActionsService.signIn(cabId, initialPos);
	}

	@GetMapping("signOut")
	public boolean signOut(@RequestParam int cabId) {
		return cabActionsService.signOut(cabId);
	}
	
	@GetMapping("numRides")
	public long numRides(@RequestParam int cabId) {
		return cabActionsService.numRides(cabId);
	}
	
	@GetMapping("printLogReset")
	public int printLogReset() {
		Logger.logReset("Receiving resetting requests from Ride Servie");
		return 1;
	}

}