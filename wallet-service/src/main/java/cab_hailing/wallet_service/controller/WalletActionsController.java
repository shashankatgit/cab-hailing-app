package cab_hailing.wallet_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cab_hailing.wallet_service.service.WalletService;

@RestController
public class WalletActionsController {
	
	@Autowired
	WalletService walletService;
	
	@GetMapping("getBalance")
	public long getBalance(@RequestParam long custID) {
		return walletService.getBalance(custID);
	}
	
	@GetMapping("deductAmount")
	public boolean deductAmount(@RequestParam long custID, @RequestParam long amount) {
		
		return walletService.deductAmount(custID, amount);
	}
	
	@GetMapping("addAmount")
	public boolean addAmount(@RequestParam long custID, @RequestParam long amount) {
		return walletService.addAmount(custID, amount);
	}
	
	@GetMapping("reset")
	public void reset() {
		
		
	}
	
}
