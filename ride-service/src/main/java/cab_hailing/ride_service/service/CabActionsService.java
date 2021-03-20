package cab_hailing.ride_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cab_hailing.ride_service.Logger;
import cab_hailing.ride_service.model.CabStatus;
import cab_hailing.ride_service.model.Ride;
import cab_hailing.ride_service.repository.CabStatusRepository;
import cab_hailing.ride_service.repository.RideRepository;
import cab_hailing.ride_service.rest_consumers.CabServiceRestConsumer;
import cab_hailing.ride_service.values.CabMajorStates;
import cab_hailing.ride_service.values.CabMinorStates;
import cab_hailing.ride_service.values.RideStates;

@Component
public class CabActionsService {

	// ---------------------------------------------------------------------------------------------

	@Autowired
	RideRepository rideRepo;

	@Autowired
	CabStatusRepository cabStatusRepo;

	@Autowired
	CabServiceRestConsumer cabServiceRestConsumer;

	// ---------------------------------------------------------------------------------------------

	/*
	 * Cab cabId invokes this to sign-in and notify the company that it wants to
	 * start its working day at location “initialPos”. Response is true from the
	 * company iff the cabId is a valid one and the cab is not already signed in.
	 */
	@Transactional
	public boolean cabSignsIn(long cabID, long initialPos) {

		// Check if cab id is valid
		// If cab status record not found, then insert the record houldn't happen
		// ideally, but if so, then this is a quick fix
		CabStatus cabStatus = cabStatusRepo.findById(cabID).orElse(null);
		if (cabStatus == null) {
			cabStatus = new CabStatus(cabID, initialPos);
			cabStatus = cabStatusRepo.save(cabStatus);
		}

		// Check if cab is in AVAILABLE state
		String cabMajorState = cabStatus.getMajorState();

		if (cabMajorState != null && cabMajorState.equals(CabMajorStates.SIGNED_OUT)) {
			cabStatus.setMajorState(CabMajorStates.SIGNED_IN);
			cabStatus.setMinorState(CabMinorStates.AVAILABLE);
			cabStatus.setCurrPos(Long.valueOf(initialPos));
			cabStatusRepo.save(cabStatus);
			return true;
		}

		return false;
	}

	// ---------------------------------------------------------------------------------------------

	/*
	 * Cab uses this to sign out for the day. Response is true (i.e., the sign-out
	 * is accepted) iff cabId is valid and the cab is in available state.
	 */
	@Transactional
	public boolean cabSignsOut(long cabID) {

		// Check if cab id is valid
		CabStatus cabStatus = cabStatusRepo.findById(cabID).orElse(null);

		// Check if cab is in AVAILABLE state
		if (cabStatus != null) {
			String cabMajorState = cabStatus.getMajorState();
			String cabMinorState = cabStatus.getMinorState();

			if (cabMajorState != null && cabMinorState != null && cabMajorState.equals(CabMajorStates.SIGNED_IN)
					&& cabMinorState.equals(CabMinorStates.AVAILABLE)) {
				cabStatus.setMajorState(CabMajorStates.SIGNED_OUT);
				cabStatus.setMinorState(CabMinorStates.NONE);
				cabStatus.setCurrPos(Long.valueOf(-1));
				cabStatusRepo.save(cabStatus);
				return true;
			}

		}

		return false;
	}

	// ---------------------------------------------------------------------------------------------

	/*
	 * This end-point is mainly to enable testing. Returns a tuple of strings
	 * indicating the current state of the cab
	 * (signed-out/available/committed/giving-ride), its last known position, and if
	 * currently in a ride then the custId and destination location. The elements of
	 * the tuple should be separated by single spaces, and the tuple should not have
	 * any beginning and ending demarcators. Last known position is the source
	 * position of the current ride if the cab is in giving-ride state, is the
	 * sign-in location if it has signed in but not given any ride yet, is the
	 * destination of the last ride if it is in available state and gave a ride
	 * after sign-in, and is -1 if it is in signed-out state.
	 * 
	 * Final String : <SIGNED_OUT/AVAILABLE/COMMITTED/GIVING_RIDE> <LAST_KNOWN_POS>
	 * [<CUST_ID> <DEST_LOC>]
	 */

	@Transactional
	public String getCabStatus(long cabID) {
		Logger.log("Received getCabStatus request for cab id : "+cabID);
		// Check if cab id is valid
		CabStatus cabStatus = cabStatusRepo.findById(cabID).orElse(null);

		String finalResponse = "";
		String cabStateStr = "";
		String lastKnownPosStr = "";
		String custIDStr = "";
		String destLocStr = "";

		// Check if cab is in AVAILABLE state
		if (cabStatus != null) {
			String cabMajorState = cabStatus.getMajorState();
			String cabMinorState = cabStatus.getMinorState();

			if (cabMajorState != null) {
				if (cabMajorState.equals(CabMajorStates.SIGNED_OUT)) {
					cabStateStr = "SIGNED_OUT";
				} else if (cabMajorState.equals(CabMajorStates.SIGNED_IN)) {
					if (cabMinorState.equals(CabMinorStates.AVAILABLE)) {
						cabStateStr = "AVAILABLE";
					} else if (cabMinorState.equals(CabMinorStates.COMMITTED)) {
						cabStateStr = "COMMITTED";
					} else if (cabMinorState.equals(CabMinorStates.GIVING_RIDE)) {
						cabStateStr = "GIVING_RIDE";

					} else {
						cabStateStr = "Error";
						Logger.log("Invalid minor state of cab id : " + cabID);
					}
				}
			}

			lastKnownPosStr = cabStatus.getCurrPos().toString();
			
			finalResponse = cabStateStr +" "+lastKnownPosStr;
			
			if (cabMinorState.equals(CabMinorStates.GIVING_RIDE)) {
				Ride ride = rideRepo.findTopByCabStatusAndRideState(cabStatus, RideStates.ONGOING);

				if (ride == null) {
					Logger.log("Missing row in rides table for a cab in giving ride state for cab id : " + cabID);
					return finalResponse+" error";
				}
				
				custIDStr = ride.getCustID().toString();
				destLocStr = ride.getDestPos().toString();
				
				finalResponse+=" "+custIDStr+" "+destLocStr;
				
				return finalResponse;
			}
			return finalResponse;
		}
		
		Logger.log("Couldn't find cab id : "+cabID + " for getCabStatus");
		return "-1";
	}

	// ---------------------------------------------------------------------------------------------
	/*
	 * This end-point will be mainly useful during testing. Send Cab.rideEnded
	 * requests to all cabs that are currently in giving-ride state, then send
	 * Cab.signOut requests to all cabs that are currently in sign-in state.
	 */
	// Not transactional
	public void reset() {
		List<CabStatus> cabStausList = new ArrayList<>();
		cabStausList = cabStatusRepo.findAllByMinorState(CabMinorStates.GIVING_RIDE);

		for (CabStatus cabStatus : cabStausList) {
			long cabID = cabStatus.getCabID();
			Ride ride = rideRepo.findTopByCabStatusAndRideState(cabStatus, RideStates.ONGOING);
			
			if(ride != null) {
				long rideID = ride.getRideID();
				
				boolean ifRideEnded = cabServiceRestConsumer.consumeRideEnded(cabID, rideID);
				if (ifRideEnded) {
					Logger.log("Ride ended successfully with CabID: " + cabID + "and rideID: " + rideID);
				} else {
					Logger.log("Could not end ride with CabID: " + cabID + "and rideID: " + rideID);
				}
			}
		}

		cabStausList = cabStatusRepo.findAllByMajorState(CabMajorStates.SIGNED_IN);

		for (CabStatus cabStatus : cabStausList) {
			long cabID = cabStatus.getCabID();

			boolean ifSignedOut = cabServiceRestConsumer.consumeSignOut(cabID);
			if (ifSignedOut) {
				Logger.log("Cab signed out successfully with CabID: " + cabID);
			} else {
				Logger.log("Could not sign out cab with CabID: " + cabID);
			}
		}
	}

}
