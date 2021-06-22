<?php
include "config.php";
$name = $_GET['username'];

$conn = mysqli_connect($servername, $username, $password, $dbname);
$sql = "SELECT Location, emails, PhoneNumber, ProfilePicture FROM GamersLogin WHERE usernames = '".$name."' ";
$res = mysqli_query($conn, $sql);
$row = mysqli_fetch_assoc($res);
$responce = array();
if($row){
	$responce['success'] = 'true';
	$responce['location'] = $row['Location'];
	$responce['email'] = $row['emails'];
	$responce['phone'] = $row['PhoneNumber'];
	$responce['profilepic'] = $row['ProfilePicture'];
}else{
	$responce['success'] = 'false';
}
echo json_encode($responce);
?>