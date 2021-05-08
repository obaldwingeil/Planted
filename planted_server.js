const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");

const MongoClient = require('mongodb').MongoClient;
const uri = "mongodb+srv://obaldwingeil:baldwin355@cluster0.8nkwr.mongodb.net/Planted?retryWrites=true&w=majority";
const client = new MongoClient(uri, { useUnifiedTopology: true });

const app = express();
const jsonParser = bodyParser.json();
app.use(cors());

let database = null;
let collection = null;

async function connectDB(){
	await client.connect();
	database = client.db("Planted"); 
}
connectDB();

async function addUser(req, res){
	collection = database.collection("users");
	let get_myPlants = req.body.myPlants;
	let myPlants = get_myPlants.substring(1, get_myPlants.length-1).split(", ");

	const userDocument = {
		"_id": req.body._id,
		"name": req.body.name,
		"password": req.body.password,
		"myPlants": myPlants,
		"myReviews": [],
		"myImages" : []

	};
	const result = await collection.insertOne(userDocument);
	const response = [
		{matchedCount : result.matchedCount},
		{modifiedCount: result.modifiedCount}
	]
	res.json(response);
}
app.post("/user/add", jsonParser, addUser);

async function getUserByID(req, res){
	collection = database.collection("users");
	const userID = req.params.userID;
	const query = {_id:userID};
	let userCursor = await collection.find(query);
	let users = await userCursor.toArray();

	const response = users;
	res.json(response);
}
app.get("/result/user/:userID", getUserByID);

async function addUserPlant(req, res){
	collection = database.collection("users");
	const userID = req.params.userID;
	const filter = {_id:userID};
	const query = {_id:userID};
	let userCursor = await collection.find(query);
	let users = await userCursor.toArray();

	// console.log(users);
	if(users.length !== 0){
		const plants = users[0]["myPlants"];

		if(plants.includes(req.body.plant)){
			let index = plants.indexOf(req.body.plant);
			plants.splice(index, 1);
		}
		else{
			plants.push(req.body.plant);
		}

		const updateDocument = {
			$set:{
				myPlants: plants
			}
		}
		const result = await collection.updateOne(filter, updateDocument);

		collection = database.collection("plants");
		const plantName = req.body.plant;
		const filter2 = {name:plantName};
		const query2 = {name:plantName};
		let plantCursor = await collection.find(query2);
		let plantArray = await plantCursor.toArray();

		const plantUsers = plantArray[0]["users"];
		if(plantUsers.includes(userID)){
			let index = plantUsers.indexOf(userID);
			plantUsers.splice(index, 1);
		}
		else{
			plantUsers.push(userID);
		}

		const updateDocument2 = {
			$set:{
				users: plantUsers
			}
		}

		
		const result2 = await collection.updateOne(filter2, updateDocument2);
		const response = [
			{matchedCount : result.matchedCount},
			{modifiedCount: result.modifiedCount},
			{matchedCount : result2.matchedCount},
			{modifiedCount: result2.modifiedCount}
		]
		res.json(response);
	}
}
app.post("/user/add/:userID", jsonParser, addUserPlant);

async function postReview(req, res){
	collection = database.collection("users");
	const userID = req.params.userID;
	const filter = {_id:userID};
	const query = {_id:userID};
	let userCursor = await collection.find(query);
	let users = await userCursor.toArray();

	let images = [];
	// console.log("images: " + req.body.images);
	if(req.body.images !== "[]"){
		images = req.body.images.substring(1, req.body.images.length-1).split(", ");
		// console.log("images after split: " + images);
	}

	let obj = {"name": req.body.name, "text": req.body.text,
	"rating": parseFloat(req.body.rating),
	"images": images,
	"plant_name": req.body.plantName,
	"user_id": userID}; 

	const reviews = users[0]["myReviews"];
	const myImages = users[0]["myImages"];

	reviews.push(req.body.plantName);
	images.forEach(image => {
		myImages.push(image)
	});

	const updateDocument = {
		$set:{
			myReviews: reviews,
			myImages: myImages
		}
	}

	const result = await collection.updateOne(filter, updateDocument);

	collection = database.collection("plants");
	const plantID = req.body.plantID;
	const filter2 = {_id:plantID};
	const query2 = {_id:plantID};
	let plantCursor = await collection.find(query2);
	let plants = await plantCursor.toArray();

	const plantReviews = plants[0]["reviews"];
	plantReviews.push(obj);

	let plantRating = plants[0]["rating"];
	if(req.body.rating !==  0){
		let total = 0;
		plantReviews.forEach(review => {
			total += review["rating"];
		})
		// console.log("total: " + total);
		plantRating = total/plantReviews.length;
	}

	const plantPhotos = plants[0]["photos"];
	images.forEach(image => {
		plantPhotos.push(image)
	});

	const updateDocument2 = {
		$set:{
			rating: plantRating,
			reviews: plantReviews,
			photos: plantPhotos
		}
	}

	const result2 = await collection.updateOne(filter2, updateDocument2);
	const response = [
		{matchedCount : result.matchedCount},
		{modifiedCount: result.modifiedCount},
		{matchedCount : result2.matchedCount},
		{modifiedCount: result2.modifiedCount}
	]
	res.json(response);
}
app.post("/user/post/review/:userID", jsonParser, postReview);

async function getUserPlantsByID(req, res){
	collection = database.collection("users");
	const userID = req.params.userID;
	const query = {_id:userID};
	let userCursor = await collection.find(query);
	let users = await userCursor.toArray();

	const plants = users[0]["myPlants"];
	// console.log("plants: " + plants);
	collection = database.collection("plants");

	if(plants.length === 0){
		const response = [];
		res.json(response);
	}
	else{
		const orQuery = [];
		plants.forEach(item =>{
			orQuery.push({name:item})
		});

		const query2 = {$or : orQuery};

		let plantCursor = await collection.find(query2);
		let plantsObj = await plantCursor.toArray()
		// console.log("Plant Objects: " + plantsObj);

		const response = plantsObj;
		res.json(response);
	}
}
app.get("/result/user/plants/:userID", getUserPlantsByID);

async function getUserReviewsByID(req, res){
	collection = database.collection("users");
	const userID = req.params.userID;
	const query = {_id:userID};
	let userCursor = await collection.find(query);
	let users = await userCursor.toArray();

	const plants = users[0]["myReviews"];
	if(plants.length === 0){
		const response = [];
		res.json(response);
	}
	else{
		// console.log("plants: " + plants);
		collection = database.collection("plants");

		const orQuery = [];
		plants.forEach(item =>{
			orQuery.push({name:item})
		});

		const query2 = {$or : orQuery};

		let plantCursor = await collection.find(query2);
		let plantsObj = await plantCursor.toArray()
		// console.log("Plant Objects: " + plantsObj);

		const reviews = [];
		plantsObj.forEach(plant => {
			// console.log("plant: " + plant);
			plant["reviews"].forEach(review => {
				// console.log("review: " + review);
				if(review.user_id === userID){
					reviews.push(review);
				}
			})
		})


		const response = reviews;
		res.json(response);
	}
	
}
app.get("/result/user/reviews/:userID", getUserReviewsByID);

async function getUserPhotosByID(req, res){
	collection = database.collection("users");
	const userID = req.params.userID;
	const query = {_id:userID};
	let userCursor = await collection.find(query);
	let users = await userCursor.toArray();

	const photos = users[0]["myImages"];
	// console.log("photos: " + photos);

	const response = photos;
	res.json(response);
}
app.get("/result/user/photos/:userID", getUserPhotosByID);

async function getPlantByID(req, res){
	collection = database.collection("plants");
	const plantID = req.params.plantID;
	// console.log("plantID: " + plantID);
	ID = parseInt(plantID);
	const query = {_id:ID};
	let plantCursor = await collection.find(query);
	let plants = await plantCursor.toArray();
	// console.log("plants: " + plants);

	const response = plants;
	res.json(response);
}
app.get("/result/plant/:plantID", getPlantByID);

async function getPlantsByLight(req, res){
	collection = database.collection("plants");
	const light = req.body.light;
	// console.log("light: " + light);
	const query = {light:light};
	let plantCursor = await collection.find(query);
	let plants = await plantCursor.toArray();
	// console.log("plants: " + plants);

	const response = plants;
	res.json(response);
}
app.get("/result/plants/light", jsonParser, getPlantsByLight);

async function getAllPlants(req, res){
	collection = database.collection("plants");
	const query = {};

	let plantCursor = await collection.find(query);
	let plants = await plantCursor.toArray();

	const response = plants;
	res.json(response);
}
app.get('/plants', getAllPlants);

async function getFilteredPlants(req, res){
	collection = database.collection("plants");
	const filters = req.body.filters;
	const sortBy = req.body.sortBy;
	const search = req.body.search;

	let query = {};
	const andQuery = [];
	if(filters.length !== 2){
		let filterArray = filters.substring(1, filters.length-1).split(", ");
		const difOr = [];
		const lightOr = [];
		const tempOr = [];
		filterArray.forEach(item => {
			if(item === "easy" || item === "medium" || item === "hard"){
				// andQuery.push({level:item});
				difOr.push({level:item});
			}
			else if(item.includes("light")){
				lightOr.push({light:item});
			}
			else if(item.includes("55")){
				tempOr.push({tempMin:{$gte: 55, $lte: 65}});
			}
			else if(item.includes("65")){
				tempOr.push({tempMin:{$gte: 65, $lte: 75}});
			}
			else{
				tempOr.push({tempMax:{$gt: 75}});
			}

		});
		if(difOr.length !== 0){
			andQuery.push({$or : difOr});
		}
		if(lightOr.length !== 0){
			andQuery.push({$or : lightOr});
		}
		if(tempOr.length !== 0){
			andQuery.push({$or : tempOr});
		}
		query = {$and : andQuery};
	}

	if(search !== ""){
		searchOr = [];
		let partial = new RegExp(search, 'i');
		searchOr.push({name: partial});
		searchOr.push({description: partial});
		andQuery.push({$or : searchOr});

		query = {$and : andQuery};
		// console.log(searchOr);
	}

	let plantCursor = await collection.find(query);
	let plants = await plantCursor.toArray();

	// console.log("plants: " + typeof plants);
	// console.log("plants.toString(): " + typeof plants.toString());
	let plantJson = JSON.parse(JSON.stringify(plants));
	// console.log(plantJson);

	// console.log("sortBy: " + sortBy);
	let rep = [];
	if(sortBy === "Alphabetical"){
		rep = plants;
	}
	else if(sortBy === "Rating: High to Low"){
		plantJson.sort((a,b) => parseFloat(b.rating) - parseFloat(a.rating));
		rep = plantJson;
	}
	else{
		let easy = [];
		let medium = [];
		let hard = [];
		plantJson.forEach(plant => {
			if(plant.level === "easy"){
				easy.push(plant);
			}
			else if(plant.level === "medium"){
				medium.push(plant);
			}
			else{
				hard.push(plant);
			}
		})
		let sorted = easy.concat(medium, hard);
		rep = sorted;
	}

	const response = rep;
	res.json(response);
}
app.get('/plants/filter', jsonParser, getFilteredPlants);

app.listen(5000,function(){
	console.log("Server is running on port 5000");
})