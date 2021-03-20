package cab_hailing.ride_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import cab_hailing.ride_service.model.CabStatus;
import cab_hailing.ride_service.repository.CabStatusRepository;
import cab_hailing.ride_service.repository.RideRepository;
import cab_hailing.ride_service.values.CabMajorStates;
import cab_hailing.ride_service.values.CabMinorStates;

@Component
public class CabActionsService {

	// ---------------------------------------------------------------------------------------------

	@Autowired
	RideRepository rideRepo;

	@Autowired
	CabStatusRepository cabStatusRepo;

	// ---------------------------------------------------------------------------------------------

	/*
	 * Cab cabId invokes this to sign-in and notify the company that it wants to
	 * start its working day at location “initialPos”. Response is true from the
	 * company iff the cabId is a valid one and the cab is not already signed in.
	 */
	@Transactional
	public boolean cabSignsIn(long cabID, long initialPos) {

		// Check if cab id is valid
		// TODO: If cab status record not found, then insert the record houldn't happen
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
	 */
	
	@Transactional
	public String getCabStatus(long cabID) {
		
		// Check if cab id is valid
		CabStatus cabStatus = cabStatusRepo.findById(cabID).orElse(null);

		// Check if cab is in AVAILABLE state
		if (cabStatus != null) {
			String cabMajorState = cabStatus.getMajorState();
			String cabMinorState = cabStatus.getMinorState();
			Long currPos = cabStatus.getCurrPos().longValue();
			
			String status = "";
			
			if(cabMajorState != null) {
				if(cabMajorState.equals(CabMajorStates.SIGNED_OUT)) {
					status += "SIGNED_OUT ";
				}else if(cabMajorState.equals(CabMajorStates.SIGNED_IN)){
					if(cabMinorState.equals(CabMinorStates.AVAILABLE)) {
						status += "AVAILABLE ";
					}else if(cabMinorState.equals(CabMinorStates.COMMITTED)){
						status += "COMMITTED ";
					}else {
						status += "GIVING_RIDE ";
					}
				}
			}
			
			status += currPos;
			
			return status;
		}
		
		return "-1";
	}


}
