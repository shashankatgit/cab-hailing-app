package cab_hailing.wallet_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cab_hailing.wallet_service.db_init.DBInitializer;
import cab_hailing.wallet_service.service.WalletActionsService;

@RestController
public class WalletActionsController {
	
	@Autowired
	WalletActionsService walletActionsService;
	
	@Autowired
	DBInitializer dbInitializer;
	
	@GetMapping("getBalance")
	public long getBalance(@RequestParam long custID) {
		return walletActionsService.getBalance(custID);
	}
	
	@GetMapping("deductAmount")
	public boolean deductAmount(@RequestParam long custID, @RequestParam long amount) {
		
		return walletActionsService.deductAmount(custID, amount);
	}
	
	@GetMapping("addAmount")
	public boolean addAmount(@RequestParam long custID, @RequestParam long amount) {
		return walletActionsService.addAmount(custID, amount);
	}
	
	@GetMapping("reset")
	public void reset() {
		dbInitializer.resetAndLoadAllTables();		
	}
	
}
