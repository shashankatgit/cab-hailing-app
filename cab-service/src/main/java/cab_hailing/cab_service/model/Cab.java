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
@Table(name = "Cabs")
public class Cab {
	
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
	@Column(name = "cab_password")
	String cabPassword;

	
	//-----------------------------------------------------
	public Cab(long cabID, String cabPassword) {
		super();
		this.cabID = cabID;
		this.cabPassword = cabPassword;
	}
	

	//-----------------------------------------------------
	public long getCabID() {
		return cabID;
	}

	public void setCabID(long cabID) {
		this.cabID = cabID;
	}
	
	
	//-----------------------------------------------------
	public String getCabPassword() {
		return cabPassword;
	}

	public void setCabPassword(String cabPassword) {
		this.cabPassword = cabPassword;
	}	
	

}
