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
		"myReviews": []

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
	const userPlants = req.body.plants;
	const newPlants = userPlants.push(req.body.newPlant)

	const updateDocument = {
		$set:{
			myPlants: newPlants
		}
	}

	const result = await collection.updateOne(filter, updateDocument);
	const response = [
		{matchedCount : result.matchedCount},
		{modifiedCount: result.modifiedCount}
	]
	res.json(response);
}
app.post("/user/add/:userID", jsonParser, addUserPlant);

async function getPlantByID(req, res){
	collection = database.collection("plants");
	const plantID = req.params.plantID;
	const query = {_id:plantID};
	let plantCursor = await collection.find(query);
	let plants = await plantCursor.toArray();

	const response = plants;
	res.json(response);
}
app.get("/result/plant/:plantID", getPlantByID);

async function getAllPlants(req, res){
	collection = database.collection("plants");
	const query = {};

	let plantCursor = await collection.find(query);
	let plants = await plantCursor.toArray();

	const response = plants;
	res.json(response);
}
app.get('/plants', getAllPlants);

app.listen(5000,function(){
	console.log("Server is running on port 5000");
})