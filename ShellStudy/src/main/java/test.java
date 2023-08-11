import constant.Constant;
import constant.SecretInfo;
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
import io.zksync.wrappers.ERC20;
import org.web3j.abi.FunctionEncoder;
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

public class test {
    public static void main(String ...args) throws IOException, TransactionException {
        ZkSync zksync = ZkSync.build(new HttpService(Constant.ZksTestNet));; // Initialize client
        EthSigner signer =  PrivateKeyEthSigner.fromMnemonic(SecretInfo.privateKey,280);; // Initialize signer
        ZkSyncTransactionReceiptProcessor processor = new ZkSyncTransactionReceiptProcessor(zksync, DEFAULT_POLLING_FREQUENCY, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
        BigInteger chainId = zksync.ethChainId().send().getChainId();

        BigInteger amountInWei = Convert.toWei("0x0000", Convert.Unit.ETHER).toBigInteger();

        BigInteger nonce = zksync
                .ethGetTransactionCount(signer.getAddress(), ZkBlockParameterName.COMMITTED).send()
                .getTransactionCount();

        Transaction estimate = Transaction.createFunctionCallTransaction(
                signer.getAddress(),
                "0x<receiver_address>",
                BigInteger.ZERO,
                BigInteger.ZERO,
                amountInWei,
                "0x"
        );

        ZkTransactionFeeProvider feeProvider = new DefaultTransactionFeeProvider(zksync, Token.ETH);

        Transaction712 transaction = new Transaction712(
                chainId.longValue(),
                nonce,
                feeProvider.getGasLimit(estimate),
                estimate.getTo(),
                estimate.getValueNumber(),
                estimate.getData(),
                BigInteger.valueOf(100000000L),
                feeProvider.getGasPrice(),
                signer.getAddress(),
                estimate.getEip712Meta()
        );

        String signature = signer.getDomain().thenCompose(domain -> signer.signTypedData(domain, transaction)).join();
        byte[] message = TransactionEncoder.encode(transaction, TransactionEncoder.getSignatureData(signature));

        EthSendTransaction sent = zksync.ethSendRawTransaction(Numeric.toHexString(message)).send();

        TransactionReceipt receipt = processor.waitForTransactionReceipt(sent.getResult());
    }
}