package application;

import io.zksync.ZkSyncWallet;
import io.zksync.protocol.core.Token;
import methods.BalanceService;
import methods.WalletService;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;

public class Main {
    public static final String EraLendAddress_USDC = "0xe62b571E9F40D158cB20796C56E93475d896c56D";

    public static void main(String[] args) {
//        BalanceService balanceService = new BalanceService();
//        balanceService.getBalancesOfAddress(EraLendAddress_USDC);

        ZkSyncWallet wallet = new WalletService().getWallet();
        try {
            String address = wallet.getSigner().getAddress();
            System.out.println(address);
            System.out.println(wallet.getBalance(Token.ETH).send());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}