package cab_hailing.ride_service.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import cab_hailing.ride_service.Logger;
import cab_hailing.ride_service.model.CabStatus;
import cab_hailing.ride_service.model.Ride;
import cab_hailing.ride_service.repository.CabStatusRepository;
import cab_hailing.ride_service.repository.RideRepository;
import cab_hailing.ride_service.rest_consumers.CabServiceRestConsumer;
import cab_hailing.ride_service.rest_consumers.WalletServiceRestConsumer;
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

	@Autowired
	CabServiceRestConsumer cabServiceRestConsumer;

	@Autowired
	WalletServiceRestConsumer walletServiceRestConsumer;

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
				CabStatus cabStatus = ride.getCabStatus();
				cabStatus.setMinorState(CabMinorStates.AVAILABLE);

				cabStatus.setCurrPos(ride.getDestPos());

				rideRepo.save(ride);
				cabStatusRepo.save(cabStatus);
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
	@Transactional
	public long requestRide(long custID, long sourceLoc, long destinationLoc) {
		// TODO: Update currPos = sourceLoc

		// Generate a globally unique rideId
		Ride ride = new Ride();
		ride.setSrcPos(sourceLoc);
		ride.setDestPos(destinationLoc);
		ride.setCustID(custID);
		ride = rideRepo.save(ride);

		long rideID = ride.getRideID();

		List<CabStatus> candidateCabs = new ArrayList<CabStatus>();
		candidateCabs.addAll(cabStatusRepo
				.findTop3ByCurrPosGreaterThanEqualAndMajorStateAndMinorStateOrderByCurrPosAsc(sourceLoc, "I", "A"));
		candidateCabs.addAll(cabStatusRepo
				.findTop3ByCurrPosLessThanAndMajorStateAndMinorStateOrderByCurrPosDesc(sourceLoc, "I", "A"));

		CabStatus selectedCab = null;

		candidateCabs.sort(new CabStatusComparatorByCurPos(sourceLoc));

		// Now we have sorted cab list
		int nTries = 0;
		for (CabStatus candidateCab : candidateCabs) {
			System.out.println(candidateCab);

			boolean ifAccept = cabServiceRestConsumer.consumeRequestRide(candidateCab.getCabID(), rideID, sourceLoc,
					destinationLoc);
			if (ifAccept) {
				System.out.println("LOG : Cab ID : " + candidateCab.getCabID() + " accepted the ride request");
				selectedCab = candidateCab;
				break;
			}
			nTries++;
			if (nTries >= 3) {
				System.out.println("LOG : Couldn't find any ride after 3 attempts");
				return -1;
			}
		}

		// if a cab accepted the ride
		if (selectedCab != null) {
			long fare = calcFare(sourceLoc, selectedCab.getCurrPos(), destinationLoc);
			Logger.log("Calculated fare for ride id : " + rideID + " is : " + fare);

			// attempt wallet deduction for the consumer
			boolean ifDeductionSuccess = walletServiceRestConsumer.consumeDeductAmount(custID, fare);

			if (ifDeductionSuccess) {
				System.out.println("LOG : Amount deduction from wallet success for user : " + custID);
				// If the deduction was a success, send request Cab.rideStarted to the accepting
				// cabId
				boolean ifRideStarted = cabServiceRestConsumer.consumeRideStarted(selectedCab.getCabID(), rideID);
				if (ifRideStarted) {
					Logger.log("Ride started, CabID: " + selectedCab.getCabID() + ", RideID: " + rideID);
					ride.setCabStatus(selectedCab);
					ride.setRideState(RideStates.ONGOING);
					ride = rideRepo.save(ride);

					CabStatus cabStatus = ride.getCabStatus();
					cabStatus.setMinorState(CabMinorStates.GIVING_RIDE);
					cabStatus.setCurrPos(sourceLoc);

					cabStatusRepo.save(cabStatus);

					return rideID;
				} else {
					Logger.log("Ride id : " + rideID + " was rejected by Cab Service for Cab ID : "
							+ selectedCab.getCabID());
				}
			} else {
				// if deduction failed send ride cancelled to cab service
				Logger.log("Amount deduction from wallet failed for user : " + custID);
				boolean ifRideCancelled = cabServiceRestConsumer.consumeRideCanceled(selectedCab.getCabID(), rideID);
				if (ifRideCancelled) {
					Logger.log("Ride cancelled, CabID: " + selectedCab.getCabID() + ", RideID: " + rideID);
					rideRepo.delete(ride);
				}
			}

		}

		System.out.println("LOG : Not enough rides available (less than 3 rides)");
		return -1;

	}

	public static long calcFare(long sourcePos, long curPos, long destPos) {
		long fare = (Math.abs(sourcePos - curPos) + Math.abs(sourcePos - destPos)) * 10;

		return fare;
	}

}

class CabStatusComparatorByCurPos implements Comparator<CabStatus> {
	long srcLoc;

	public CabStatusComparatorByCurPos(long srcLoc) {
		this.srcLoc = srcLoc;
	}

	public int compare(CabStatus a, CabStatus b) {
		long relativeLocOfA = Math.abs(a.getCurrPos() - this.srcLoc);
		long relativeLocOfB = Math.abs(b.getCurrPos() - this.srcLoc);

		if (relativeLocOfA == relativeLocOfB)
			return Long.signum(a.getCabID() - b.getCabID());

		return Long.signum(relativeLocOfA - relativeLocOfB);
	}

}
