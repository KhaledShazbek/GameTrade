<?php
include "config.php";
$gamerID = $_GET['ID'];

$conn = mysqli_connect($servername, $username, $password, $dbname);

function loadData($limit, $userid){
    require "config.inc.php";
 
    $query = $con->prepare("SELECT * FROM Uploads WHERE FK = $userid ORDER BY id DESC LIMIT $limit "); 
    $query->execute();
    $array = array(); 
 
    while($data = $query->fetch(PDO::FETCH_ASSOC)){
    	$id = $data['id'];
        $gamename = $data['GameName'];
        $price = $data['Price']; 
        $image = $data['Image'];
        array_push($array, array(
        		"id" => $id,
                "KEY_GAMENAME" => $gamename,
                "KEY_PRICE" => $price, 
                "KEY_GAMEPIC" => $image
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
	    	$id = $data['id'];
	        $gamename = $data['GameName'];
	        $price = $data['Price']; 
	        $image = $data['Image'];
	        array_push($array, array(
	                "id" => $id,
	                "KEY_GAMENAME" => $gamename,
	                "KEY_PRICE" => $price, 
	                "KEY_GAMEPIC" => $image,
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
    $limit = $_GET['limit'];
    loadMoreData($lastId, $limit, $gamerID);
} else {
    $limit = $_GET['limit'];
    loadData($limit, $gamerID);
}

?>