<?php
$to      = 'shazbek11@gmail.com';
$subject = 'GameTrade Feedback';
$message = $_POST['message'];

mail($to, $subject, wordwrap($message, 70, "\r\n"));
?>