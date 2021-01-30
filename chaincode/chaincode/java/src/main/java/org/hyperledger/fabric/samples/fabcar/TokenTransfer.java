/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcar;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

// import jdk.internal.jshell.tool.resources.l10n;

import com.owlike.genson.Genson;


@Contract(
        name = "TokenTransfer",
        info = @Info(
                title = "TokenTransfer contract",
                description = "The hyperlegendary Item contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "f.carr@example.com",
                        name = "F Carr",
                        url = "https://hyperledger.example.com")))
@Default
public final class TokenTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum FabCarErrors {
        ITEM_NOT_FOUND,
        ITEM_ALREADY_EXISTS
    }

    @Transaction()
    public void earnBalance(final Context ctx, final String name) {
        ChaincodeStub stub = ctx.getStub();
        String walletState = stub.getStringState(name);

        if (walletState.isEmpty()) {
            String errorMessage = String.format("%s 's wallet is empty", name);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.ITEM_NOT_FOUND.toString());
        }

        Token wallet = new Token(name,100);
        walletState = genson.serialize(wallet);
        stub.putStringState(name,walletState);
    }


    @Transaction()
    public void transferBalance(final Context ctx, final String from, final String to, final int price) {
        ChaincodeStub stub = ctx.getStub();

        String walletState_from = stub.getStringState(from);

        if (walletState_from.isEmpty()) {
            String errorMessage = String.format("%s 's wallet is empty", from);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.ITEM_NOT_FOUND.toString());
        }

        String walletState_to = stub.getStringState(to);

        if (walletState_to.isEmpty()) {
            String errorMessage = String.format("%s 's wallet is empty", to);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.ITEM_NOT_FOUND.toString());
        }

        Token token_from = genson.deserialize(walletState_from,Token.class);
        Token token_to = genson.deserialize(walletState_to,Token.class);

        Token newToken_from = new Token(token_from.getOwner(),token_from.getBalance() - price);
        Token newToken_to = new Token(token_to.getOwner(),token_to.getBalance() + price);

        String newWalletState_from = genson.serialize(newToken_from);
        String newWalletState_to = genson.serialize(newToken_to);

        stub.putStringState(from, newWalletState_from);
        stub.putStringState(to, newWalletState_to);
    } 

  

    @Transaction()
    public String getMyBalance(final Context ctx,final String name)  {

        ChaincodeStub stub = ctx.getStub();
        
        String walletState = stub.getStringState(name);

        

        List<TokenQueryResult> queryResults = new ArrayList<TokenQueryResult>();
  
        Token token = genson.deserialize(walletState,Token.class);
        queryResults.add(new TokenQueryResult(walletState, token));
        
        final String response = genson.serialize(queryResults);
        return response;
    }
}
