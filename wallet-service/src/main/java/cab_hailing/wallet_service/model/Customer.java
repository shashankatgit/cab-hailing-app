package cab_hailing.wallet_service.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "Customers")
public class Customer {
	@Id
	@GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "CustSeqGen"
    )
    @SequenceGenerator(name = "CustSeqGen",
                initialValue = 1001, allocationSize = 1
    )
	@Column(name = "cust_id")
	long custID;

	
	
	public Customer(long custID, String password) {
		super();
		this.custID = custID;
		this.password = password;
	}

	@Column(name = "password")
	String password;
	
	@OneToOne(mappedBy="customer")
	Wallet wallet;

	public Customer(String password) {
		super();
		this.password = password;
	}
	
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Customer(long custID) {
		super();
		this.custID = custID;
		this.password = "pass"+custID;
	}
	
	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
