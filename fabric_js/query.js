/*
 * SPDX-License-Identifier: Apache-2.0
 */

'use strict';
var register = require('../fabric_js/registerUser.js')

const { Gateway, Wallets } = require('fabric-network');
const path = require('path');
const fs = require('fs');

var queryMyItem = async function(name){
    try {

        const ccpPath = path.resolve(__dirname, '..', '..' ,'fabric_project','test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        let ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));

        // Create a new file system based wallet for managing identities.
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);
        console.log("queryMyItem에 들어온 name",name);
        
        // Check to see if we've already enrolled the user.
        const identity = await wallet.get(name);
        if (!identity) {
            console.log('An identity for the user "appUser" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }
        // Create a new gateway for connecting to our peer node.
        const gateway = new Gateway();
        await gateway.connect(ccp, { wallet, identity: name, discovery: { enabled: true, asLocalhost: true } });

        // Get the network (channel) our contract is deployed to.
        const network = await gateway.getNetwork('mychannel');

        // Get the contract from the network.
        const contract = network.getContract('fabcar');

        //execute query function
        const result = await contract.evaluateTransaction('getMyItems', name);

        //query result value(string), string to json and use 
        //var test = JSON.parse(result);  -> string to json
        //console.log('test: ', test['make']);  -> use json object
        console.log(`Transaction has been evaluated, result is: ${result.toString()}`);

        return result.toString();
        

    } catch (error) {
        console.error(`Failed to evaluate transaction: ${error}`);
        process.exit(1);
    }
}

var queryAllRegistered = async function(name){
    try {

        const ccpPath = path.resolve(__dirname, '..', '..' ,'fabric_project','test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        let ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));

        // Create a new file system based wallet for managing identities.
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);
  
        // Check to see if we've already enrolled the user.
        const identity = await wallet.get(name);
        if (!identity) {
            console.log('An identity for the user "appUser" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }
        // Create a new gateway for connecting to our peer node.
        const gateway = new Gateway();
        await gateway.connect(ccp, { wallet, identity: name, discovery: { enabled: true, asLocalhost: true } });

        // Get the network (channel) our contract is deployed to.
        const network = await gateway.getNetwork('mychannel');

        // Get the contract from the network.
        const contract = network.getContract('fabcar');

        //execute query function
        const result = await contract.evaluateTransaction('getAllRegisteredItems')

        //query result value(string), string to json and use 
        //var test = JSON.parse(result);  -> string to json
        //console.log('test: ', test['make']);  -> use json object
        console.log(`Transaction has been evaluated, result is: ${result.toString()}`);

        return result.toString();
        

    } catch (error) {
        console.error(`Failed to evaluate transaction: ${error}`);
        process.exit(1);
    }
}

var queryAllOrderedItems = async function(name){
    try {

        const ccpPath = path.resolve(__dirname, '..', '..' ,'fabric_project','test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        let ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));

        // Create a new file system based wallet for managing identities.
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);

        // Check to see if we've already enrolled the user.
        const identity = await wallet.get(name);
        if (!identity) {
            console.log('An identity for the user "appUser" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;

        }
        // Create a new gateway for connecting to our peer node.
        const gateway = new Gateway();
        await gateway.connect(ccp, { wallet, identity: name, discovery: { enabled: true, asLocalhost: true } });

        // Get the network (channel) our contract is deployed to.
        const network = await gateway.getNetwork('mychannel');

        // Get the contract from the network.
        const contract = network.getContract('fabcar');

        //execute query function
        const result = await contract.evaluateTransaction('getAllOrderedItems')

        //query result value(string), string to json and use 
        //var test = JSON.parse(result);  -> string to json
        //console.log('test: ', test['make']);  -> use json object
        console.log(`Transaction has been evaluated, result is: ${result.toString()}`);

        return result.toString();
        

    } catch (error) {
        console.error(`Failed to evaluate transaction: ${error}`);
        process.exit(1);
    }
}
var queryAllItems = async function(name){
    try {

        const ccpPath = path.resolve(__dirname, '..', '..' ,'fabric_project','test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        let ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));

        // Create a new file system based wallet for managing identities.
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);
        console.log("queryMyBalance에 들어온 name",name);
        
        // Check to see if we've already enrolled the user.
        const identity = await wallet.get(name);
        if (!identity) {
            console.log('An identity for the user "appUser" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }
        // Create a new gateway for connecting to our peer node.
        const gateway = new Gateway();
        await gateway.connect(ccp, { wallet, identity: name, discovery: { enabled: true, asLocalhost: true } });

        // Get the network (channel) our contract is deployed to.
        const network = await gateway.getNetwork('mychannel');

        // Get the contract from the network.
        const contract = network.getContract('fabcar');

        //execute query function
        const result = await contract.evaluateTransaction('getAllItems');

        //query result value(string), string to json and use 
        //var test = JSON.parse(result);  -> string to json
        //console.log('test: ', test['make']);  -> use json object
        console.log(`Transaction has been evaluated, result is: ${result.toString()}`);

        return result.toString();
        

    } catch (error) {
        console.error(`Failed to evaluate transaction: ${error}`);
        process.exit(1);
    }
}

var queryMyBalance = async function(name){
    try {

        const ccpPath = path.resolve(__dirname, '..', '..' ,'fabric_project','test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        let ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));

        // Create a new file system based wallet for managing identities.
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);
        console.log("queryMyBalance에 들어온 name",name);
        
        // Check to see if we've already enrolled the user.
        const identity = await wallet.get(name);
        if (!identity) {
            console.log('An identity for the user "appUser" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }
        // Create a new gateway for connecting to our peer node.
        const gateway = new Gateway();
        await gateway.connect(ccp, { wallet, identity: name, discovery: { enabled: true, asLocalhost: true } });

        // Get the network (channel) our contract is deployed to.
        const network = await gateway.getNetwork('mychannel');

        // Get the contract from the network.
        const contract = network.getContract('fabcar');

        //execute query function
        const result = await contract.evaluateTransaction('getMyBalance', name);

        //query result value(string), string to json and use 
        //var test = JSON.parse(result);  -> string to json
        //console.log('test: ', test['make']);  -> use json object
        console.log(`Transaction has been evaluated, result is: ${result.toString()}`);

        return result.toString();
        

    } catch (error) {
        console.error(`Failed to evaluate transaction: ${error}`);
        process.exit(1);
    }
}

exports.queryMyItem = queryMyItem;
exports.queryAllRegistered = queryAllRegistered;
exports.queryAllOrderedItems = queryAllOrderedItems;
exports.queryMyBalance = queryMyBalance;
exports.queryAllItems = queryAllItems;