<?php
include "config.php";
$userID = $_GET['id'];
$verify = 'true';

$conn = mysqli_connect($servername, $username, $password, $dbname);
$sql = "UPDATE GamersLogin SET isVerified = '" .$verify. "' WHERE id = '".$userID."' ";
$result = mysqli_query($conn, $sql);
if ($result) {
    echo "OK";
}else{
    echo "Something went wrong!";
}

?>