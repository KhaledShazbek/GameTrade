<?php

function loadresult($item, $limit){
    require "config.inc.php";
    $array = array();
    $query = $con->prepare("SELECT * FROM GamersLogin WHERE 
            usernames LIKE '%" . $item . "%'
            OR Location LIKE '%" . $item . "%'
            ORDER BY id DESC LIMIT $limit");
    $query->execute();
    while($data = $query->fetch(PDO::FETCH_ASSOC)){
        $id = $data['id'];
        $name = $data['usernames'];
        $useremail = $data['emails']; 
        $userpic = $data['ProfilePicture'];
        $usernumber = $data['PhoneNumber'];
        $userlocation = $data['Location'];
        array_push($array, array(
                "id" => $id,
                "KEY_USERNAME" => $name,
                "KEY_EMAIL" => $useremail, 
                "KEY_PHONENUMBER" => $usernumber,
                "KEY_LOCATION" => $userlocation,
                "KEY_PROFILEPICTURE" => $userpic
            )
        );
    }
    echo json_encode($array);
}
    
function loadmoreresults($item, $limit, $lastID){
        require "config.inc.php";
    try{
        $query = $con->prepare("SELECT * FROM GamersLogin WHERE
        id < $lastID
        AND usernames LIKE '%" . $item . "%' 
        OR Location LIKE '%" . $item . "%' 
        ORDER BY id DESC LIMIT $limit");
        $query->execute();
        $array = array(); 
 
        while($data = $query->fetch(PDO::FETCH_ASSOC)){
            $id = $data['id'];
            $name = $data['usernames'];
            $useremail = $data['emails']; 
            $userpic = $data['ProfilePicture'];
            $usernumber = $data['PhoneNumber'];
            $userlocation = $data['Location'];
            array_push($array, array(
                    "id" => $id,
                    "KEY_USERNAME" => $name,
                    "KEY_EMAIL" => $useremail, 
                    "KEY_PHONENUMBER" => $usernumber,
                    "KEY_LOCATION" => $userlocation,
                    "KEY_PROFILEPICTURE" => $userpic
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