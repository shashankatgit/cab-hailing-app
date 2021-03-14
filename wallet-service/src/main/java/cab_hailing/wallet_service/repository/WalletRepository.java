package cab_hailing.wallet_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cab_hailing.wallet_service.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
	
}
