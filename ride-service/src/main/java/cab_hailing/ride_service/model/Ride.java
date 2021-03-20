package cab_hailing.ride_service.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "Rides")
public class Ride {
	
	//----------------------------------------------
	@Id
	@GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "RideSeqGen"
    )
    @SequenceGenerator(name = "RideSeqGen",
                initialValue = 3001, allocationSize = 1
    )
	@Column(name = "ride_id")
	private long rideID;	
	
	@Column(name = "cust_id")
	private long custID;
		
	@Column(name = "cab_id")
	private long cabID;
		
	@Column(name = "ride_state",length=1)
	private String rideState;
	
		
	//-----------------------------------------------------
	@OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private CabStatus cabStatus;
		
	
	//----------------------------------------------
	public Ride() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Ride(long rideID, long custID, long cabID, String rideState, CabStatus cabStatus) {
		super();
		this.rideID = rideID;
		this.custID = custID;
		this.cabID = cabID;
		this.rideState = rideState;
		this.cabStatus = cabStatus;
	}
	
	
	//----------------------------------------------
	public long getRideID() {
		return rideID;
	}

	public void setRideID(long rideID) {
		this.rideID = rideID;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public long getCabID() {
		return cabID;
	}

	public void setCabID(long cabID) {
		this.cabID = cabID;
	}

	public String getRideState() {
		return rideState;
	}

	public void setRideState(String rideState) {
		this.rideState = rideState;
	}

	public CabStatus getCabStatus() {
		return cabStatus;
	}

	public void setCabStatus(CabStatus cabStatus) {
		this.cabStatus = cabStatus;
	}
	 
	
}
