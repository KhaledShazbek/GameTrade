<?php
include "config.php";

date_default_timezone_set('America/Los_Angeles');
$date = date('d-m-Y-h:i:sa', time());
$name = $_POST['NAME'];
$email = $_POST['EMAIL'];
$location = $_POST['LOCATION'];
$phonenumber = $_POST['PHONE'];
$profilepic = $_POST['PROFILEPIC'];
$FK = $_POST['FK'];
$json = array();

$conn = mysqli_connect($servername, $username, $password, $dbname);
$checkUsername = "SELECT * From GamersLogin WHERE usernames = '" . $name . "' AND NOT id = $FK";
$result = mysqli_query($conn, $checkUsername);
$row = mysqli_fetch_array($result);
if ($row) {
	echo "Username already taken!";
}else{
	if ($profilepic != 'null') {
		$deleteprofilepic = "SELECT ProfilePicPath FROM GamersLogin WHERE id = $FK";
		$array = mysqli_fetch_assoc(mysqli_query($conn, $deleteprofilepic));
		if ($array['ProfilePicPath'] != 'null') {
			unlink($array['ProfilePicPath']);
		}
		$decodedImage = base64_decode($profilepic);
		$url = "http://shazbekgametrade.000webhostapp.com/profilepics/".$FK."-".$date.".JPG";
		$path = "profilepics/".$FK."-".$date.".JPG";
		$return = file_put_contents($path, $decodedImage);
		if ($return) {
			$sql = "UPDATE GamersLogin SET usernames = '" . $name . "',
			emails = '" . $email . "', 
			Location = '" . $location . "',
			PhoneNumber = '" . $phonenumber . "',
			ProfilePicture = '" . $url . "',
			ProfilePicPath = '" . $path . "'
			WHERE id = $FK ";
			$update = mysqli_query($conn, $sql);
			if ($update) {
				$json['success'] = 'CHANGED';
				$json['url'] = $url;
				echo json_encode($json);
			}else{
				$json['success'] = "Something went wrong!";
				echo json_encode($json);
			}
		}else{
			$json['success'] = "Something went wrong!";
			echo json_encode($json);
		}
	}else{
		$sql = "UPDATE GamersLogin SET usernames = '" . $name . "',
		emails = '" . $email . "', 
		Location = '" . $location . "',
		PhoneNumber = '" . $phonenumber . "'
		WHERE id = $FK ";
		$update = mysqli_query($conn, $sql);
		if ($update) {
			$json['success'] = 'CHANGED';
			$json['url'] = 'null';
			echo json_encode($json);
		}else{
			$json['success'] = "Something went wrong!";
			echo json_encode($json);
		}
	}
	
}

?>