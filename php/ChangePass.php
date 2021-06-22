<?php
include "config.php";
$oldpass = $_GET['oldpass'];
$newpass = $_GET['newpass'];
$id = $_GET['id'];

$conn = mysqli_connect($servername, $username, $password, $dbname);
$checkpass = "SELECT password FROM GamersLogin WHERE id = '" . $id . "' ";
$output = mysqli_query($conn, $checkpass);
$row = mysqli_fetch_assoc($output);

if (password_verify($oldpass, $row['password'])) {
	$pass = password_hash($newpass, PASSWORD_DEFAULT);
	$SQL = "UPDATE GamersLogin SET password = '" . $pass . "' WHERE id = '" . $id . "' ";
	$result = mysqli_query($conn, $SQL);
	if ($result) {
		echo "CHANGED";
	}else{
		echo "something went wrong";
	}	
}else{
	echo "Please check your password.";
}

?>