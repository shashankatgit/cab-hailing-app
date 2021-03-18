package cab_hailing.cab_service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

//import cab_hailing.wallet_service.model.Wallet;


@Entity
@Table(name = "Cab_Status")
public class CabStatus {
	
	//Columns----------------------------------------------
	@Id
	@GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "CabSeqGen"
    )
    @SequenceGenerator(name = "CabSeqGen",
                initialValue = 2001, allocationSize = 1
    )
	@Column(name = "cab_id")
	long cabID;	
	
	//-----------------------------------------------------
//	enum Major_State {
//	    SIGNED_OUT,
//	    SIGNED_IN
//	}
//	
//	@Column(name = "major_state")
//	Major_State majorState;	
	@Column(name = "major_state")
	long major_state;
	
	//-----------------------------------------------------
//	enum Minor_State {
//	    AVAILABLE,
//	    COMMITTED,
//	    GIVING_RIDE,
//	    NULL
//	}	
//	
//	@Column(name = "minor_state")
//	Minor_State minorState;	
	@Column(name = "minor_state")
	long minor_state;
	
	//-----------------------------------------------------
	@Column(name = "curr_ride_id")
	long currRideID;

	public long getCabID() {
		return cabID;
	}

	public void setCabID(long cabID) {
		this.cabID = cabID;
	}

	public long getMajor_state() {
		return major_state;
	}

	public void setMajor_state(long major_state) {
		this.major_state = major_state;
	}

	public long getMinor_state() {
		return minor_state;
	}

	public void setMinor_state(long minor_state) {
		this.minor_state = minor_state;
	}

	public long getCurrRideID() {
		return currRideID;
	}

	public void setCurrRideID(long currRideID) {
		this.currRideID = currRideID;
	}

	public CabStatus(long cabID, long major_state, long minor_state, long currRideID) {
		super();
		this.cabID = cabID;
		this.major_state = major_state;
		this.minor_state = minor_state;
		this.currRideID = currRideID;
	}
	
	
	//-----------------------------------------------------
	

}
