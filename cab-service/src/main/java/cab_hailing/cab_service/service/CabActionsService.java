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
	
	@Autowired
	CabRepository cabRepo;
	
	@Autowired
	CabStatusRepository cabStatusRepo;
	
	
	
	@Transactional
	public boolean signIn(long cabID, long initialPos) {
		
		// Check if cab id is valid and cab in signed out state
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if(cab == null) {
			return false;
		}
		
		CabStatus cabStatus = cab.getCabStatus();
		
		// if cab status record not found, then insert the record
		// shouldn't happen ideally, but if so, then this is a quick fix
		if(cabStatus == null) {
			cabStatus = new CabStatus(cabID);
			cabStatus = cabStatusRepo.save(cabStatus);
		}
		
		// check if cab in signed out state
		if(cabStatus.getMajorState()!=null && cabStatus.getMajorState().equals(CabMajorStates.SIGNED_OUT)) {
			
			// forward the request to RideService
			
			// if RideService responds with success
			if(true) {
				cabStatus.setMajorState(CabMajorStates.SIGNED_IN);
				cabStatus.setMinorState(CabMinorStates.AVAILABLE);
				
				cabStatusRepo.save(cabStatus);
				
				return true;
			}
		}
		
		return false;
	}
	
	@Transactional
	public boolean signOut(long cabID) {
		// Check if cab id is valid and cab in signed out state
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if(cab == null) {
			return false;
		}
		
		CabStatus cabStatus = cab.getCabStatus();
		
		if(cabStatus != null) {
			String cabMajorState = cabStatus.getMajorState();
			String cabMinorState = cabStatus.getMinorState();
			
			// cab is signed in and available
			if(cabMajorState == null || !cabMajorState.equals(CabMajorStates.SIGNED_IN))
				return false;
			
			if(cabMinorState == null || !cabMinorState.equals(CabMinorStates.AVAILABLE))
				return false;
					
			
			// forward the request to RideService
			// if RideService responds with success, set status to available
			if(true) {
				cabStatus.setMajorState(CabMajorStates.SIGNED_OUT);
				cabStatus.setMinorState(CabMinorStates.NONE);
				cabStatusRepo.save(cabStatus);
				
				return true;
			}
		}
				
		return false;
	}
	
	@Transactional
	public long numRides(long cabID) {
		Cab cab = cabRepo.findById(cabID).orElse(null);
		if(cab == null) {
			return -1;
		}
		
		CabStatus cabStatus = cab.getCabStatus();
		
		if(cabStatus != null) {
			Long nRidesGiven = cabStatus.getnRidesGiven();

			// cab is signed in and available
			if(nRidesGiven != null)
				return nRidesGiven.longValue();
		}
		return -1;
	}
	
}
