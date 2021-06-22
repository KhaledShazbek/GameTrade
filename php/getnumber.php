<?php
include "config.php";
$name = $_GET['user'];

$conn = mysqli_connect($servername, $username, $password, $dbname);
$getNumber = "SELECT PhoneNumber FROM GamersLogin WHERE usernames = '" . $name . "' ";
$result = mysqli_query($conn, $getNumber);
if ($result) {
	$array = mysqli_fetch_assoc($result);
	echo $array['PhoneNumber'];	
}else{
	echo "Something went wrong";
}

?>