/*
 * SPDX-License-Identifier: Apache-2.0
 */

'use strict';

const { Wallets } = require('fabric-network');
const FabricCAServices = require('fabric-ca-client');
const fs = require('fs');
const path = require('path');

var registerUser = async function(name) {
    try {

        const ccpPath = path.resolve(__dirname, '..', '..' ,'fabric_project','test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        const ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));

        // Create a new CA client for interacting with the CA.
        const caURL = ccp.certificateAuthorities['ca.org1.example.com'].url;
        const ca = new FabricCAServices(caURL);

        // Create a new file system based wallet for managing identities.
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);


        console.log("register User안에서 ",name);
        // Check to see if we've already enrolled the user.
        const userIdentity = await wallet.get(name);
        if (userIdentity) {
            console.log('registerUser에서 나온 에러, 아이디 존재');
            return;
        }

        // Check to see if we've already enrolled the admin user.
        const adminIdentity = await wallet.get('admin');
        if (!adminIdentity) {
            console.log('An identity for the admin user "admin" does not exist in the wallet');
            console.log('Run the enrollAdmin.js application before retrying');
            return;
        }

        // build a user object for authenticating with the CA
        const provider = wallet.getProviderRegistry().getProvider(adminIdentity.type);
        const adminUser = await provider.getUserContext(adminIdentity, 'admin');

        // Register the user, enroll the user, and import the new identity into the wallet.
        const secret = await ca.register({
            affiliation: 'org1.department1',
            enrollmentID: name,
            role: 'client'
        }, adminUser);
        const enrollment = await ca.enroll({
            enrollmentID: name,
            enrollmentSecret: secret
        });
        const x509Identity = {
            credentials: {
                certificate: enrollment.certificate,
                privateKey: enrollment.key.toBytes(),
            },
            mspId: 'Org1MSP',
            type: 'X.509',
        };

        
        await wallet.put(name, x509Identity);

        // const network = await gateway.getNetwork('mychannel');
        // const contract = network.getContract('fabcar');

        // await contract.submitTransaction('getBalance',name,100);
        // console.log('토큰 전송 완료.');
       
        console.log('Successfully registered and enrolled admin user "appUser" and imported it into the wallet');


    } catch (error) {
        console.error(`Failed to register user "user1": ${error}`);
        process.exit(1);
    }
}

exports.registerUser = registerUser;
