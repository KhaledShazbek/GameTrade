<?php
include "config.php";
$name = $_GET['username'];

$conn = mysqli_connect($servername, $username, $password, $dbname);
$id = "SELECT id FROM GamersLogin WHERE usernames = '".$name."' ";
$query = mysqli_query($conn, $id);
$row = mysqli_fetch_assoc($query);

function loadData($limit, $userid){
    require "config.inc.php";
 
    $query = $con->prepare("SELECT * FROM Uploads WHERE FK = $userid ORDER BY id DESC LIMIT $limit "); 
    $query->execute();
    $array = array(); 
 
    while($data = $query->fetch(PDO::FETCH_ASSOC)){
    	$sql = $con->prepare("SELECT * FROM GamersLogin WHERE id = '".$data['FK']."' ");
    	$sql->execute();
    	$userinfo = $sql->fetch(PDO::FETCH_ASSOC);
    	$name = $userinfo['usernames'];
    	$location = $userinfo['Location'];
    	$userpic = $userinfo['ProfilePicture'];
    	$id = $data['id'];
        $gamename = $data['GameName'];
        $price = $data['Price']; 
        $image = $data['Image'];
        array_push($array, array(
        		"id" => $id,
                "KEY_GAMENAME" => $gamename,
                "KEY_PRICE" => $price, 
                "KEY_GAMEPIC" => $image,
                "KEY_USERNAME" => $name,
                "KEY_LOCATION" => $location,
                "KEY_USERPIC" => $userpic
            )
        );
}
 
echo json_encode($array); 
}

function loadMoreData($lastId, $limit, $userid){
    require "config.inc.php";
    try{
        $query = $con->prepare("SELECT * FROM Uploads WHERE FK = $userid AND id < $lastId ORDER BY id DESC LIMIT $limit "); 
        $query->execute();
        $array = array(); 
 
        while($data = $query->fetch(PDO::FETCH_ASSOC)){
            $sql = $con->prepare("SELECT * FROM GamersLogin WHERE id = $userid ");
	    	$sql->execute();
	    	$userinfo = $sql->fetch(PDO::FETCH_ASSOC);
	    	$name = $userinfo['usernames'];
	    	$location = $userinfo['Location'];
	    	$userpic = $userinfo['ProfilePicture'];
	    	$id = $data['id'];
	        $gamename = $data['GameName'];
	        $price = $data['Price']; 
	        $image = $data['Image'];
	        array_push($array, array(
	                "id" => $id,
	                "KEY_GAMENAME" => $gamename,
	                "KEY_PRICE" => $price, 
	                "KEY_GAMEPIC" => $image,
	                "KEY_USERNAME" => $name,
	                "KEY_LOCATION" => $location,
	                "KEY_USERPIC" => $userpic
	            )
            );
        }
 
        echo json_encode($array);
    } catch(Exception $e){
        die($e->getMessage());
    }
}
 
if(isset($_GET['action']) && $_GET['action'] == "loadmore"){
    $lastId = $_GET['lastId'];
    // this is teh limit set in the android java code (LOAD_LIMIT)
    $limit = $_GET['limit'];
    loadMoreData($lastId, $limit, $row['id']);
} else {
    $limit = $_GET['limit'];
    loadData($limit, $row['id']);
}

?>