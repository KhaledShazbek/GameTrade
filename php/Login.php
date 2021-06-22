<?php
$name=$_GET["username"];
$pass=$_GET["password"];
include "config.php";
$responce = array();

$conn = mysqli_connect($servername, $username, $password, $dbname);
$sql="SELECT * FROM GamersLogin WHERE usernames='".$name."' ";
$result=mysqli_query($conn,$sql);
$row=mysqli_fetch_assoc($result);
if(password_verify($pass, $row['password']))
{
	$responce['email']=$row["emails"];
	$responce['location']=$row["Location"];
	$responce['phonenumber']=$row["PhoneNumber"];
	$responce['id']=$row["id"];
	$responce['profilepic']=$row['ProfilePicture'];
	$responce['isVerified']=$row['isVerified'];
	echo json_encode($responce);
}
else{
	echo "FAILED";
}
?>