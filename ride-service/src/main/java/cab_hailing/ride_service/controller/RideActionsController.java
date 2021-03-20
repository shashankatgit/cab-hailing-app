package cab_hailing.ride_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cab_hailing.ride_service.service.CabActionsService;
import cab_hailing.ride_service.service.RideActionsService;

@RestController
public class RideActionsController {

	@Autowired
	CabActionsService cabActionsService;

	@Autowired
	RideActionsService rideActionsService;

	@GetMapping("rideEnded")
	public boolean rideEnded(@RequestParam int rideId) {
		return rideActionsService.rideEnded(rideId);
	}

	@GetMapping("cabSignsIn")
	public boolean cabSignsIn(@RequestParam int cabId, @RequestParam int initialPos) {
		return cabActionsService.cabSignsIn(cabId, initialPos);
	}

	@GetMapping("cabSignsOut")
	public boolean cabSignsOut(@RequestParam int cabId) {
		return cabActionsService.cabSignsOut(cabId);
	}

	@GetMapping("requestRide")
	public long requestRide(@RequestParam int custId, @RequestParam int sourceLoc, @RequestParam int destinationLoc) {
		return rideActionsService.requestRide(custId, sourceLoc, destinationLoc);
	}

	@GetMapping("getCabStatus")
	public String getCabStatus(@RequestParam int cabId) {
		return cabActionsService.getCabStatus(cabId);
	}

	@GetMapping("reset")
	public void reset() {
		cabActionsService.reset();
	}

}