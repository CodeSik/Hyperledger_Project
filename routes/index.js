var express = require('express');

var enroll = require('../fabric_js/enrollAdmin.js')
var register = require('../fabric_js/registerUser.js')
var query = require('../fabric_js/query.js')
var invoke = require('../fabric_js/invoke.js')

var router = express.Router();

let user = "default";

/* GET home page. */
router.get('/', async function(req, res, next) {
	console.log('main page')

	if(user == "default"){
		await enroll.enrollAdmin();
		await register.registerUser(user);

		res.render('index', { 
			name: req.cookies.user,
			balance: "로그인되지않음",
			myitems: 0,
			registeredItem: 0,
			saledItem: 0
		});
	}

	else{
		user = req.cookies.user;
		/* GET My Balances */
		
		var resultMyBalance = await query.queryMyBalance(req.cookies.user);
		console.log(resultMyBalance)
		if(resultMyBalance === "0"){
			resultMyBalance = 0
		}
		else{
			var resultMyBalanceobj  = JSON.parse(resultMyBalance);
			console.log("벨런스의 값",resultMyBalanceobj)
			resultMyBalance = resultMyBalanceobj[0].record.balance;	
		}

		/* GET My Items */
		var resultMy = await query.queryMyItem(req.cookies.user);
		var resultMyobj = JSON.parse(resultMy);
		console.log(resultMyobj)
		resultMy = Array();
		console.log(resultMyobj.length)
		for(var i=0 ; i<resultMyobj.length ; i++){
			subResult = Array();
			subResult.push(resultMyobj[i].key)
			subResult.push(resultMyobj[i].record.name)
			subResult.push(resultMyobj[i].record.owner)
			subResult.push(resultMyobj[i].record.price)
			subResult.push(resultMyobj[i].record.state)
			resultMy.push(subResult);
		}
		// console.log(resultMy);

		/* GET Registered Items */
		var resultRegistered = await query.queryAllRegistered(req.cookies.user);
		var resultRegisteredobj = JSON.parse(resultRegistered)
		// console.log(resultRegisteredobj)
		resultRegistered = Array();
		// console.log(resultMyobj.length)

		for(var i=0 ; i<resultRegisteredobj.length ; i++){
			subResult = Array();
			subResult.push(resultRegisteredobj[i].key)
			subResult.push(resultRegisteredobj[i].record.name)
			subResult.push(resultRegisteredobj[i].record.owner)
			subResult.push(resultRegisteredobj[i].record.price)
			subResult.push(resultRegisteredobj[i].record.state)
			resultRegistered.push(subResult);
		}
		// console.log(resultRegistered);

		/* GET Ordered Items */
		var resultSaled = await query.queryAllOrderedItems(req.cookies.user);
		var resultSaledobj = JSON.parse(resultSaled)
		console.log(resultSaledobj)
		resultSaled = Array();
		console.log(resultSaledobj.length)

		for(var i=0 ; i<resultSaledobj.length ; i++){
			subResult = Array();
			subResult.push(resultSaledobj[i].key)
			subResult.push(resultSaledobj[i].record.name)
			subResult.push(resultSaledobj[i].record.owner)
			subResult.push(resultSaledobj[i].record.price)
			subResult.push(resultSaledobj[i].record.state)
			resultSaled.push(subResult);
		}
		console.log(resultSaled);



		res.render('index', { 
			name: req.cookies.user,
			balance: resultMyBalance,
			myitems: resultMy,
			registeredItem: resultRegistered,
			saledItem: resultSaled
		});

	}
});



router.get('/enrollAdmin', async function(req, res, next) {
	await enroll.enrollAdmin();
	res.redirect('/');
})

// router.get('/getMyData', async function(req, res, next) {
// 	console.log('getData')
// 	var resultMy = await query.queryMyItem(req.cookies.user);
// 	var myItems = document.getElementById('myItems');
// 	for(var i=0 ; i < resultMy.length ; i++)
// 	{
// 		var row = myItems.insertRow( myItems.rows.length ); // 하단에 추가
//     	var cell1 = row.insertCell(0);
//     	var cell2 = row.insertCell(1);
// 		var cell3 = row.insertCell(2);
// 		cell1.innerHTML = resultMy[i][0];
// 		cell2.innerHTML = resultMy[i][1];
// 		cell3.innerHTML = resultMy[i][2];

// 		option_value = resultMy[i];
// 		option_text = resultMy[i];
// 		myItemsCategory.append('<option value="'+ option_value +'">'+ option_text +'</option>');

// 	}


// 	res.redirect('/');
// })

router.post('/registerUser', async function(req, res, next) {
	user = req.body.user;
	console.log(user);
	await register.registerUser(user);
	res.cookie('user', user);
	res.redirect('/');
})

router.post('/registerItem', async function(req, res, next) {
	user = req.cookies.user;
	item_name = req.body.item_name;
	price = req.body.price;
	var resultAll = await query.queryAllItems(req.cookies.user);
	var resultAllobj = JSON.parse(resultAll)
	var key = resultAllobj.length;

	console.log(user,item_name,price);
	await invoke.registerItem(key,user,item_name,price);
	res.cookie('user', user);
	res.redirect('/');
})

router.post('/sellMyItem', async function(req, res, next) {
	user = req.cookies.user;
	myItemsCategory = req.body.myItems_category;
	item = String(myItemsCategory).split('/'); //key / itemname / price
	console.log(user,item);
	await invoke.sellMyItem(user,item[0],item[1],item[2]);
	res.cookie('user', user);
	res.redirect('/');
})

router.post('/earnToken', async function(req, res, next) {
	user = req.cookies.user;
	await invoke.earnToken(user);
	res.cookie('user', user);
	res.redirect('/');
})


//final Context ctx, final String name, final String owner, final String newOwner
router.post('/buyUserItem', async function(req, res, next) {
	user = req.cookies.user;
	myItemsCategory = req.body.sale_category;
	item = String(myItemsCategory).split('/'); //key / itemname / owner / price

	await invoke.buyUserItem(item[0],user,item[2],item[1],item[3]);
	res.cookie('user', user);
	res.redirect('/');
})





module.exports = router;
