package methods;

import constant.Constant;
import constant.SecretInfo;
import io.zksync.ZkSyncWallet;
import io.zksync.abi.TransactionEncoder;
import io.zksync.crypto.signer.EthSigner;
import io.zksync.crypto.signer.PrivateKeyEthSigner;
import io.zksync.methods.request.Transaction;
import io.zksync.protocol.ZkSync;
import io.zksync.protocol.core.Token;
import io.zksync.protocol.core.ZkBlockParameterName;
import io.zksync.transaction.fee.DefaultTransactionFeeProvider;
import io.zksync.transaction.fee.ZkTransactionFeeProvider;
import io.zksync.transaction.response.ZkSyncTransactionReceiptProcessor;
import io.zksync.transaction.type.Transaction712;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

import static io.zksync.transaction.manager.ZkSyncTransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH;
import static io.zksync.transaction.manager.ZkSyncTransactionManager.DEFAULT_POLLING_FREQUENCY;

public class WalletService {

    public static ZkSync zksync = ZkSync.build(new HttpService(Constant.ZksMainNet));


    public ZkSyncWallet getWallet(){
        EthSigner signer = null;
        try {
            BigInteger chainId = zksync.ethChainId().send().getChainId();
            signer = PrivateKeyEthSigner.fromMnemonic(SecretInfo.privateKey,chainId.longValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ZkSyncWallet(zksync,signer,Token.ETH);

    }

//    public void transferETH() throws IOException, TransactionException {
//        ZkSyncTransactionReceiptProcessor processor = new ZkSyncTransactionReceiptProcessor(zksync, DEFAULT_POLLING_FREQUENCY, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
//        BigInteger chainId = zksync.ethChainId().send().getChainId();
//        System.out.println(chainId);
//        BigInteger amountInWei = Convert.toWei("0.001", Convert.Unit.ETHER).toBigInteger();
//
//        BigInteger nonce = zksync
//                .ethGetTransactionCount(signer.getAddress(), ZkBlockParameterName.COMMITTED).send()
//                .getTransactionCount();
//
//        Transaction estimate = Transaction.createFunctionCallTransaction(
//                signer.getAddress(),
//                "0x30c0D71677f68Af817c64BbDc92DE3Ddef7cc450",
//                BigInteger.ZERO,
//                BigInteger.ZERO,
//                amountInWei,
//                null
//        );
//        System.out.println(estimate.getValueNumber());
//        System.out.println("gas price:"+estimate.getGasPrice());
//
//        ZkTransactionFeeProvider feeProvider = new DefaultTransactionFeeProvider(zksync, Token.ETH);
//        System.out.println(1);
//        Transaction712 transaction = new Transaction712(
//                chainId.longValue(),
//                nonce,
//                feeProvider.getGasLimit(estimate),
//                estimate.getTo(),
//                estimate.getValueNumber(),
//                estimate.getData(),
//                BigInteger.valueOf(100000000L),
//                feeProvider.getGasPrice(),
//                signer.getAddress(),
//                estimate.getEip712Meta()
//        );
//        System.out.println(2);
//        String signature = signer.getDomain().thenCompose(domain -> signer.signTypedData(domain, transaction)).join();
//        byte[] message = TransactionEncoder.encode(transaction, TransactionEncoder.getSignatureData(signature));
//
//        EthSendTransaction sent = zksync.ethSendRawTransaction(Numeric.toHexString(message)).send();
//
//        TransactionReceipt receipt = processor.waitForTransactionReceipt(sent.getResult());
//        System.out.println(receipt);
//    }



}
