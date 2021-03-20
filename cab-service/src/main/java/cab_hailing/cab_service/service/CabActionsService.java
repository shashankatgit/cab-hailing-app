package cab_hailing.cab_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cab_hailing.cab_service.model.Cab;
import cab_hailing.cab_service.model.CabStatus;
import cab_hailing.cab_service.repository.CabRepository;
import cab_hailing.cab_service.repository.CabStatusRepository;
import cab_hailing.cab_service.values.CabMajorStates;
import cab_hailing.cab_service.values.CabMinorStates;

@Component
public class CabActionsService {

	// ---------------------------------------------------------------------------------------------

	@Autowired
	CabRepository cabRepo;

	@Autowired
	CabStatusRepository cabStatusRepo;

	// ---------------------------------------------------------------------------------------------

	/*
	 * Cab driver will send this request, to indicate his/her desire to sign-in with
	 * starting location initialPos. If cabId is a valid ID and the cab is currently
	 * in signed-out state, then send a request to RideService.cabSignsIn, forward
	 * the response from RideService.cabSignsIn back to the driver, and transition
	 * to signed-in state iff the response is true. Otherwise, else respond with -1
	 * and do not change state.
	 */
	@Transactional
	public boolean signIn(long cabID, long initialPos) {

		// Check if cabID is valid
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if (cab == null) {
			return false;
		}

		// Get corresponding record from cab_status. If cab status record not found,
		// then insert the record houldn't happen ideally, but if so, then this is a
		// quick fix
		CabStatus cabStatus = cab.getCabStatus();
		if (cabStatus == null) {
			cabStatus = new CabStatus(cabID);
			cabStatus = cabStatusRepo.save(cabStatus);
		}

		// Check if cab is in SIGNED_OUT state
		if (cabStatus.getMajorState() != null && cabStatus.getMajorState().equals(CabMajorStates.SIGNED_OUT)) {

			// TODO:forward the request to RideService

			// If RideService responds with success
			if (true) {
				cabStatus.setMajorState(CabMajorStates.SIGNED_IN);
				cabStatus.setMinorState(CabMinorStates.AVAILABLE);
				cabStatusRepo.save(cabStatus);
				return true;
			}
		}

		return false;
	}

	// ---------------------------------------------------------------------------------------------

	/*
	 * Cab driver will send this request, to indicate his/her desire to sign-out. If
	 * cabId is a valid ID and the cab is currently in signed-in state, then send a
	 * request to RideService.cabSignsOut, forward the response from
	 * RequestRide.cabSignsOut back to the driver, and transition to signed-out
	 * state iff the response is true. Otherwise, else respond with -1 and do not
	 * change state.
	 */
	@Transactional
	public boolean signOut(long cabID) {

		// Check if cabID is valid
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if (cab == null) {
			return false;
		}

		// Get corresponding record from cab_status.
		CabStatus cabStatus = cab.getCabStatus();

		if (cabStatus != null) {
			String cabMajorState = cabStatus.getMajorState();
			String cabMinorState = cabStatus.getMinorState();

			// Check if cab is SIGNED_IN
			if (cabMajorState == null || !cabMajorState.equals(CabMajorStates.SIGNED_IN))
				return false;

			// Check if cab is AVAILABLE
			if (cabMinorState == null || !cabMinorState.equals(CabMinorStates.AVAILABLE))
				return false;

			// TODO:forward the request to RideService

			// If RideService responds with success
			if (true) {
				cabStatus.setMajorState(CabMajorStates.SIGNED_OUT);
				cabStatus.setMinorState(CabMinorStates.NONE);
				cabStatusRepo.save(cabStatus);
				return true;
			}
		}

		return false;
	}

	// ---------------------------------------------------------------------------------------------

	/*
	 * To be used mainly for testing purposes. If cabId is invalid, return -1.
	 * Otherwise, if cabId is currently signed-in then return number of rides given
	 * so far after the last sign-in (including ongoing ride if currently in
	 * giving-ride state), else return 0.
	 */
	@Transactional
	public long numRides(long cabID) {
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if (cab == null) {
			return -1;
		}

		CabStatus cabStatus = cab.getCabStatus();

		if (cabStatus != null) {
			Long nRidesGiven = cabStatus.getnRidesGiven();

			// cab is signed in and available
			if (nRidesGiven != null)
				return nRidesGiven.longValue();
		}
		return -1;
	}

	// ---------------------------------------------------------------------------------------------

}
