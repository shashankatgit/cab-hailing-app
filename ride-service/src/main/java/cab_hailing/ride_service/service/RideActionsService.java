package cab_hailing.ride_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import cab_hailing.ride_service.model.CabStatus;
import cab_hailing.ride_service.model.Ride;
import cab_hailing.ride_service.repository.CabStatusRepository;
import cab_hailing.ride_service.repository.RideRepository;
import cab_hailing.ride_service.values.CabMajorStates;
import cab_hailing.ride_service.values.CabMinorStates;
import cab_hailing.ride_service.values.RideStates;
import org.springframework.data.domain.Sort;

@Component
public class RideActionsService {

	// ---------------------------------------------------------------------------------------------

	@Autowired
	RideRepository rideRepo;

	@Autowired
	CabStatusRepository cabStatusRepo;

	// ---------------------------------------------------------------------------------------------
	/*
	 * Cab uses this request, to signal that rideId has ended (at the chosen
	 * destination). Return true iff rideId corresponds to an ongoing ride.
	 */
	@Transactional
	public boolean rideEnded(long rideId) {

		// Check if ride id is valid
		Ride ride = rideRepo.findById(rideId).orElse(null);
		if (ride != null) {
			String rideStatus = ride.getRideState();

			if (rideStatus != null && rideStatus.equals(RideStates.ONGOING)) {
				ride.setRideState(RideStates.COMPLETED);
				// TODO: Update currPos, add destination loc to ride table
				rideRepo.save(ride);
				return true;
			}
		}

		return false;
	}

	// ---------------------------------------------------------------------------------------------
	/*
	 * Customer uses this to request a ride from the service. The cab service should
	 * first generate a globally unique rideId corresponding to the received
	 * request. It should then try to find a cab (using Cab.requestRide) that is
	 * willing to accept this ride. It should request cabs that are currently in
	 * available state one by one in increasing order of current distance of the cab
	 * from sourceLoc. The first time a cab accepts the request, the service should
	 * calculate the fare (the formula for this is described later) and attempt to
	 * deduct the fare from custId’s wallet. If the deduction was a success, send
	 * request Cab.rideStarted to the accepting cabId and then respond to the
	 * customer with the generated rideId, else send request Cab.rideCanceled to the
	 * accepting cabId and then respond with -1 to the customer. If three cabs have
	 * been requested and all of them reject, then respond with -1 to the customer.
	 * If fewer than three cabs have been contacted and they all reject the requests
	 * and there are no more cabs available to request that are currently signed-in
	 * and not currently giving a ride, respond with -1 to the customer. The fare
	 * for a ride is equal to the distance from the accepting cab’s current location
	 * to sourceLoc plus the distance from sourceLoc to destinationLoc, times 10
	 * (i.e., Rs. 10 per unit distance).
	 * 
	 */
	public int requestRide(long custId, long sourceLoc, long destinationLoc) {
		// TODO: Update currPos = sourceLoc
		
		//Generate a globally unique rideId 
		Ride ride = rideRepo.save(new Ride());
		long rideID = ride.getRideID();

		List<CabStatus> cabList = cabStatusRepo.findAll();
		for (CabStatus cab : cabList) {
			Long currPos = cab.getCurrPos().longValue();
			cab.setCurrPos(Math.abs(sourceLoc - currPos));			
		}
		
//		cabList = cabList.sort(cab);
		
		CabStatus cabAcceptsRide = new CabStatus();

		int tries = 0;
		for (CabStatus cab : cabList) {
			System.out.println(cab.getCurrPos());
			
			if(cab.getMinorState() == CabMinorStates.AVAILABLE) {
				// TODO: Query cabs and set cabAcceptsRide
				++tries;
			}
			
			
			if (tries == 3)
				break;
		}

		if (true) {
			long currPos = cabAcceptsRide.getCurrPos();
			long fare = (Math.abs(sourceLoc - currPos) + Math.abs(sourceLoc - destinationLoc)) * 10;

			// TODO: Try to deduct from wallet

			if (true) {
				// TODO: If the deduction was a success, send request Cab.rideStarted to the
				// accepting cabId
				
				return (int) rideID;
			}
		}
			
		return -1;
		
	}

}
