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

/*async function connectDB(){
	await client.connect();
	database = client.db("Planted");
	collection = database.collection("plants");

	// add: insertOne or insertMany -> Use to insert new Aesthetics into the db
	const plantDocument = {
		"_id": 9,
		"name": "Green Velvet Alocasia",
		"level": "hard", 
		"images": [
			"1qxF1rHlGmzPGGXW8cgbqt32yqrP3GEtW",
			"1E_edFs4sjaCLLkytxHhgoTKfob09yVqc",
			"1ILTFIK8SH7PxCk63bm2diRgM7WyqfYmr"
		],
		"link": "https://www.amazon.com/s?k=velvet+alocasia&ref=nb_sb_noss",
		"description": "The foliage is why a grower would purchase a Green Velvet Alocasia, and not for the flowers. The leaves look kind of arrow shaped, displaying thick white veins on a dark green velvet leaf surface. Native to South East Asia, this perennial rhizome rooted plant prefers high humidity conditions and warm temperatures.",
		"colors": [ 
			"green",
			"white"
		],
		"light": "indirect bright light",
		"temp": "65-75°F",
		"water": "water thoroughly when top soil becomes dry to the touch",
		"tags": [
			"toxic - dogs",
			"toxic - cats",
			"foliage"
		],
		"reviews": [
			{
				"name": "Olivia", 
				"text": "This plant is great!",
				"rating": 5,
				"images": []

			}		
		],
		"photos": []

	}
	const result = await collection.insertOne(plantDocument);
	console.log(result.insteredId); 
}
async function connectDB(){
	await client.connect();
	database = client.db("Planted");
	collection = database.collection("users");
	const userDocument = {
		"_id": "user_email@email.com",
		"name": "user_name",
		"password": "12345"
		"myPlants": [],
		"myReviews": []
	}
	const result = await collection.insertOne(userDocument);
}

connectDB(); */