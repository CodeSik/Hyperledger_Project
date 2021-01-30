/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcar;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Token {


    @Property()
    private final String owner;

    @Property()
    private final int balance;



    public String getOwner() {
        return owner;
    }
    public int getBalance() {
        return balance;
    }




    public Token( @JsonProperty("owner") final String owner, @JsonProperty("balance") final int balance){
        this.owner = owner; // 소유자 이름
        this.balance = balance;
    }


    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getBalance());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) 
        + "[owner=" + owner + "Balance=" + balance + "]";
    }
}
