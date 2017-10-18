<?php
error_reporting(E_ALL);

$service_port = 3001;
$address = "127.0.0.1";

$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
if ($socket === false) {
    echo "socket_create() failed: reason: " . socket_strerror(socket_last_error()) . "\n";
} else {
    echo "OK.\n";
}

echo "Attempting to connect to '$address' on port '$service_port'...";
$result = socket_connect($socket, $address, $service_port);
if ($result === false) {
    echo "socket_connect() failed.\nReason: ($result) " . socket_strerror(socket_last_error($socket)) . "\n";
} else {
    echo "OK.\n";
}

$in = 
'{
  "key": "hotelalert",
  "data": {
    "message":"This is an hotel alert!",
    "username": "The General"
  }
}';

if(socket_write($socket, $in, strlen($in)) === false)
{
echo socket_strerror( socket_last_error($socket) );
}

$out = socket_read($socket, 2048);
    echo "Response:" . $out;

?>