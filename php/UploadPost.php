<?php
include "config.php";
header('Content-type=application/json; charset=utf-8');
$conn = mysqli_connect($servername, $username, $password, $dbname);

date_default_timezone_set('America/Los_Angeles');
$date = date('d-m-Y-h:i:sa', time());

$name = $_POST["name"];
$image = $_POST["image"];
$price = $_POST["price"];
$FK = $_POST["FK"];

$decodedImage = base64_decode($image);
$url = "http://shazbekgametrade.000webhostapp.com/images/".$FK."-".$date.".JPG";
$path = "images/".$FK."-".$date.".JPG";

$return = file_put_contents($path, $decodedImage);

if ($return) {
	$SQL = "INSERT INTO Uploads (FK,GameName,Price,Image,ImagePath) 
	VALUES ('".$FK."','".$name."','".$price."','".$url."','".$path."')";
	$result = mysqli_query($conn, $SQL);
	if ($result) {
		echo "OK";
	}else{
		echo "Image upload failed.";
	}

}else{
	echo "Something went wrong!";
}

?>
