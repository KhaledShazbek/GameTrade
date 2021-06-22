<?php

function loadresult($item, $limit){
    require "config.inc.php";
    $array = array();
	$query = $con->prepare("SELECT * FROM Uploads WHERE 
			GameName LIKE '%" . $item . "%'
			OR Price LIKE '%" . $item . "%'
			ORDER BY id DESC LIMIT $limit");
	$query->execute();
	while($data = $query->fetch(PDO::FETCH_ASSOC)){
		$sql = $con->prepare("SELECT usernames, Location, ProfilePicture FROM GamersLogin WHERE id = '".$data['FK']."' ");
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
	
function loadmoreresults($item, $limit, $lastID){
        require "config.inc.php";
	try{
		$query = $con->prepare("SELECT * FROM Uploads WHERE
		id < $lastID
		AND GameName LIKE '%" . $item . "%' 
		OR Price LIKE '%" . $item . "%' 
		ORDER BY id DESC LIMIT $limit");
		$query->execute();
        $array = array(); 
 
        while($data = $query->fetch(PDO::FETCH_ASSOC)){
            $sql = $con->prepare("SELECT usernames, Location, ProfilePicture FROM GamersLogin WHERE id = '".$data['FK']."' ");
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
	}catch(Exception $e){
		die($e->getmessage());
	}
}

if(isset($_GET['action']) && $_GET['action'] == "loadmore"){
    $lastId = $_GET['lastId'];
    // this is teh limit set in the android java code (LOAD_LIMIT)
	$search = $_GET['search'];
    $limit = $_GET['limit'];
    loadmoreresults($search, $limit, $lastId);
} else {
	$search = $_GET['search'];
    $limit = $_GET['limit'];
    loadresult($search, $limit);
}
?> 