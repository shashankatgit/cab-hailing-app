package cab_hailing.wallet_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cab_hailing.wallet_service.model.Customer;
import cab_hailing.wallet_service.model.Wallet;
import cab_hailing.wallet_service.repository.CustomerRepository;
import cab_hailing.wallet_service.repository.WalletRepository;



/**
 * This service provides atomic transactions on users' wallet.
 * The following services are provided
 * 		- getBalance
 * 		- deductAmount
 * 		- addAmount
 * 		- reset
 * 
 * @author Shashank Singh (cse.shashanksingh@gmail.com, shashanksing@iisc.ac.in)
 */

@Service
public class WalletActionsService {
	
	@Autowired
	CustomerRepository custRep;
	
	@Autowired
	WalletRepository walletRep;
	
	@Transactional
	public long getBalance(long custID) {
		Customer customer = custRep.findById(custID).orElse(new Customer());
		Wallet custWallet = customer.getWallet();
		
		if(custWallet != null )
			return custWallet.getBalanceAmount();
		else
			return -1;
	}
	
	@Transactional
	public boolean deductAmount(long custID, long deductionAmount) {
		Customer customer = custRep.findById(custID).orElse(new Customer());
		Wallet custWallet = customer.getWallet();
		
		if(custWallet != null ) {
			long availBalance = custWallet.getBalanceAmount();
			
			
			
			if(availBalance >= deductionAmount) {
				custWallet.setBalanceAmount(availBalance - deductionAmount);
				walletRep.save(custWallet);
				
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	@Transactional
	public boolean addAmount(long custID, long additionAmount) {
		Customer customer = custRep.findById(custID).orElse(new Customer());
		Wallet custWallet = customer.getWallet();
		
		if(custWallet != null ) {
			long availBalance = custWallet.getBalanceAmount();
			
			custWallet.setBalanceAmount(availBalance + additionAmount);
			walletRep.save(custWallet);
			
			return true;

		}
		else
			return false;
	}
}
