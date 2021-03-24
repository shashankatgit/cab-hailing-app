package cab_hailing.cab_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cab_hailing.cab_service.Logger;
import cab_hailing.cab_service.model.Cab;
import cab_hailing.cab_service.model.CabStatus;
import cab_hailing.cab_service.repository.CabRepository;
import cab_hailing.cab_service.repository.CabStatusRepository;
import cab_hailing.cab_service.rest_consumers.RideServiceRestConsumer;
import cab_hailing.cab_service.values.CabMajorStates;
import cab_hailing.cab_service.values.CabMinorStates;

@Component
public class RideActionsService {
	@Autowired
	CabRepository cabRepo;

	@Autowired
	CabStatusRepository cabStatusRepo;

	@Autowired
	RideServiceRestConsumer rideServiceRestConsumer;

	@Transactional
	public boolean requestRide(long cabID, long rideID, long sourceLoc, long destinationLoc) {
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if (cab == null) {
			return false;
		}

		CabStatus cabStatus = cab.getCabStatus();
		if (cabStatus != null) {
			String cabMajorState = cabStatus.getMajorState();
			String cabMinorState = cabStatus.getMinorState();
			Long nRequestsRecvd = cabStatus.getnRequestsRecvd();

			if (cabMajorState == null || !cabMajorState.equals(CabMajorStates.SIGNED_IN))
				return false;

			if (cabMinorState == null || !cabMinorState.equals(CabMinorStates.AVAILABLE))
				return false;

			long nReqVal = nRequestsRecvd == null ? 0 : nRequestsRecvd.longValue();

			cabStatus.setnRequestsRecvd(nReqVal + 1);

			if (nReqVal % 2 != 0) {
				cabStatusRepo.save(cabStatus);
				return false;
			}

			cabStatus.setCurrRideID(rideID);
			cabStatus.setMinorState(CabMinorStates.COMMITTED);
			cabStatusRepo.save(cabStatus);
			return true;
		}

		return false;
	}

	@Transactional
	public boolean rideCancelled(long cabID, long rideID) {
		// Check if cab id is valid and in riding state with this rideID
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if (cab == null) {
			return false;
		}

		CabStatus cabStatus = cab.getCabStatus();
		if (cabStatus != null) {
			String cabMajorState = cabStatus.getMajorState();
			String cabMinorState = cabStatus.getMinorState();
			Long curRideID = cabStatus.getCurrRideID();

			// cab is signed in and available
			if (cabMajorState == null || !cabMajorState.equals(CabMajorStates.SIGNED_IN))
				return false;

			if (cabMinorState == null || !cabMinorState.equals(CabMinorStates.COMMITTED))
				return false;

			if (curRideID == null || curRideID.longValue() != rideID)
				return false;

			cabStatus.setMinorState(CabMinorStates.AVAILABLE);
			cabStatus.setCurrRideID(null);
			cabStatusRepo.save(cabStatus);

			return true;
		}

		return false;
	}

	@Transactional
	public boolean rideStarted(long cabID, long rideID) {
		// Check if cab id is valid and in riding state with this rideID
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if (cab == null) {
			return false;
		}

		CabStatus cabStatus = cab.getCabStatus();
		if (cabStatus != null) {
			String cabMajorState = cabStatus.getMajorState();
			String cabMinorState = cabStatus.getMinorState();
			Long curRideID = cabStatus.getCurrRideID();

			// cab is signed in and available
			if (cabMajorState == null || !cabMajorState.equals(CabMajorStates.SIGNED_IN))
				return false;

			if (cabMinorState == null || !cabMinorState.equals(CabMinorStates.COMMITTED))
				return false;

			if (curRideID == null || curRideID.longValue() != rideID)
				return false;

			cabStatus.setMinorState(CabMinorStates.GIVING_RIDE);
			cabStatus.setnRidesGiven(cabStatus.getnRidesGiven() + 1);

			cabStatusRepo.save(cabStatus);

			return true;
		}

		return false;
	}

	@Transactional
	public boolean rideEnded(long cabID, long rideID) {
		// Check if cab id is valid and in riding state with this rideID
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if (cab == null) {
			return false;
		}

		CabStatus cabStatus = cab.getCabStatus();
		if (cabStatus != null) {
			String cabMajorState = cabStatus.getMajorState();
			String cabMinorState = cabStatus.getMinorState();
			Long curRideID = cabStatus.getCurrRideID();

			// cab is signed in and available
			if (cabMajorState == null || !cabMajorState.equals(CabMajorStates.SIGNED_IN))
				return false;

			if (cabMinorState == null || !cabMinorState.equals(CabMinorStates.GIVING_RIDE))
				return false;

			if (curRideID == null || curRideID.longValue() != rideID)
				return false;

			// forward the request to RideService
			// if RideService responds with success, set status to available
			boolean ifRideEnded = rideServiceRestConsumer.consumeRideEnded(rideID);
			if (ifRideEnded) {
				Logger.log("Ride ended for ride id : " + rideID);
				cabStatus.setMinorState(CabMinorStates.AVAILABLE);
				cabStatus.setCurrRideID(null);

				cabStatusRepo.save(cabStatus);

				return true;
			}

		}

		return false;
	}
}
