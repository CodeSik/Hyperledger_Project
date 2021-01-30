/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcar;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Item {

    @Property()
    private final String name;

    @Property()
    private final String owner;

    @Property()
    private final String price;

    @Property()
    private final String state;

    public String getOwner() {
        return owner;
    }
    public String getName() {
        return name;
    }
    public String getPrice() {
        return price;
    }
    public String getState() {
        return state;
    }



    public Item(@JsonProperty("name") final String name, @JsonProperty("owner") final String owner,
    @JsonProperty("price") final String price, @JsonProperty("state") final String state) {
        this.name = name; //제조사
        this.owner = owner; // 소유자 이름
        this.price = price;
        this.state = state;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Item other = (Item) obj;

        return Objects.deepEquals(new String[] {getName(), getOwner()},
                new String[] {other.getName(), other.getOwner()});
    }  

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getOwner(), getPrice(), getState());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) 
        + "[name=" + name + "owner=" + owner + "price=" + price + "state=" + state + "]";
    }
}
