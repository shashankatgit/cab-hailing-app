package cab_hailing.ride_service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import cab_hailing.ride_service.values.CabMajorStates;
import cab_hailing.ride_service.values.CabMinorStates;

@Entity
@Table(name = "Cab_Status")
public class CabStatus {

	// Columns----------------------------------------------
	@Id
	@Column(name = "cab_id")
	private Long cabID;

	@Column(name = "major_state", length = 1)
	private String majorState;

	@Column(name = "minor_state", length = 1)
	private String minorState;

	@Column(name = "curr_pos")
	private Long currPos;

	// -----------------------------------------------------
	@OneToOne(mappedBy = "cabStatus")
	private Ride ride;

	// -------------------------------------------------------
	public CabStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CabStatus(Long cabID, String majorState, String minorState, Long currPos, Ride ride) {
		super();
		this.cabID = cabID;
		this.majorState = majorState;
		this.minorState = minorState;
		this.currPos = currPos;
		this.ride = ride;
	}

	public CabStatus(Long cabID, Long currPos) {
		super();
		this.cabID = cabID;
		this.majorState = CabMajorStates.SIGNED_OUT;
		this.minorState = CabMinorStates.NONE;
		this.currPos = currPos;
	}

	// -------------------------------------------------------
	public Long getCabID() {
		return cabID;
	}

	public void setCabID(Long cabID) {
		this.cabID = cabID;
	}

	public String getMajorState() {
		return majorState;
	}

	public void setMajorState(String majorState) {
		this.majorState = majorState;
	}

	public String getMinorState() {
		return minorState;
	}

	public void setMinorState(String minorState) {
		this.minorState = minorState;
	}

	public Long getCurrPos() {
		return currPos;
	}

	public void setCurrPos(Long currPos) {
		this.currPos = currPos;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

}
