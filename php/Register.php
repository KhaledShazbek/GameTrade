<?php
include "config.php";
$user_name = $_GET["username"];
$email = $_GET["email"];
$location = $_GET["location"];
$phone = $_GET["phonenumber"];
$pass = $_GET["password"];
$profilepic = 'null';
$hash = password_hash($pass, PASSWORD_DEFAULT);

$conn = mysqli_connect($servername, $username, $password, $dbname);
$CheckSQL = "SELECT * FROM GamersLogin WHERE usernames='$user_name'";
$check = mysqli_fetch_array(mysqli_query($conn,$CheckSQL));
 if(isset($check)){

 	echo "Username Already Exist, Please Choose Another one.";

 }else{
	$sql = "INSERT INTO GamersLogin (usernames, password, emails, Location, PhoneNumber, ProfilePicture, ProfilePicPath, isVerified) 
	VALUES ('".$user_name."','".$hash."','".$email."','".$location."','".$phone."','".$profilepic."','null','false')";
	$result = mysqli_query($conn,$sql);
	if ($result) {
		$user_id = "SELECT id FROM GamersLogin WHERE usernames= '".$user_name."' ";
		$row = mysqli_fetch_assoc(mysqli_query($conn, $user_id));
		echo $row['id'];
	}else{
		echo "Failed";
	}
}

?>
