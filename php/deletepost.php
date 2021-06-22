<?php
include "config.php";
$ID = $_GET['ID'];

$conn = mysqli_connect($servername, $username, $password, $dbname);
$getPath = "SELECT ImagePath fROM Uploads WHERE id = '" . $ID . "' ";
$path = mysqli_fetch_assoc(mysqli_query($conn, $getPath));
$deleteimg = unlink($path['ImagePath']);
if ($deleteimg) {
	$SQL = "DELETE FROM Uploads WHERE id = $ID";
	$result = mysqli_query($conn, $SQL);
	if ($result) {
		echo "DELETED";
	}else{
		echo "FAILED";
	}
}else{
	echo "Something went wrong!";
}
	


?>