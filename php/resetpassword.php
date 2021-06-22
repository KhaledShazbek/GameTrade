<?php
include "config.php";
$name = $_POST['username'];
$pass = $_POST['password'];

$newpass = password_hash($pass, PASSWORD_DEFAULT);
$conn = mysqli_connect($servername, $username, $password, $dbname);
$sql = "UPDATE GamersLogin SET password = '" . $newpass . "' WHERE usernames = '" . $name . "' ";
$result = mysqli_query($conn, $sql);
if ($result) {
	echo "DONE";
}else{
	echo "Something went wrong";
}

?>