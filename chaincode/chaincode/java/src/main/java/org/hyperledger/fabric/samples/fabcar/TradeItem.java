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
        name = "TradeItem",
        info = @Info(
                title = "TradeItem contract",
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
public final class TradeItem implements ContractInterface {

    private final Genson genson = new Genson();

    private enum FabCarErrors {
        ITEM_NOT_FOUND,
        ITEM_ALREADY_EXISTS
    }

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        String[] ItemData = {
            "{ \"name\": \"iphone\", \"owner\": \"Geonsik\", \"price\": \"20\", \"state\": \"ing\" }",
            "{ \"name\": \"ipad\", \"owner\": \"Geonsik\", \"price\": \"30\", \"state\": \"ing\" }",
            "{ \"name\": \"airpod\", \"owner\": \"Geonsik\", \"price\": \"50\", \"state\": \"not\" }",
            "{ \"name\": \"Mac\", \"owner\": \"Passat\", \"price\": \"40\", \"state\": \"end\" }",
            "{ \"name\": \"Pro\", \"owner\": \"S\", \"price\": \"30\", \"state\": \"end\" }"
        };

        for (int i = 0; i < ItemData.length; i++) {
            String key = String.format("ITEM%d", i);

            Item item = genson.deserialize(ItemData[i], Item.class);
            String ItemState = genson.serialize(item);
            stub.putStringState(key, ItemState);
        }
    }

        /**
     * Creates a new car on the ledger.
     *
     * @param ctx the transaction context
     * @param key the key for the new car
     * @param name the itemname
     * @param owner the model of the new car
     * @param price the color of the new car
     * @param state the owner of the new car
     * @return the created Car
     */


    //물품을 등록하는 함수이며 createCar와 매칭.
    @Transaction()
    public Item registerItem(final Context ctx, final String key, final String name,
    final String owner, final String price) {
        ChaincodeStub stub = ctx.getStub();

        String ItemState = stub.getStringState(key);
        if (!ItemState.isEmpty()) {
            String errorMessage = String.format("Item %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.ITEM_ALREADY_EXISTS.toString());
        }

        Item item = new Item(name,owner,price,"not");
        ItemState = genson.serialize(item);
        stub.putStringState(key, ItemState);

        return item;
    } 

    //자신의 물품 판매한다고 등록하는 함수
    @Transaction()
    public Item sellMyItem(final Context ctx, final String key, final String name, 
    final String owner, final String price) {
        ChaincodeStub stub = ctx.getStub();
        String itemState = stub.getStringState(key);

        if (itemState.isEmpty()) {
            String errorMessage = String.format("Item %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.ITEM_NOT_FOUND.toString());
        }

        Item item = genson.deserialize(itemState, Item.class);

        Item newItem = new Item(item.getName(), item.getOwner(), price, "ing");

        String newItemState = genson.serialize(newItem);
        stub.putStringState(key, newItemState);

        return newItem;
    }
    
    //판매한다고 등록된 물품을 구매하는 함수
    @Transaction()
    public Item buyUserItem(final Context ctx, final String key, final String name, 
    final String owner, final String newOwner)  {
        ChaincodeStub stub = ctx.getStub();
        String itemState = stub.getStringState(key);

        if (itemState.isEmpty()) {
            String errorMessage = String.format("Item %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.ITEM_NOT_FOUND.toString());
        }

        Item item = genson.deserialize(itemState, Item.class);

        Item newItem = new Item(item.getName(), newOwner, item.getPrice(), "end");

        String newItemState = genson.serialize(newItem);
        stub.putStringState(key, newItemState);

        return newItem;
    }

    //물품의 소유자를 변경하는 함수, ChainCarOwner와 매칭
    @Transaction()
    public Item changeItemOwner(final Context ctx, final String key, final String newOwner)  {
        ChaincodeStub stub = ctx.getStub();
        String itemState = stub.getStringState(key);

        if (itemState.isEmpty()) {
            String errorMessage = String.format("Car %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.ITEM_NOT_FOUND.toString());
        }

        Item item = genson.deserialize(itemState, Item.class);

        Item newItem = new Item(item.getName(), newOwner, item.getPrice(), item.getState());

        String newItemState = genson.serialize(newItem);
        stub.putStringState(key, newItemState);

        return newItem;
    }
    
    @Transaction()
    public String getMyItems(final Context ctx,final String owner)  {

        ChaincodeStub stub = ctx.getStub();

        final String startKey = "ITEM0";
        final String endKey = "ITEM99";

        List<ItemQueryResult> queryResults = new ArrayList<ItemQueryResult>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Item item = genson.deserialize(result.getStringValue(), Item.class);
            if(item.getOwner().equals(owner) && item.getState().equals("not")){
                queryResults.add(new ItemQueryResult(result.getKey(), item));
                System.out.println("item's owner"+item.getOwner());
            }
            else{
                continue;
            }
        }
        final String response = genson.serialize(queryResults);
        return response;
    }

    //모든 물품 조회
    @Transaction()
    public String getAllRegisteredItems(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "ITEM0";
        final String endKey = "ITEM99";
        List<ItemQueryResult> queryResults = new ArrayList<ItemQueryResult>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);
        for (KeyValue result: results) {
            Item item = genson.deserialize(result.getStringValue(), Item.class);
            if(item.getState().equals("ing")){
                queryResults.add(new ItemQueryResult(result.getKey(), item));
            }
            else{
                continue;
            }
        }
        final String response = genson.serialize(queryResults);
        return response;
    }

    @Transaction()
    public String getAllOrderedItems(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "ITEM0";
        final String endKey = "ITEM99";
        List<ItemQueryResult> queryResults = new ArrayList<ItemQueryResult>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);
        for (KeyValue result: results) {
            Item item = genson.deserialize(result.getStringValue(), Item.class);
            if(item.getState().equals("end")){
                queryResults.add(new ItemQueryResult(result.getKey(), item));
            }
            else{
                continue;
            }
            
        }
        final String response = genson.serialize(queryResults);
        return response;
    }

    @Transaction()
    public String getAllItems(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "ITEM0";
        final String endKey = "ITEM99";
        List<ItemQueryResult> queryResults = new ArrayList<ItemQueryResult>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);
        for (KeyValue result: results) {
            Item item = genson.deserialize(result.getStringValue(), Item.class);
            queryResults.add(new ItemQueryResult(result.getKey(), item));
        }
        final String response = genson.serialize(queryResults);
        return response;
    }

    @Transaction()
    public void earnBalance(final Context ctx, final String name) {
        ChaincodeStub stub = ctx.getStub();
        String walletState = stub.getStringState(name);

        if (walletState.isEmpty()) {
            Token newwallet = new Token(name,0);
            String newWalletState = genson.serialize(newwallet);

            stub.putStringState(name,newWalletState);
        }
        else{
            Token token = genson.deserialize(walletState, Token.class);

            Token newwallet = new Token(token.getOwner(),token.getBalance()+100);
            String newWalletState = genson.serialize(newwallet);
    
            stub.putStringState(name,newWalletState);
        }

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

        if (walletState.isEmpty()) {
            return "0";
        }
        List<TokenQueryResult> queryResults = new ArrayList<TokenQueryResult>();
  
        Token token = genson.deserialize(walletState,Token.class);
        queryResults.add(new TokenQueryResult(name, token));
        
        final String response = genson.serialize(queryResults);
        return response;
    }

}
